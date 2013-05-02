#WANG ZHIYU
####################################
#Line merging functions
####################################

import sys
from classes import *
from extractLine import*
from constant import *

#Summary:check whether two line segments intersect
#Input: list of horizontal lines, list of vertical lines
#Output: Boolean value
def intersect(hLine,vLine): 
    hIntersect = (vLine.start.row <= hLine.start.row <=vLine.end.row)
    vIntersect = (hLine.start.col <=vLine.start.col <= hLine.end.col)
    return hIntersect and vIntersect

#Summary: create a new line segment
#Input: startrow, startcol, endrow, endcol
#Output: a line object
def newLine(startRow,startCol, endRow,endCol):
    startPoint=Point(startRow,startCol)
    endPoint=Point(endRow,endCol)
    newLine=Line(startPoint,endPoint)
    return newLine

#Summary: mergeVertex function idenfies intersections that are 
#supposed to be corners in the original image and make them corners by 
#removing short line segments that gobeyond the intersection.
#Input: list of horizontal lines, list of vertical lines
#Output: None

def mergeVertex(hLines,vLines):
    hIndex=0 #index used in traversing list of horizontal lines
    vIndex=0 #index used in traversing list of vertical lines
    newhLines=[] # new horizontal lines
    newvLines=[] # new vertical lines
    while  hIndex < len(hLines):
        vIndex=0
        while vIndex < len(vLines):
            #for ease of refEndrence: 
            #hl=horizontal line; vl=vertical lines
            hl=hLines[hIndex]
            vl=vLines[vIndex]
            if intersect(hl,vl):
                #isp denotes the intersection point
                isp=Point(hl.start.row, vl.start.col)
                
                #two line segments intersecting will give us four line segments,
                #we check whether each of the line segments satisfy minimum 
                #length cutoff, if they do not, we delete them from our
                #line segment lists
                #we name the four line segments, upline, downline,leftline
                #and rightline

                #case1: check upline 
                #check whether the newly generated upline satisfies minlength
                if vl.start.col-hl.start.col> minlen:
                    line1=Line(hl.start,isp)
                    newhLines.append(line1)
                #case2: check leftline
                #check whether the newly generated leftline satisfies minlength
                if isp.row-vl.start.row > minlen:
                    line2=Line(vl.start,isp)
                    newvLines.append(line2)
                
                #case3:check rightline 
                #rightline is the original horizontal line segment
                if hl.end.col-isp.col > minlen:
                    hLines[hIndex].start=isp
                else:
                    #case when original horizontal line shorter than minlength
                    #we then delete it from our list
                    hLines.pop(hIndex)
                    hIndex-=1
                #case4: check downline
                #downline is the original vertical line segment
                if vl.end.row-isp.row>minlen:
                    vLines[vIndex].start=isp
                else:
                    #case when original vertical line shorter than minlength
                    #we then delete it from our list
                    vLines.pop(vIndex)
                    vIndex-=1
            #increase vLine list index, and check for the same horizontal line  
            #intersection again
            vIndex+=1
        #increase hLine index     
        hIndex+=1
    hLines+=newhLines
    vLines+=newvLines

#Summary: check distances between two horizontal lines
#Input: first line object, second line object
#Output: distance
def hDistance(line1,line2):
    fStart=line1.start
    fEnd=line1.end
    sStart=line2.start
    sEnd=line2.end
    #compute distance in horizontal axis
	#case where two lines overlap horizontally
    if ((sStart.col <= fEnd.col <= sEnd.col) or \
			(sStart.col<= fStart.col<= sEnd.col)or\
            (fStart.col <= sStart.col and sEnd.col<= fEnd.col)):
        hd=0 #distance in horizontal coordinate

    #case where two lines not overlap but really close
    else:
        hd=min(abs(fStart.col-sEnd.col),abs(fEnd.col-sStart.col))
   	
    #computer distance in vertical axis
    vd=abs(line1.start.row-line2.start.row)
    return max(hd,vd)

#Summary: check distances between two horizontal lines
#Input: first line object, second line object
#Output: distance
def vDistance(line1,line2):
    fStart=line1.start
    fEnd=line1.end
    sStart=line2.start
    sEnd=line2.end
    #compute distance in vertical axis
    #case where two lines overlap vertically
    if ((sStart.row <= fStart.row <= sEnd.row) 
	    or (sStart.row <= fEnd.row <=sEnd.row)
	    or (fStart.row<=sStart.row and sEnd.row<=fEnd.row)):
        vd=0
	#case where two lines not overlap but close
    else:
        vd=min(abs(fStart.row-sEnd.row),abs(fEnd.row-sStart.row))
    
    #compute distance in horizontal axis
    hd = abs(line1.start.col-line2.start.col)
    return max(hd,vd)

#Summary:merge two horizontal lines by averaging the row numbers and 
#        taking the starting point min and ending point max
#Input: line1, line2
#Output: None
def hMerge(line1,line2):
    row1=line1.start.row
    row2=line2.start.row
	#average the row indices
    mRow=(row1+row2)/2
	#take min of starting points col as merged starting col
    fCol=min(line1.start.col,line2.start.col)
	#take max of ending points col as merged ending col
    sCol=max(line1.end.col,line2.end.col)
    line1.start.row=mRow
    line1.end.row=mRow
    line1.start.col=fCol
    line1.end.col=sCol

#Summary: merge two vertical lines by averaging the col numbers and
#         taking the starting point min and ending point max
#Input: line1,line2
#Output: None
def vMerge(line1,line2):
    col1=line1.start.col
    col2=line2.start.col
	#average the col indices
    mCol = col1
    #mcol=(col1+col2)/2
	#take min of starting points row as merged starting row
    fRow=min(line1.start.row,line2.start.row)
	#take max of ending points row as merged ending row
    sRow=max(line2.end.row,line2.end.row)
    line1.start.row=fRow
    line1.end.row=sRow
    line1.start.col=mCol
    line1.end.col=mCol

#Summary:merge horizontal lines in close proximity
#Input: list of horizontal lines
#Output: None
def mergeHlines(hLines):
    fIndex=0
    sIndex=1
    while fIndex < len(hLines):
        sIndex=fIndex+1
        while sIndex< len(hLines):
            currentLine=hLines[fIndex]
            nextLine=hLines[sIndex]
            #merge if distance between currentLine line and
			#next line is smaller than the pre-set value
            if hDistance(currentLine,nextLine)<epsilon:
                hMerge(currentLine,nextLine)
                hLines.pop(sIndex)
           		#rechecking the resulting merged line
                fIndex-=1
                break
            sIndex+=1
        fIndex+=1

#Summary:merge vertical lines in close proximity
#Input: list of vertical lines
#Output: None
def mergeVlines(vLines):
    fIndex=0
    sIndex=1
    while fIndex < len(vLines):
        sIndex=fIndex+1
        while sIndex< len(vLines):
            currentLine=vLines[fIndex]
            nextLine=vLines[sIndex]
			#merge if distance between currentLine line and
			#next line is smaller than the pre-set value
            if vDistance(currentLine,nextLine)<epsilon:
                vMerge(currentLine,nextLine)
                vLines.remove(nextLine)
                #recheck the resulting merged line
                fIndex-=1
                break
            sIndex+=1
        fIndex+=1
