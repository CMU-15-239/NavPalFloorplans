
class Point(object):
    def __init__(self,row=-1,col=-1):
        self.row=row
        self.col=col

    #redefine EQUAL  to compare points  
    def __eq__(self,other):
        return (self.row==other.row) and (self.col==other.col)

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

#Image Object 
class FImage(object):
    def __init__(self, width=0,height=0):
        self.width=width
        self.height=height
        self.rgbs=[]
        self.ImgSize=width*height
        self.maximum=0
