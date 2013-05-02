#Author: Wang Zhiyu. Carnegie Mellon University
#process each horizontal pixel line and extract straigt line segments
#out and add them into the list of line segments
from constant import *
from classes import *


#Summary:Search and store horizontal segmentals in a particular row
#Input: row number, image array, list of lines to store new line segments
#Output:None
def processHorizontalImageLine(row, image, lines):
    x=0 #x is the index of pixel in the given row 
    imgLine=image.rgb[row]
    while x < image.width:
        #find the first pixel that is not white     
        while ( x< image.width and ((imgLine[x])>=wthresh)):
            x=x+1
        if x >= image.width: break
        #initialize a new line and the starting point
        newLine=Line()
        newLine.start.row=row
        newLine.start.col=x-1
        #search for the ending point till we meet a white pixel
        while (x < image.width and (imgLine[x]<wthresh)):
            x+=1
        newLine.end.row=row
        newLine.end.col=x-1
        #add the line segments into the list of lines
        if newLine.end.col-newLine.start.col > minlen:
            lines.append(newLine)
    return


#Summary:Search and store vertical segmentals in a particular column
#Input: column number, image array, list of lines to store new line segments
#Output:None
def processVerticalImageLine(col,image,lines):
    y=0 # y is the index of pixel in a given column
    img=image.rgb
    while y < image.height:
        #search for the first non-white pixel
        while (y < image.height and (img[y][col]>=wthresh)):
            y+=1
        if y>=image.height: break
        #initialize the new line and the starting point
        newLine=Line()
        newLine.start.row=y-1
        newLine.start.col=col
        #keep extending the line till 
        #meeting the first white pixel
        while (y < image.height and (img[y][col]<wthresh)):
            y+=1
        newLine.end.row=y-1
        newLine.end.col=col

        if newLine.end.row-newLine.start.row > minlen:
            lines.append(newLine)

#Summary: wrapper function to process all rows of the floor plan image
#Input: image object, list of lines
#Output: updated list of lines
def processHorizontal(IMG, lines):
    for row in xrange(IMG.height):
        processHorizontalImageLine(row,IMG,lines),
    return lines

#Summary: wrapper function to process all columns of the floor plan image
#Input: image object, list of lines
#Output: updated list of lines
def processVertical(IMG,lines):
    for col in xrange(IMG.width):
        processVerticalImageLine(col,IMG,lines)
    return lines
