import sys
from classes import *
epsilon=8



def h_distance(line1,line2):
    fs=line1.start
    fe=line1.end
    ss=line2.start
    se=line2.end
	#case where two lines overlap horizontally
    if ((ss.col <= fe.col <= se.col) or \
			(ss.col<= fs.col<= se.col)or\
            (fs.col <= ss.col and se.col<= fe.col)):
        hd=0
    #case where two lines not overlap but really close
    else:
        hd=min(abs(fs.col-se.col),abs(fe.col-ss.col))
   	#vd denotes the distance of two lines in vertical direction 
    vd=abs(line1.start.row-line2.start.row)
    return max(hd,vd)

def v_distance(line1,line2):
    fs=line1.start
    fe=line1.end
    ss=line2.start
    se=line2.end
    #case where two lines overlap vertically
    if ((ss.row <= fs.row <= se.row) \
	    or (ss.row <= fe.row <=se.row)\
	    or (fs.row<=ss.row and se.row<=fe.row)):
        vd=0
	#case where two lines not overlap but close
    else:
        vd=min(abs(fs.row-se.row),abs(fe.row-ss.row))
    hd = abs(line1.start.col-line2.start.col)
    return max(hd,vd)

#merge two horizontal lines by 
#averaging the row numbers
#taking the starting point min and ending point max

def hmerge(line1,line2):
    row1=line1.start.row
    row2=line2.start.row
	#average the row indices
    mrow=(row1+row2)/2
	#take min of starting points col as merged starting col
    fcol=min(line1.start.col,line2.start.col)
	#take max of ending points col as merged ending col
    scol=max(line1.end.col,line2.end.col)
    line1.start.row=mrow
    line1.end.row=mrow
    line1.start.col=fcol
    line1.end.col=scol

#merge two vertical lines by 
#averaging the col numbers
#taking the starting point min and ending point max

def vmerge(line1,line2):
    col1=line1.start.col
    col2=line2.start.col
	#average the col indices
    mcol=(col1+col2)/2
	#take min of starting points row as merged starting row
    frow=min(line1.start.row,line2.start.row)
	#take max of ending points row as merged ending row
    srow=max(line2.end.row,line2.end.row)
    line1.start.row=frow
    line1.end.row=srow
    line1.start.col=mcol
    line1.end.col=mcol

#check pairwise distances of lines segments in the list
def merge_hlines(h_lines):
    index=0
    after=1
    while index < len(h_lines):
        after=index+1
        while after< len(h_lines):
            current=h_lines[index]
            nextl=h_lines[after]
            #merge if distance between current line and
			#next line is smaller than the pre-set value
            if h_distance(current,nextl)<epsilon:
                hmerge(current,nextl)
                h_lines.pop(after)
           		#rechecking the resulting merged line
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
			#merge if distance between current line and
			#next line is smaller than the pre-set value
            if v_distance(current,nextl)<epsilon:
                vmerge(current,nextl)
                v_lines.remove(nextl)
                #recheck the resulting merged line
                index-=1
                break
            after+=1
        index+=1

