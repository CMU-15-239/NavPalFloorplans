#author: Zhiyu Wang
#Email: zhiyuw@andrew.cmu.edu
########################################
#Utility functions
########################################

from PIL import Image
import cv2
import sys
from merge import *
from extractLine import *
from classes import *
from OCR import *
#import urllib2
#from multiprocessing import Pool
from time import clock
#####################################################
#Image Manipulation 
#####################################################


def checkBackgroundColor(IMG):
    w=IMG.width
    h=IMG.height

    if IMG.rgbs[0][0]>=127 or (127,127,127): return "white"
    else: return "black"

#Summary: color the lines passed as inputs with green
#Arguements: (list of hlines, list of vlines, image object)
#Output: NONE

def colorLines(hlines,vlines,IMG):
    for line in hlines:
        row=line.start.row
        cols=line.start.col
        cole=line.end.col
        for i in xrange(cols,cole+1):
            IMG.rgbs[row][i]=(124,252,0)
    
    for line in vlines:
        col=line.start.col
        rows=line.start.row
        rowe=line.end.row
        for j in xrange(rows,rowe+1):
            IMG.rgbs[j][col]=(124,252,0)


#extractLine: extract lines from the image
#input: image object
#output: a list of horizontal and vertical lines
def extractLines(IMG):
    hlines=[]
    vlines=[]
    process_horizontal(IMG,hlines)
    process_vertical(IMG,vlines)
    return (hlines,vlines)


#gray2rgb: convert grayScale format to RGB format
#input: image with grayscale format
#output: image with RGB format
def grayToRgb(IMG):
    rgbs=IMG.rgbs
    for i in xrange(len(rgbs)):
        for j in xrange(len(rgbs[0])):
            pixel=rgbs[i][j]
            IMG.rgbs[i][j]=(pixel,pixel,pixel)
    return

#Utility functions
#oneToMulti: convert 1d list to 2d list, 
#input: (list, length of each sub-list)
def oneToMulti(l,width):
    return [l[i:i+width] for i in xrange(0,len(l),width)]


#removeLines: remove the lines from the original image
#input: (image object, list of vlines, list of hlines)
#output: NONE
def removeLines(IMG,vlines,hlines): 
    #reverseColor(IMG)
    for line in hlines:
        row=line.start.row
        cols=line.start.col
        cole=line.end.col
        for i in xrange(cols, cole+1):
            # color lines black to "remove them"
            IMG.rgbs[row][i]=0
    for line in vlines:
        col=line.start.col
        rows=line.start.row
        rowe=line.end.row
        for j in xrange(rows,rowe+1):
            IMG.rgbs[j][col]=0


#reverseColor: reverse the color of the image
#input:  image
#output: image with reversed color
def reverseColor(IMG):
    rgbs=IMG.rgbs
    for i in xrange(len(rgbs)):
        for j in xrange(len(rgbs[0])):
            pixel=rgbs[i][j]
            # IMG is greyscale format
            if type(pixel)==int :
                IMG.rgbs[i][j]=255-pixel
            # IMG is RGB format
            else:IMG.rgbs[i][j]=(255-pixel[0],255-pixel[1],255-pixel[2])
    return IMG    

#fail points
def saveImage(imagedir,IMG):
    im=np.array(np.uint8(IMG.rgbs))
    cv2.imwrite(imagedir,im)
    return im

#saveRemoveLines: removeLines and save the image without the lines
#input: (image object, list of vlines, list of hlines)
#output: NONE
def saveRemoveLines(IMG,vlines,hlines):
    removeLines(IMG,vlines,hlines)
    #create a new GreyScale and save it for text removal
    pixels=[]
    for img_line in IMG.rgbs:
        pixels+=img_line
    
    imSize=(IMG.width, IMG.height)
    im2=Image.new('L', imSize)
    im2.putdata(pixels)
    # more fail points
    im2.save("temp.png","png")


#visualizeLines: color the extracted lines for visualization
#input: (image object, list of vlines, list of hlines)
#output: NONE
def visualizeLines(IMG,vlines,hlines):
    grayToRgb(IMG)
    colorLines(hlines,vlines,IMG)
    pixels=[]
    for img_line in IMG.rgbs:
        pixels+=img_line
   
    #create a new image using PIL library function to
    #visualize the effect of line extraction
    imSize=(IMG.width,IMG.height)
    im2= Image.new('RGB',imSize)
    im2.putdata(pixels)
    im2.show()



