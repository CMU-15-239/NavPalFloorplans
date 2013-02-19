from PIL import Image
import sys
from merge import *
from extractLine import *
from classes import *
#Feature1:Extract straight lines in the floor plan
          #using brute-force search
#Feature2:Merging lines in close proximity. Reduce the number of lines
          #identified by 60 percent
#Lines are represented by Starting and End Point, each is 2-tuple


#Utility functions
#convert 1d list to 2d list, 
#given the dimension of the 2d list
def oneToMulti(l,width):
    return [l[i:i+width] for i in xrange(0,len(l),width)]

#convert grayscale image to RGB format 
def gray2rgb(IMG):
    rgbs=IMG.rgbs
    for i in xrange(len(rgbs)):
        for j in xrange(len(rgbs[0])):
            pixel=rgbs[i][j]
            IMG.rgbs[i][j]=(255-pixel,255-pixel,255-pixel)
    return

#parsing input file function

def parse_input_file2(image_dir):
    img=Image.open(image_dir)
    #convert image to grayscale
    img=img.convert('L')
    #destructively save the image
    img.save(image_dir)

    #extract the pixels
    list_of_pixels=list(img.getdata())
    (width,height)=img.size
    #initialize IMG object
    IMG=FImage()
    IMG.width=width
    IMG.height=height
    IMG.rgbs=oneToMulti(list_of_pixels,IMG.width)
    return IMG

def extract_lines(IMG):
    hlines=[]
    vlines=[]
    process_horizontal(IMG,hlines)
    process_vertical(IMG,vlines)
    return (hlines,vlines)


def write_vertex_list(hlines,vlines):
    fd=open('nsh.json','w')

    fd.write("#line format as follow:\n")
    fd.write("#starting_row starting_col \
                ending_row ending_col\n")
    fd.write('{\"lines\":[\n')

    #write the line segments into a json file for canvas 
    for line in hlines:
        fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}\n' %\
        (line.start.row, line.start.col,line.end.row,\
        line.end.col))
    for line in vlines:
        fd.write('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}\n' %\
            (line.start.row, line.start.col, line.end.row,\
                line.end.col))
    fd.write("}\n")
    fd.close()

def generate_string(hlines,vlines):
    s='{\"lines\":[\n'
    for line in hlines:
        s+=('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}\n' %\
                (line.start.row, line.start.col,line.end.row,\
                        line.end.col))
    for line in vlines:
        s+=('{\"line\":[{\"p1\":[%d,%d]},{\"p2\":[%d,%d]}]}\n' %\
                (line.start.row, line.start.col,line.end.row,\
                        line.end.col))
    s+=']}\n'
    print s

#Utility function to color the lines segments in yellow

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


def visualize_lines(IMG,vlines,hlines):
    gray2rgb(IMG)
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


def process_img(image_dir):
    IMG=parse_input_file2(image_dir)
    (hlines,vlines)=extract_lines(IMG)
    #merge lines in close proximity
    merge_hlines(hlines)
    #print "Mergeing horizontal lines!"
    merge_vlines(vlines)
    
    #visualize_lines(IMG,vlines,hlines)
    
    generate_string(vlines,hlines)
    

def init():
    args=sys.argv[1:]
    image_dir=args[0]
    process_img(image_dir)    
init()






