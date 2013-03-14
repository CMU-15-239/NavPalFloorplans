#/usr/bin/env python
minl=10

class Point(object):
	def __init__(self,row=-1,col=-1):
		self.row=row
		self.col=col
	
	#redefine equal function to compare points	
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
	def __eq__(self,other):
		return (self.start==other.start) and (self.end==other.end)

class Image(object):
	def __init__(self, width=0,height=0):
		self.width=width
		self.height=height
		self.rgbs=[]
		self.ImgSize=width*height
		self.maximum=0


def process_img_line_horizontal(img_line, row, fimage, lines,fd, min_length):
	x=0
	while x < fimage.width:
		while ( x< fimage.width and ((img_line[x])>=240)):
			x=x+1
		if x >= fimage.width: break
		newLine=Line()
		newLine.start.row=row
		newLine.start.col=x-1
		while (x < fimage.width and (img_line[x]<240)):
			x+=1
		newLine.end.row=row
		newLine.end.col=x-1
		if newLine.end.col-newLine.start.col > min_length:
			fd.write("{line:[{p1:[%d,%d]},{p2:[%d,%d]}]}\n" %\
			(newLine.start.row, newLine.start.col,newLine.end.row, newLine.end.col))
			lines.append(newLine)
	return

def process_img_line_vertical(col,fimage,lines,fd,min_length):
	y=0
	img=fimage.rgbs
	while y < fimage.height:
		while (y < fimage.height and (img[y][col]>=240)):
			y+=1
		if y>=fimage.height: break
		newLine=Line()
		newLine.start.row=y-1
		newLine.start.col=col
		while (y < fimage.height and (img[y][col]<240)):
			y+=1
		newLine.end.row=y-1
		newLine.end.col=col
		
		if newLine.end.row-newLine.end.col > min_length:
			fd.write("{line:[{p1:[%d,%d]},{p2:[%d,%d]}]}\n" %\
			(newLine.start.row, newLine.start.col,newLine.end.row, newLine.end.col))
			lines.append(newLine)

def process_horizontal(IMG, lines,fd):
	for row in xrange(IMG.height):
		img_line=IMG.rgbs[row]
		process_img_line_horizontal(img_line,row,IMG,lines,fd,minl),
	return lines
	

def process_vertical(IMG,lines,fd):
	for col in xrange(IMG.width):
		process_img_line_vertical(col,IMG,lines,fd,minl)
	return lines

def oneToMulti(l,width):
	return [l[i:i+width] for i in xrange(0,len(l),width)]


def parse_input_file(fd,IMG):
	fd.readline()
	
	#read in the pgm's dimensions
	dimension_txt=fd.readline()
	dimensions=dimension_txt.split(" ")
	
	IMG.height=int(dimensions[1][:-1])
	IMG.width=int(dimensions[0])
	
	IMG.maximum=int(fd.readline()[:-1])
	
	#read in the rgb arrays
	
	pgms=fd.read()
	data=pgms.replace("\n", "")
	data=data.split(" ")
	print "dimension=",IMG.width*IMG.height	
	data=data[:-1]
	IMG.rgbs=oneToMulti(data,IMG.width)
	print "height=",len(IMG.rgbs)
	print "width",len(IMG.rgbs[0])

	for i in xrange(len(IMG.rgbs)):
		for j in xrange(len(IMG.rgbs[0])):
			IMG.rgbs[i][j]=int(IMG.rgbs[i][j])


def extract_lines(fd):
	hlines=[]
	vlines=[]
	IMG=Image()
	ifd=open('nsh2.pgm','r')
	parse_input_file(ifd,IMG)
	type(hlines)
	process_horizontal(IMG,hlines,fd)
	print "Done with horizontal lines"
	process_vertical(IMG,vlines,fd)
	return (hlines,vlines)

def write_vertex_list():
	fd=open('nsh.json','w')
	#fd2=open('bogus.txt','w')

	fd.write("#line format as follow:\n")
	fd.write("#starting_row staring_col ending_row ending_col\n")
	fd.write("#begin horizontal lines\n")
	fd.write("{lines:[\n")
	(hlines,vlines)=extract_lines(fd)
	fd.write("}\n")
	a="""
	for line in hlines:
		fd2.write("%d %d %d %d\n" %(line.start.row, line.start.col,\
								line.end.row, line.end.col))
	
	for line in vlines:
		fd2.write("%d %d %d %d\n" %(line.start.row, line.start.col,\
								line.end.row, line.end.col))
 """
 	#fd2.close()	
	fd.close()
	return (hlines,vlines)

"""
def colorLines(hlines,vlines,IMG):
	for line in hlines:
		row=line.start.row
		cols=line.start.col
		cole=line.end.col
		IMG.rgbs[]:W
"""

def do():
	(hlines,vlines)=write_vertex_list()
	print "extracting lines..."
	print "Done with line extraction"

do()







