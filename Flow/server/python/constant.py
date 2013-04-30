#Author: ZHIYU WANG
###################################################
#CONSTANTS
###################################################
#This file specifies the constants used in preprocessor

#threshhold value for determining white or black color in Black and white format
bwThresh=127
#threshold value for determining white or black color in RGB format
rgbThresh=(127,127,127)

green=(0,255,0)
rgbWhite=(255,255,255)
white=255
bthresh=5
wthresh=250
#number of attempts to template match doors
doorAttempt=50
#minimum length of a line segment
minlen=20
#line merging cutoff
ep=5

textWidthMin=15
textWidthMax=90
textHeightMin=2
textHeightMax=20
#text extraction confidence threshold
confidenceLevel=70
#Ascii value for valid room symbols
validCharStart=47
validCharEnd=58
#resizing constant
resize=3
textOffset=20
#epsilon is minimum line segment cutoff
epsilon=5
localPath='temporaryStorage.png'

doorWidthMin=14
doorWidthMax=40

doorHeightMin=10
