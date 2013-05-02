#author: Zhiyu Wang
########################################
#Utility functions
########################################

from PIL import Image
import cv2
import sys
from constant import *
from merge import *
from extractLine import *
from classes import *
from OCR import *
from matching import *
#import urllib2
#from multiprocessing import Pool
from time import clock

#####################################################
#Image Manipulation 
#####################################################

#Summary: a crude function that detects the background color of the floorplan
#Input: Image object
#output: color string
def checkBackgroundColor(IMG):
    w=IMG.width
    h=IMG.height
    if IMG.rgb[0][0] >= bwThresh or rgbThresh: return "white"
    else: return "black"

#Summary: color the lines passed as inputs with green
#Input: (list of horizontal lines, list of vertical lines, image object)
#Output: NONE
def colorLines(hLines,vLines,IMG):
    for line in hLines:
        row=line.start.row
        cols=line.start.col #cols: starting column number
        cole=line.end.col   #cole: ending column number
        for i in xrange(cols,cole+1):
            IMG.rgb[row][i]=green
    
    for line in vLines:
        col=line.start.col
        rows=line.start.row #rows: starting row number
        rowe=line.end.row   #rowe: ending row number
        for j in xrange(rows,rowe+1):
            IMG.rgb[j][col]=green


#Summary: extract lines from the image
#Input: image object
#Output: a list of horizontal and vertical lines
def extractLines(IMG):
    hLines=[]
    vLines=[]
    processHorizontal(IMG,hLines)
    processVertical(IMG,vLines)
    return (hLines,vLines)


#gray2rgb: convert grayScale format to RGB format
#Input: image with grayscale format
#Output: image with RGB format
def grayToRgb(IMG):
    rgb=IMG.rgb
    for i in xrange(len(rgb)):
        for j in xrange(len(rgb[0])):
            intensity=rgb[i][j]
            IMG.rgb[i][j]=(intensity,intensity,intensity)
    return

#Utility functions
#oneToMulti: convert 1d list to 2d list, 
#Input: (list, length of each sub-list)
#Output: 2d list of lists of length of input number
def oneToMulti(l,width):
    return [l[i:i+width] for i in xrange(0,len(l),width)]


#removeLines: remove the lines from the original image
#input: (image object, list of vLines, list of hLines)
#output: NONE
def removeLines(IMG,vLines,hLines): 
    for line in hLines:
        row=line.start.row
        cols=line.start.col #starting column number
        cole=line.end.col #ending column number
        for i in xrange(cols, cole+1):
            # color lines black to "remove them"
            IMG.rgb[row][i]=0
    for line in vLines:
        col=line.start.col
        rows=line.start.row
        rowe=line.end.row
        for j in xrange(rows,rowe+1):
            IMG.rgb[j][col]=0


#reverseColor: reverse the color of the image
#input:  image
#output: image with reversed color
def reverseColor(IMG):
    rgb=IMG.rgb
    for i in xrange(len(rgb)):
        for j in xrange(len(rgb[0])):
            pixel=rgb[i][j]
            # IMG is greyscale format
            if type(pixel)==int :
                IMG.rgb[i][j]=255-pixel
            # IMG is RGB format
            else:IMG.rgb[i][j]=(255-pixel[0],255-pixel[1],255-pixel[2])
    return IMG    

#Summary:save opencv2-format image object into specified path
#Input: imagepath, IMG object
#Output: opencv2-format image object
def saveImage(imagedir,IMG):
    img=np.array(np.uint8(IMG.rgb))
    cv2.imwrite(imagedir,img)
    return img

#saveRemoveLines: removeLines and save the image without the lines
#input: (sourcePath,image object, list of vLines, list of hLines)
#output: NONE
def saveRemoveLines(sourcePath,IMG,vLines,hLines):
    removeLines(IMG,vLines,hLines)
    #create a new GreyScale and save it for text removal
    #pixels=[]
    #for imgLine in IMG.rgb:
     #   pixels+=imgLine
    img=np.array(np.uint8(IMG.rgb))
    return img
    #imSize=(IMG.width, IMG.height)
    #im2=Image.new('L', imSize)
    #im2.putdata(pixels)
    #im2.save('temp.png','png')


#visualizeLines: color the extracted lines for visualization
#input: (image object, list of vLines, list of hLines)
#output: NONE
def visualizeLines(IMG,vLines,hLines):
    grayToRgb(IMG)
    colorLines(hLines,vLines,IMG)
    pixels=[]
    for imgLine in IMG.rgb:
        pixels+=imgLine
    #create a new image using PIL library function to
    #visualize the effect of line extraction
    imSize=(IMG.width,IMG.height)
    im2= Image.new('RGB',imSize)
    im2.putdata(pixels)
    im2.save("lineExtration.png",'png')
    im2.show()


