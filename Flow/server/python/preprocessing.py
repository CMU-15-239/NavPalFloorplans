##Author: Zhiyu Wang

from utilities import *

#Feature1:Extract straight lines in the floor plan
          #using brute-force search
#Feature2:Merging lines in close proximity. Reduce the number of lines
          #identified by 60 percent
#Lines are represented by Starting and End Point, each is 2-tuple


def process_img(sourcepath,destpath, datapath):

    IMG=parseInputFile(sourcepath)    
    

    (hlines,vlines)=extractLines(IMG)
    
    
    reverseColor(IMG)
    #saveImage(destpath,IMG) 
    
    #saveRemoveLines(IMG,vlines,hlines)    
    
    img=cv2.imread("temp.png") 
    #writeVertexList(hlines,vlines,datapath) 
    OCR(img,sourcepath,datapath)
    
    #merge lines in close proximity
    
    #mergeHlines(hlines)
    #print "Mergeing horizontal lines!"
    #mergeVlines(vlines)
    
    #mergeVertex(hlines,vlines)
    
    #visualizeLines(IMG,vlines,hlines)



    #cvImg=np.array(np.uint8(IMG.rgbs))
    #visualizeDoors(img,sourcepath)
   
def init():
    start=clock()
    args=sys.argv[1:]
    if len(args)<3:
        print "three inputs are required\n"
    #numOfImage=len(args)
    #pool=Pool(processes=4)
    #pages=pool.map(process_img,args)
    else: process_img(args[0],args[1],args[2])
    elapsed=clock() - start
    print elapsed
init()






