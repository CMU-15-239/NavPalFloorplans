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
    im=saveImage(destpath,IMG) 
   
    im_bw=cv2.threshold(im,5,255,cv2.THRESH_BINARY)[1] 


    saveRemoveLines(IMG,vlines,hlines)    
    
    img=cv2.imread("temp.png") 

    doorRects=ExtractDoors(img,sourcepath)
    extractDoors(im,hlines,vlines,doorRects)
    
    #merge lines in close proximity    
    mergeHlines(hlines)
    #print "Mergeing horizontal lines!"
    mergeVlines(vlines)
    
    mergeVertex(hlines,vlines)

    #writeVertexList(hlines,vlines,datapath) 
    #OCR(img,sourcepath,datapath)
    visualizeLines(IMG,vlines,hlines)
	
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