def ExtractDoors(im, sourcepath):
    im=cv2.cvtColor(im,cv2.COLOR_BGR2GRAY) 
    img=cv2.threshold(im,5,255,cv2.THRESH_BINARY)[1]
    image=cv2.cvtColor(img,cv2.COLOR_GRAY2BGR)
    contours,hierarchy=cv2.findContours(img,cv2.RETR_TREE,\
                                    cv2.CHAIN_APPROX_SIMPLE)
    bboxs=[]
    for cc in contours:
        (x,y,w,h)=cv2.boundingRect(cc)
        if (abs(w-h)<2 and w>10 and h>10):
            bboxs.append((x,y,w,h))
            cv2.rectangle(image,(x,y),(x+w,y+h),(0,255,0),1)
    #cv2.imshow("doors",image)
    #cv2.waitKey(0)
    return bboxs

def extractDoors(img,hlines,vlines,doorRects):
    for bbox in doorRects:
        (x,y,w,h)=bbox
        if x>=2100 or y>=1275: 
            print "messy"
            return
        probe=w/2+5
        cx=x+w/2 
        cy=y+h/2
        counts=[0,0,0,0]
        
        cv2.line(img,(cx,cy),(cx-probe,cy),255,2)
        cv2.line(img,(cx,cy),(cx,cy+probe),255,2)        
        cv2.line(img,(cx,cy),(cx+probe,cy),255,2)
        cv2.line(img,(cx,cy),(cx,cy-probe),255,2)
        
        temp=cx
        cx=cy
        cy=cx
 
        for i in xrange(probe):
            if img[cx-i][cy]>127: counts[0]=-1
            if img[cx][cy+i]>127: counts[1]=-1
            if img[cx+i][cy]>127: counts[2]=-1
            if img[cx][cy-i]>127: counts[3]=-1
        
        if counts[0]==0:
            line=newLine(y,x,y,x+w)
            hlines.append(line)
        if counts[1]==0:
            line=newLine(y,x,y+h,x+w)
            vlines.append(line)
        if counts[2]==0:
            line=newLine(y+h,x,y+h,x+w)
            hlines.append(line)
        if counts[3]==0:
            line=newLine(y,x,y+h,x)
            vlines.append(line)
        else: print "WTF! that can't happen!"    
    #cv2.imshow("img",img)
    #cv2.waitKey(0) 




#I do not know what it does, if nobody calls it, I am going to delete it.
def generate_string(hlines,vlines):
    JSON='{\"lines\":[\n'
    for line in hlines:
        JSON+=('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]},\n' %\
                (line.start.row, line.start.col,line.end.row,\
                        line.end.col))
    for line in vlines:
        JSON+=('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]},\n' %\
                (line.start.row, line.start.col,line.end.row,\
                        line.end.col))
    JSON=JSON[:-2]
    JSON+=']}\n'
    print JSON


#parse_input_file: take imagePath and set up a object representing the image
#input: image path
#output: image object
def parseInputFile(image_dir):
    img=Image.open(image_dir)
    #convert image to grayscale
    img=img.convert('L')
    #extract the pixels
    list_of_pixels=list(img.getdata())
    #extract image size
    (width,height)=img.size
    #initialize IMG object
    IMG=FImage()
    IMG.width=width
    IMG.height=height
    IMG.rgbs=oneToMulti(list_of_pixels,IMG.width)
    if checkBackgroundColor(IMG)=="black":reverseColor(IMG)
    return IMG


#writeVertexList: write the lines into json file
#input: (hlines, vlines)
#output: json file with lines written into it
def writeVertexList(hlines,vlines,destpath):
    
    
    fd=open(destpath,'w')

    fd.write('{\"lines\":[\n')

    #write the line segments into a json file for canvas 
    for line in hlines:
        fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]},\n' %\
        (line.start.row, line.start.col,line.end.row,\
        line.end.col))
    for i in xrange(len(vlines)-1):
        line=vlines[i]
        fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]},\n' %\
            (line.start.row, line.start.col, line.end.row,\
                line.end.col))
    line=vlines[len(vlines)-1]
    fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}\n' %\
                (line.start.row, line.start.col, line.end.row,\
                 line.end.col))
    fd.write("]\n")

    fd.close()
    
