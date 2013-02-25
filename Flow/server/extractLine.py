#process each horizontal pixel line and extract straigt line segments
#out and add them into the list of line segments

from classes import *
minlen=50

def process_img_line_horizontal(row, fimage, lines):
    x=0
    img_line=fimage.rgbs[row]
    while x < fimage.width:
        #find the first pixel that is not white     
        while ( x< fimage.width and ((img_line[x])>=250)):
            x=x+1
        if x >= fimage.width: break
        #initialize a new line and the starting point
        newLine=Line()
        newLine.start.row=row
        newLine.start.col=x-1
        #search for the ending point till we meet a white pixel
        while (x < fimage.width and (img_line[x]<250)):
            x+=1
        newLine.end.row=row
        newLine.end.col=x-1
        #add the line segments into the list of lines
        if newLine.end.col-newLine.start.col > minlen:
            lines.append(newLine)
    return


#similar function to process_img_line_horizontal 
#but in vertical dirction

def process_img_line_vertical(col,fimage,lines):
    y=0
    img=fimage.rgbs
    while y < fimage.height:
        #search for the first non-white pixel
        while (y < fimage.height and (img[y][col]>=250)):
            y+=1
        if y>=fimage.height: break
        #initialize the new line and the starting point
        newLine=Line()
        newLine.start.row=y-1
        newLine.start.col=col
        #keep extending the line till 
        #meeting the first white pixel
        while (y < fimage.height and (img[y][col]<250)):
            y+=1
        newLine.end.row=y-1
        newLine.end.col=col

        if newLine.end.row-newLine.start.row > minlen:
            lines.append(newLine)

#wrapper function to process all horizontal straight lines

def process_horizontal(IMG, lines):
    for row in xrange(IMG.height):
        process_img_line_horizontal(row,IMG,lines),
    return lines

#wrapper function to process all vertical straight lines

def process_vertical(IMG,lines):
    for col in xrange(IMG.width):
        process_img_line_vertical(col,IMG,lines)
    return lines