#Summary:
#1. First find all the connected components in the image
#2. Find connected components in approxiately rectangular shape 
#3. Save the location of the bounding box into a list
#Input: image object, source image path
#Output: list of positions of bounding boxes
def ExtractDoors(img, sourcepath):
   # bwImg=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY) 
    bwImg=cv2.threshold(img,5,255,cv2.THRESH_BINARY)[1]
    rgbImage=cv2.cvtColor(bwImg,cv2.COLOR_GRAY2BGR)
    #find all the connected components in the floorplan
    contours,hierarchy=cv2.findContours(bwImg,cv2.RETR_TREE,\
                                    cv2.CHAIN_APPROX_SIMPLE)
    bboxs=[]
    for cc in contours:
        (x,y,w,h)=cv2.boundingRect(cc)
        if (abs(w-h)<2 and doorWidthMin<w<doorWidthMax and h>doorHeightMin):
            bboxs.append((x,y,w,h))
            cv2.rectangle(rgbImage,(x,y),(x+w,y+h),(0,255,0),1)
    #below code are for visualization purpose
    #cv2.imshow("doors",image)
    #cv2.imwrite("map/doors.png",image)
    #cv2.waitKey(0)
    return bboxs


#Summary: Use template matching method to obtain the rectangular bounding boxes           for doors
#Input: opencv2-formated image object
#Output: list of positions of bounding boxes
def obtainRects(img):
    return templateMatching(img)



#Summary: Detect the position of the doors and save them into json file
#Input: image object, horizontal lines, vertical lines, list of 
#       rectangular bounding boxes, destination path
#Output: None
def extractDoors(img,hLines,vLines,doorRects,destpath):
    fd=open(destpath,'w')
    fd.write('{\"door\":[')
    addcomma=False
    for bbox in doorRects:
        (x,y,w,h)=bbox
        imgHeight,imgWidth= img.shape[:2]
        if x>=imgWidth or y>=imgHeight: 
            print "Exceed Image Dimension."
            return
        probe=w/2+5
        cx=x+w/2 #cx: center x coordinate 
        cy=y+h/2 #cy: center y coordinate
        counts=[0,0,0,0]

        #Opencv2 refers pixel in cartesian notation, i.e x=col, y=row 
        temp=cx
        cx=cy
        cy=temp
        for i in xrange(probe):
            if img[cx-i][cy]>bwThresh:counts[0]=-1
            if img[cx][cy+i]>bwThresh:counts[1]=-1
            if img[cx+i][cy]>bwThresh:counts[2]=-1
            if img[cx][cy-i]>bwThresh:counts[3]=-1
        
        s=',' if addcomma==True else ' '   
    
        if counts[0]==0:
            line=newLine(y,x,y,x+w)
            cv2.line(img,(x,y),(x+w,y),255,2)
            fd.write('%s\n {\"door\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}'%\
                        (s,y,x,y+h,x))
            addcomma=True
            hLines.append(line)
        elif counts[1]==0:
            line=newLine(y,x+w,y+h,x+w)
            cv2.line(img,(x+w,y),(x+w,y+h),255,2)
            fd.write('%s\n {\"door\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}'%\
                        (s,y+h,x,y+h,x+w))
            addcomma=True
            vLines.append(line)
        elif counts[2]==0:
            line=newLine(y+h,x,y+h,x+w)
            cv2.line(img,(x,y+h),(x+w,y+h),255,2)
            fd.write('%s \n {\"door\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}'%\
                        (s,y,x+w,y+h,x+w))
            hLines.append(line)
            addcomma=True
        elif counts[3]==0:
            line=newLine(y,x,y+h,x)
            cv2.line(img,(x,y),(x,y+h),255,2)
            fd.write('%s \n{\"door\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}'%\
                        (s,y,x,y,x+w))
            vLines.append(line)
            addcomma=True
        else:print "not a door"
    
    fd.write("]\n")
    fd.close()
    #for visualization purpose
    #cv2.waitKey(0) 


#parse_input_file: take imagePath and set up a object representing the image
#input: image path
#output: image object
def parseInputFile(image_dir):
    img=Image.open(image_dir)
    #convert image to grayscale
    img=img.convert('L')
    #extract the pixels
    pixelArray=list(img.getdata())
    #extract image size
    (width,height)=img.size
    #initialize IMG object
    IMG=FImage()
    IMG.width=width
    IMG.height=height
    IMG.rgb=oneToMulti(pixelArray,IMG.width)
    if checkBackgroundColor(IMG)=="black":reverseColor(IMG)
    return IMG


#writeVertexList: write the lines into json file
#input: (hLines, vLines)
#output: json file with lines written into it
def writeVertexList(hLines,vLines,destpath):
    fd=open(destpath,'a')

    fd.write(',\"lines\":[\n')

    #write the line segments into a json file for canvas 
    for line in hLines:
        fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]},\n' %\
        (line.start.row, line.start.col,line.end.row,\
        line.end.col))
    for i in xrange(len(vLines)-1):
        line=vLines[i]
        fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]},\n' %\
            (line.start.row, line.start.col, line.end.row,\
                line.end.col))
    line=vLines[len(vLines)-1]
    fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}\n' %\
                (line.start.row, line.start.col, line.end.row,\
                 line.end.col))
    fd.write("]\n")

    fd.close()
    
