#Author: Zhiyu Wang
#Content summary:
    #Contour.py takes in a floor plan that is already maphorlogically dilated and use cv2.findContour function to extract all the connected components, pass them to OCR(namely Tesseract) and return all the texts identified.
    #1.Extract Connected Compomennt
    #2 Pass to OCR for identificaiton

import sys
import tesseract
import numpy as np
import cv2
import cv2.cv as cv
from constant import *

#dilate: maphological dilation operation utility function
#        http://en.wikipedia.org/wiki/Dilation_%28morphology%29
#Input: (image,dilate level)
#Output: Dilated image
#Adapted from https://github.com/abidrahmank/OpenCV2-Python/blob/master/Official_Tutorial_Python_Codes/3_imgproc/morphology_1.py 
def dilate(img,dilationSize):
    dilationSize=2*dilationSize+1
    kernel=cv2.getStructuringElement(cv2.MORPH_ELLIPSE,\
                                    (dilationSize,dilationSize))
    dilated=cv2.dilate(img,kernel)
    return dilated

#Summary: find all connected components in the image
#Input: image object
#Output: a list of the connected components
#Adapted from https://github.com/abidrahmank/OpenCV2-Python/blob/master/Official_Tutorial_Python_Codes/3_imgproc/findcontours.py
def findConnectedComponents(im):
    #imgray=cv2.cvtColor(im, cv2.COLOR_BGR2GRAY)
    ret,thresh=cv2.threshold(im, bwThresh,white,0)
    #contours store a list of the connected components identified
    contours,hierarchy=cv2.findContours(thresh,cv2.RETR_TREE,\
                                        cv2.CHAIN_APPROX_SIMPLE)
    return contours

#visualizeCC: draw rectangle boxes around all connected components
#Input: list of boundaries of connected components
#Output: Visualization
def visualizeCC(contours):
    cv2.drawContours(im,contours, -1,green,-1)
    cv2.waitKey(0)

#getBoundingBoxes:extracts the locations of the bounding box (margin adjusted)
#Input: image object, list of boundaries of connected components
#Output: list of bounding rectangles
def getBoundingBoxes(im,contours):
    bboxs=[]
    for cc in contours:
        (x,y,width,height)=cv2.boundingRect(cc)
        w,h = width,height
        if (textWidthMin<w<textWidthMax and textHeightMin<h<textHeightMax):
            bboxs.append((x,y,w,h))
            cv2.rectangle(im,(x-ep,y-ep),(x+w+2*ep,y+h+2*ep),green,2)    
    #cv2.imshow("testing",im)
    return bboxs

#getText does three things:
#1. Use the locations of the bounding box of the texts to extract the 
    #rectangle part of the original floor plan image
#2. Pass the image to OCR(Tesseract) for identification
#3. Write them to a txt file, namely "preText.txt"
#Adapted from http://code.google.com/p/python-tesseract/
def getText(image0):
    #thicken the border in order to make tesseract feel happy
    offset=textOffset
    height,width,channel=image0.shape
    image1=cv2.copyMakeBorder(image0, offset, offset,offset,offset,\
                            cv2.BORDER_CONSTANT,value=rgbWhite)
    
    #set up tesseract API 
    api=tesseract.TessBaseAPI()
    api.Init(".","eng",tesseract.OEM_DEFAULT)

    api.SetPageSegMode(tesseract.PSM_AUTO)
    height1,width1,channel1=image1.shape

    #star text extraction
    cvmat_image=cv.fromarray(image1)
    iplimage=cv.GetImage(cvmat_image)
    #extract texts
    tesseract.SetCvImage(iplimage,api)
    text=api.GetUTF8Text()
    conf=api.MeanTextConf()
    return text,conf  


#OCR:extract texts and save the texts as strings into a txt file
#input: image
#output: txt file containing all extracted txts     
def OCR(img, imageDir,destpath):
    #perform mahorlogical dilation on the floor plan
    img=dilate(img,2)
    #detect and extracted all connected components
    connComponents=findConnectedComponents(img)
    bboxs=getBoundingBoxes(img,connComponents)
    #extract the subimage of the connected components from original image
    originalImage=cv2.imread(imageDir)
    textList=[]
    fd=open(destpath,'a')
    count=0
    fd.write(',\"text\":[\n')
    if len(bboxs)<1:
        print "nothing detected,mission aborted"
    else:
        first=1
        for (x,y,w,h) in bboxs:
            center=(x+w/2.0, y+h/2.0)
            #extract the rectangle region of the original floor plan image
            patch=cv2.getRectSubPix(originalImage,(w,h),center)
            patch=cv2.resize(patch, (int(w*resize), int(h*resize)))
            #extract texts
            text,conf=getText(patch)
            #filter certain text
            #extractions based on the confidence level
            i=text.find("\n")
            if (conf>confidenceLevel and 
                    validCharStart< ord(text[0])<validCharEnd):
                if first==0:
                    fd.write(',\n{\"value\": \"%s\", \"point\": [%d,%d]}'\
                            %(text[:i],x,y))
                else:
                    fd.write('{\"value\": \"%s\", \"point\": [%d,%d]}'\
                            %(text[:i],x,y))
                    first=0
                    
                count+=1
    fd.write("]")
    fd.write("}")
    fd.close()
    print "total number of texts identified=%s\n"% count


    

    
    

