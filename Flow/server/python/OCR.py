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

#epsilon value used to expland the margin of the bounding box
#that bounded connected components
ep=5

#dilate: maphological dilation operation utility function
#input: (image,dilate level)
#output: Dilated image

def dilate(img,dilationSize):
    dilationSize=2*dilationSize+1
    kernel=cv2.getStructuringElement(cv2.MORPH_ELLIPSE,\
                                    (dilationSize,dilationSize))
    dilated=cv2.dilate(img,kernel)
    #cv2.imshow('dilation demo',dilated)
    #cv2.waitKey(0)
    return dilated

#fCC: find all connected components in the image
#input: image
#output: a list of the connected components
def findConnectedComponents(im):
    imgray=cv2.cvtColor(im, cv2.COLOR_BGR2GRAY)
    ret,thresh=cv2.threshold(imgray, 127,255,0)
    #contours store a list of the connected components identified
    contours,hierarchy=cv2.findContours(thresh,cv2.RETR_TREE,\
                                        cv2.CHAIN_APPROX_SIMPLE)
    return contours

#visualizeCC: draw rectangle boxes around all connected components
def visualizeCC(contours):
    cv2.drawContours(im,contours, -1,(0,255,0),-1)
    cv2.waitKey(0)

#getBoundingBoxes:extracts the locations of the bounding box (margin adjusted)
#input: image
def getBoundingBoxes(im,contours):
    bboxs=[]
    for cc in contours:
        (x,y,w,h)=cv2.boundingRect(cc)
        if (15<w<90 and 2<h<20):
            bboxs.append((x,y,w,h))
            cv2.rectangle(im,(x-ep,y-ep),(x+w+2*ep,y+h+2*ep),(0,255,0),2)    
    #cv2.imshow("testing",im)

    return bboxs

#getText does three things:
#1. Use the locations of the bounding box of the texts to extract the 
    #rectangle part of the original floor plan image
#2. Pass the image to OCR(Tesseract) for identification
#3. Write them to a txt file, namely "preText.txt"

def getText(image0):
    #thicken the border in order to make tesseract feel happy
    offset=20
    height,width,channel=image0.shape
    image1=cv2.copyMakeBorder(image0, offset, offset,offset,offset,\
                            cv2.BORDER_CONSTANT,value=(255,255,255))
    
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
def OCR(im, imageDir,destpath):
    im=dilate(im,2)
    connComponents=findConnectedComponents(im)
    bboxs=getBoundingBoxes(im,connComponents)
    originalImage=cv2.imread(imageDir)
    textList=[]
    fd=open(destpath,'a')
    count=0
    if len(bboxs)<1:
        print "nothing detected,mission aborted"
        return
    else:
        fd.write('\"text\":[\n')
        first=1
        for (x,y,w,h) in bboxs:
            center=(x+w/2.0, y+h/2.0)
            #extract the rectangle region of the original floor plan image
            patch=cv2.getRectSubPix(originalImage,(w,h),center)
            patch=cv2.resize(patch, (int(w*3), int(h*3)))
            #extract texts
            text,conf=getText(patch)
            #filter certain text extractions based on the confidence level
            i=text.find("\n")
            if (conf>70 and 47< ord(text[0])<58):
                if first==0:
                    fd.write(',\n{\"value\": \"%s\", \"point\": [%d,%d]}'\
                            %(text[:i],x,y))
                else:
                    fd.write('{\"value\": \"%s\", \"point\": [%d,%d]}'\
                            %(text[:i],x,y))
                    first=0
                    
                count+=1

    fd.write("]\n")
    fd.write("}")
    fd.close()
    print "total number of texts identified=%s\n"% count


    

    
    

