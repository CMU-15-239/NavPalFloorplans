from PIL import Image

##brute force approach to extract lines out of floor plan

minl=50
epsilon=10

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

class FImage(object):
	def __init__(self, width=0,height=0):
		self.width=width
		self.height=height
		self.rgbs=[]
		self.ImgSize=width*height
		self.maximum=0


def process_img_line_horizontal(img_line, row, fimage, lines,fd, min_length):
	x=0
	while x < fimage.width:
		while ( x< fimage.width and ((img_line[x])>=250)):
			x=x+1
		if x >= fimage.width: break
		newLine=Line()
		newLine.start.row=row
		newLine.start.col=x-1
		while (x < fimage.width and (img_line[x]<250)):
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
		while (y < fimage.height and (img[y][col]>=250)):
			y+=1
		if y>=fimage.height: break
		newLine=Line()
		newLine.start.row=y-1
		newLine.start.col=col
		while (y < fimage.height and (img[y][col]<250)):
			y+=1
		newLine.end.row=y-1
		newLine.end.col=col
		
		if newLine.end.row-newLine.start.row > min_length:
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
	IMG=FImage()
	ifd=open('nsh2.pgm','r')
	parse_input_file(ifd,IMG)
	type(hlines)
	process_horizontal(IMG,hlines,fd)
	print "Done with horizontal lines"
	process_vertical(IMG,vlines,fd)
	return (hlines,vlines,IMG)

def write_vertex_list():
	fd=open('nsh.json','w')
	#fd2=open('bogus.txt','w')

	fd.write("#line format as follow:\n")
	fd.write("#starting_row staring_col ending_row ending_col\n")
	fd.write("#begin horizontal lines\n")
	fd.write("{lines:[\n")
	(hlines,vlines,IMG)=extract_lines(fd)
	fd.write("}\n")
	fd.close()
	return (hlines,vlines,IMG)

def h_distance(line1,line2):
	fs=line1.start
	fe=line1.end
	ss=line2.start
	se=line2.end
	if ((ss.col <= fe.col <= se.col) or (ss.col<= fs.col<= se.col)or\
				(fs.col <= ss.col and se.col<= fe.col)):
		hd=0	
	else: 
		hd=min(abs(fs.col-se.col),abs(fe.col-ss.col))
	vd=abs(line1.start.row-line2.start.row)
	return max(hd,vd)

def v_distance(line1,line2):
	fs=line1.start
	fe=line1.end
	ss=line2.start
	se=line2.end
	if ((ss.row <= fs.row <= se.row) or (ss.row <= fe.row <=se.row)or\
					(fs.row<=ss.row and se.row<=fe.row)):
		vd=0
	else: 
		vd=min(abs(fs.row-se.row),abs(fe.row-ss.row))
	hd=abs(line1.start.col-line2.start.col)
	return max(hd,vd)

def hmerge(line1,line2):
	row1=line1.start.row
	row2=line2.start.row
	mrow=(row1+row2)/2
	fcol=min(line1.start.col,line2.start.col)
	scol=max(line1.end.col,line2.end.col)
	line1.start.row=mrow
	line1.end.row=mrow
	line1.start.col=fcol
	line1.end.col=scol

def vmerge(line1,line2):
	col1=line1.start.col
	col2=line2.start.col
	mcol=(col1+col2)/2
	frow=min(line1.start.row,line2.start.row)
	srow=max(line2.end.row,line2.end.row)
	line1.start.row=frow
	line1.end.row=srow
	line1.start.col=mcol
	line1.end.col=mcol

def merge_hlines(h_lines):
	index=0
	after=1
	while index < len(h_lines):
		after=index+1
		while after< len(h_lines):
			current=h_lines[index]
			nextl=h_lines[after]
			if h_distance(current,nextl)<epsilon:
				hmerge(current,nextl)
				h_lines.pop(after)
				index-=1
				break
			after+=1
		index+=1

def merge_vlines(v_lines):
	index=0
	after=1
	while index < len(v_lines):
		after=index+1
		while after< len(v_lines):
			current=v_lines[index]
			nextl=v_lines[after]
			if v_distance(current,nextl)<epsilon:
				vmerge(current,nextl)
				v_lines.remove(nextl)
				index-=1
				break
			after+=1
		index+=1

def gray2rgb(IMG):
	rgbs=IMG.rgbs
	for i in xrange(len(rgbs)):
		for j in xrange(len(rgbs[0])):
			pixel=rgbs[i][j]
			IMG.rgbs[i][j]=(pixel,pixel,pixel)
	return


def colorLines(hlines,vlines,IMG):
	for line in hlines:
		row=line.start.row
		cols=line.start.col
		cole=line.end.col
		for i in xrange(cols,cole+1):
			IMG.rgbs[row][i]=(255,255,0)

	for line in vlines:
		col=line.start.col
		rows=line.start.row
		rowe=line.end.row
		for j in xrange(rows,rowe+1):
			IMG.rgbs[j][col]=(255,255,0)



def do():
	(hlines,vlines,IMG)=write_vertex_list()
	print len(hlines),len(vlines) 

	floor=Image.open('nsh2.png')
	list_of_pixels=list(floor.getdata())
	
	merge_hlines(hlines)
	merge_vlines(vlines)
	merge_hlines(hlines)
	merge_vlines(vlines)

	print len(hlines),len(vlines)
	gray2rgb(IMG)
	colorLines(hlines,vlines,IMG)
	rgbs=IMG.rgbs
	pixels=[]
	for img_line in rgbs:
		pixels+=img_line
	
	imSize=(IMG.width,IMG.height)
	im2= Image.new('RGB',imSize)
	im2.putdata(pixels)
	im2.show()

do()







