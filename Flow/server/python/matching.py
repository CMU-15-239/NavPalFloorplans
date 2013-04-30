#ZHIYU WANG
###########################################################
#Template Matching:
#What it does: 
#Given original image and template image, extract a list of bounding rectangles of the subimages matching the template image in the original image
#This is used in NavPal floor plan to detect the door objects.

#########################################################3
import cv2
import numpy as np
from constant import *

#Summary: Use pattern matching to obtain the bounding rectangles of the 
#         subimages matching the template image
#Input: image object
#Output: list of bounding rectangles
def templateMatching(img):
    bboxes=[]
    #thresholding
    #img=cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    img=cv2.threshold(img,bthresh,white,cv2.THRESH_BINARY)[1]
    img=cv2.cvtColor(img,cv2.COLOR_GRAY2BGR)
    img2=img.copy()
    t1 = cv2.imread('template.png')
    t2 =cv2.imread('template 2.png')
    t3 =cv2.imread('template 3.png')
    t4 =cv2.imread('template 1.png')
    templateList=[t1,t2,t3,t4]
    for i in xrange(len(templateList)):
        template=templateList[i]
        #obtain dimension of templates
        trows,tcols = template.shape[:2] # template rows and cols
        bboxes+=match(1,img,template,img2,tcols,trows)
    #cv2.imwrite('result.png',img2)
    return bboxes

#Summary: Template matching implementation
#Input: matchvalue, image object, template object, visualized image object,
#       image width, image height
#Output: list of bounding rectangles
#Adapted from https://github.com/abidrahmank/OpenCV2-Python/blob/master/Official_Tutorial_Python_Codes/3_imgproc/templatematching.py
def match(matchvalue,img,template,img2,tcols,trows):
    rects=[]
    matches = cv2.matchTemplate(img,template,matchvalue)
    cv2.normalize(matches,matches,0,white,cv2.NORM_MINMAX)
    for i in xrange(doorAttempt):
        mini,maxi,(mx,my),(Mx,My) = cv2.minMaxLoc(matches) 
        # We find minimum and maximum value locations in result
        if matchvalue in [0,1]: 
        # For SQDIFF and SQDIFF_NORMED, the best matches are lower values.
            MPx,MPy = mx,my
        else:
             # Other cases, best matches are higher values.
            MPx,MPy = Mx,My

        # Normed methods give better results, ie matchvalue = [1,3,5], 
        #others sometimes shows errors
        cv2.rectangle(img2, (MPx,MPy),(MPx+tcols,MPy+trows),(0,0,white),2)
        matches[MPy][MPx]=bwThresh
        rects.append((MPx,MPy,tcols,trows))
    return rects







