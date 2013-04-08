#Author: Zhiyu Wang
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
            IMG.rgbs[i][j]=(255-pixel,255-pixel,255-pixel)
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
    reverseColor(IMG)
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
    image=Image.new('L',(IMG.width,IMG.height))
    pixels=[]
    for img_line in IMG.rgbs:
        pixels+=img_line
    print IMG.rgbs[0][0]
    image.putdata(IMG.rgbs)
    image.save(imagedir+'.png','png')


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

#######################################################
#File Manipulation Utility functions
#######################################################





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
    fd.write("],\n")

    fd.close()
    
