#WANG ZHIYU
#NavPal FloorPlan
#############################################################
#Class structure definitions
#############################################################

#Point: used in denoting endpoints of lines in the floorplan
class Point(object):
    def __init__(self,row=-1,col=-1):
        self.row=row
        self.col=col

    #redefine EQUAL  to compare points  
    def __eq__(self,other):
        if other==None: return False
        return (self.row==other.row) and (self.col==other.col)

#Line: used in denoting line segments in the floorplan
class Line(object):
    def __init__(self,start=None,end=None):
        if start == None:
            start = Point()
        self.start=start
        if end == None:
            end = Point()
        self.end=end
    #redefine EQUAL to compare lines    
    def __eq__(self,other):
        return (self.start==other.start) and (self.end==other.end)

#Image Object: used in denoting image objects 
class FImage(object):
    def __init__(self, width=0,height=0):
        self.width=width
        self.height=height
        self.rgb=[]
        self.ImgSize=width*height



