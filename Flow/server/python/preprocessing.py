##Author: Zhiyu Wang

from utilities import *

#Feature1:Extract straight lines in the floor plan
          #using brute-force search
#Feature2:Merging lines in close proximity. Reduce the number of lines
          #identified by 60 percent
#Lines are represented by Starting and End Point, each is 2-tuple


def process_img(sourcepath, destpath):

    IMG=parseInputFile(sourcepath)    
    
    saveImage(sourcepath,IMG) 
    (hlines,vlines)=extractLines(IMG)
    
    
    #merge lines in close proximity
    
    #mergeHlines(hlines)
    #print "Mergeing horizontal lines!"
    #mergeVlines(vlines)
    
    #mergeVertex(hlines,vlines)
    
    #visualizeLines(IMG,vlines,hlines)
    
    saveRemoveLines(IMG,vlines,hlines)    
    
    img=cv2.imread("temp.png")  
    
    writeVertexList(hlines,vlines,destpath) 
    OCR(img,sourcepath,destpath)
   
def init():
    start=clock()
    args=sys.argv[1:]
    if len(args)<2:
        print "two inputs are required"
    #numOfImage=len(args)
    #pool=Pool(processes=4)
    #pages=pool.map(process_img,args)
    else: process_img(args[0],args[1])
    elapsed=clock() - start
    print elapsed
init()






