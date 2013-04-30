##Author: Zhiyu Wang

from utilities import *
from constant import *
#Feature1:Extract straight lines in the floor plan
          #using brute-force search
#Feature2:Merging lines in close proximity. Reduce the number of lines
          #identified by 60 percent
#Lines are represented by Starting and End Point, each is 2-tuple

#Summary: Wrapper function of the core functions of preprocessor. 
#Inputs:  Source image path,  destination image path, datapath
#Outputs: None
def processImage(sourcepath,destpath, datapath):
    
    IMG=parseInputFile(sourcepath)    
    #line extraction: results saved as list of 
    #horizontal lines and vertical lines
    
    (hLines,vLines)=extractLines(IMG)
    
    reverseColor(IMG)
    im=saveImage(destpath,IMG)  
    bwImg=cv2.threshold(im,5,255,cv2.THRESH_BINARY)[1] 
    #Remove extracted lines to get a cleaner image for door/text detection
    img=saveRemoveLines(sourcepath,IMG,vLines,hLines)    
    #Identify the bounding rectangles of doors in the floorplan
    #Detect position of doors and extract them
    doorRects=obtainRects(img)
    extractDoors(bwImg,hLines,vLines,doorRects,datapath)
    #merge lines in close proximity    
    mergeHlines(hLines)
    mergeVlines(vLines)
    
    mergeVertex(hLines,vLines)
    #Write line objects into a json file and pass to the authoring tool
    writeVertexList(hLines,vLines,datapath)
    #Detect texts and write the data into json file 
    OCR(img,sourcepath,datapath)
    #visualizeLines(IMG,vLines,hLines)

#Summary: standard init function that takes terminal arguments 
#         and runs image pre-processing functions	
def init():
    start=clock()
    args=sys.argv[1:]
    if len(args)<3:
        print "three inputs are required\n"
    #Below are codes that enable multi-processing
    #numOfImage=len(args)
    #pool=Pool(processes=4)
    #pages=pool.map(process_img,args)
    else: processImage(args[0],args[1],args[2])
    elapsed=clock() - start
    print elapsed
init()






