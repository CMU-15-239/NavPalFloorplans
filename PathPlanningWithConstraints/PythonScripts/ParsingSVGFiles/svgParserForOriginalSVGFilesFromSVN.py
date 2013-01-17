#### {{{ http://code.activestate.com/recipes/116539/ (r1)
##==================================================
##xmlreader.py:
##==================================================
##import sys

from xml.dom.minidom import parse
import xml.dom.minidom as dom
from xml.dom import minidom
from xml.dom import Node
import re
import math

import xml.etree.ElementTree as ET

######################################################
##   ____                _              _       
##  / ___|___  _ __  ___| |_ __ _ _ __ | |_ ___ 
## | |   / _ \| '_ \/ __| __/ _` | '_ \| __/ __|
## | |__| (_) | | | \__ \ || (_| | | | | |_\__ \
##  \____\___/|_| |_|___/\__\__,_|_| |_|\__|___/
##                                              
######################################################

CURRENTMAP = 1
DEBUG = 0

## Global Variables
numberOfRooms = 0

######################################################
##   ____ _                         
##  / ___| | __ _ ___ ___  ___  ___ 
## | |   | |/ _` / __/ __|/ _ \/ __|
## | |___| | (_| \__ \__ \  __/\__ \
##  \____|_|\__,_|___/___/\___||___/
##                                  
######################################################

######################################################
# Class to parse a string of tokens
######################################################

class StringTokenizer(object):

    def __init__(self, dStr = "", delims = ' '):
        self.dStr = dStr
        self.stringTokPtr = 0

        if len(dStr) > 0:
            self.tokens    = re.split(delims, dStr)
            self.numTokens = len(self.tokens)
        else:
            self.tokens    = []
            self.numTokens = 0

    def getList(self):
        return self.tokens

    def hasMoreTokens(self):
        if self.stringTokPtr < self.numTokens:
            return True
        else:
            return False

    def nextToken(self):

        if self.stringTokPtr < self.numTokens:
            self.currentToken = self.tokens[self.stringTokPtr]
            self.stringTokPtr += 1
            return self.currentToken

        else:
            return None

######################################################
# Class to store a vertex
######################################################
class Vertex(object):

    def __init__(self, x=0.0, y=0.0):
        self.x = x
        self.y = y

    def printVertices(self):
        return "%f %f" % (self.x, self.y)

######################################################
##  _____                 _   _                 
## |  ___|   _ _ __   ___| |_(_) ___  _ __  ___ 
## | |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
## |  _|| |_| | | | | (__| |_| | (_) | | | \__ \
## |_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/
##                                              
######################################################

######################################################
# Convert the string representation of the transform
# to an actual matrix
######################################################
def getTransform(T):

    st = StringTokenizer(T, ' |,|\(|\)')

    nextToken = st.nextToken()
    if st.hasMoreTokens() and ( nextToken == "matrix"):

        A = [[0.0, 0.0, 0.0], [0.0, 0.0, 0.0], [0.0, 0.0, 0.0]]

        i = -1
	while (st.hasMoreTokens() and i < 6):
            i += 1
            nextToken = st.nextToken()

            try:
                f = float(nextToken)
            except:
                break
            
            A[i % 2][i // 2] = f

	A[2][0] = 0
	A[2][1] = 0
	A[2][2] = 1

        return A
        
    return None

######################################################
# Parse the d String from the XML file and extracts
# the cooridnates of the current room or hallway
######################################################
def findRoomVertices(roomType, roomData):

    global DEBUG

##    if (DEBUG): print "DEBUG ON in findRoomVertices"
##    raise Exception("Inside of findRoomVertices()!")

    # Get the name of the room
    roomName = roomData['name']
    #print "roomName = %s" % (roomName)

    # Prepare the 'd' string for parsing so the room geometry can be extracted
    d = roomData['d']
    StringTok = StringTokenizer(d, ' ') # Was ', '

    # Convert the transform into an actual matrix
    transform = roomData['transform'];
    T = getTransform(transform)

    absolute = False;
    curving  = False;
    A = [0.0, 0.0, 0.0]
    P = [0.0, 0.0]
    tokens = 0;

    vertices = []

    while (StringTok.hasMoreTokens()):

        Token = StringTok.nextToken()
        tokens += 1
        if (DEBUG): print "While: %s and tokens == %d" % (Token, tokens)

        if (Token == "m"):
            if (DEBUG): print "'m' Block:\n\tToken: %s \n\n" % (Token);
            absolute = False
        else:
            if (Token == "M"):
                if (DEBUG): print "'M' Block:\n\tToken: %s \n\n" % (Token);
                absolute = True
            else:
                if (Token.lower() == "z"):
                    if (curving):
                        curving = False
                        #vertex.add(new PointF(Math.abs((P[1] + transformMatrix(getTransform())[0][2])), Math.abs((P[0] + transformMatrix(getTransform())[1][2]))));
                        a = math.fabs(P[1] + T[0][2])
                        b = math.fabs(P[0] + T[1][2])
                        vertices.append(Vertex(a, b))
                        if (DEBUG): print "'z' and 'curving' Block:\n\tP[1] = %f\n\tP[0] = %f\n\ttransform [0][2] = %f\n\ttransform [1][2] = %f\n\tx: %f, y: %f\n\n" % (P[1], P[0], T[0][2], T[1][2], a, b)
				
                    break
                else:
                    if (Token == "L"):
                        if (DEBUG): print "'L' Block:\n\tToken: %s \n\n" % (Token);
                        if (curving):
                            curving = False
                            #vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
                            a = math.fabs(P[1] + T[0][2])
                            b = math.fabs(P[0] + T[1][2])
                            vertices.append(Vertex(a, b))
                            if (DEBUG): print "'L' and 'curving' Block:\n\tP[1] = %f\n\tP[0] = %f\n\ttransform [0][2] = %f\n\ttransform [1][2] = %f\n\tx: %f, y: %f\n\n" % (P[1], P[0], T[0][2], T[1][2], a, b)
                            
                        absolute = True
                    else:
                        if (Token == "l"):
                            if (DEBUG): print "'l' Block:\n\tToken: %s \n\n" % (Token);
                            if (curving):
                                curving = False;
                                #vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
                                a = math.fabs(P[1] + T[0][2])
                                b = math.fabs(P[0] + T[1][2])
                                vertices.append(Vertex(a, b))
                                if (DEBUG): print "'l' and 'curving' Block:\n\tP[1] = %f\n\tP[0] = %f\n\ttransform [0][2] = %f\n\ttransform [1][2] = %f\n\tx: %f, y: %f\n\n" % (P[1], P[0], T[0][2], T[1][2], a, b)
                                
                            absolute = False;
                        else:
                            if (Token == "C"):
                                if (curving):
                                    curving = False
                                    #vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
                                    a = math.fabs(P[1] + T[0][2])
                                    b = math.fabs(P[0] + T[1][2])
                                    vertices.append(Vertex(a, b))
                                    if (DEBUG): print "'C' and 'curving' Block:\n\tP[1] = %f\n\tP[0] = %f\n\ttransform [0][2] = %f\n\ttransform [1][2] = %f\n\tx: %f, y: %f\n\n" % (P[1], P[0], T[0][2], T[1][2], a, b)
                                
                                Token = StringTok.nextToken()
                                absolute = True
                                curving = True
                            else:
				if (Token == "c"):
                                    if (curving):
                                        curving = False;
					#vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
					a = math.fabs(P[1] + T[0][2])
                                        b = math.fabs(P[0] + T[1][2])
                                        vertices.append(Vertex(a, b))
                                        if (DEBUG): print "'c' and 'curving' Block:\n\tP[1] = %f\n\tP[0] = %f\n\ttransform [0][2] = %f\n\ttransform [1][2] = %f\n\tx: %f, y: %f\n\n" % (P[1], P[0], T[0][2], T[1][2], a, b)
				    absolute = False;
				    curving = True;
				else:
                                    if (DEBUG): print "Last Block:\n"
                                    if (curving):
                                        if (DEBUG): print "Curving is True"
                                    else:
                                        if (DEBUG): print "Curving is False"
                                        
                                    if (not curving):

                                        if (DEBUG): print "\tNot curving\n\tOld P[0] = %f\n\tOld P[1] = %f\n\tA[0][0] = %f\n\tA[1][0] = %f\n" % (P[0], P[1], A[0], A[1])
                                        P[0] = A[0];
                                        P[1] = A[1];
                                        if (DEBUG): print "\tNew P[0] = %f\n\tNew P[1] = %f\n\tToken: %s" % (P[0], P[1], Token)

                                        subStringTok = StringTokenizer(Token, ',| ');
                                        
					if (absolute) or (tokens == 1):

                                            #A[0][0] = Float.parseFloat(subStringTok.nextToken());
                                            A[0] = float(subStringTok.nextToken())
                                            if (DEBUG): print "\t\tabsolute or tokens == 1 Block\n\t\tsubStringTok: nextToken Was %f\n\t\tA[0][0] = %f\n" % (A[0], A[0])
                                            
					    try:
                                                #A[1][0] = Float.parseFloat(subStringTok.nextToken());
                                                A[1] = float(subStringTok.nextToken())
                                                if (DEBUG): print "A[1][0] =  %f" % (A[1])
                                                if (DEBUG): print "\tsubStringTok: nextToken Was %f\n\t\tTry A[1][0] =%f\n" % (A[1], A[1])
                                                
                                            except Exception:	
                                                #A[1][0] = Float.parseFloat(StringTok.nextToken());
                                                A[1] = float(StringTok.nextToken())
                                                if (DEBUG): print "A[1][0] =  %f" % (A[1])
                                                if (DEBUG): print "\tStringTok: nextToken Was %f\n\t\tCatch A[1][0] =%f\n" % (A[1], A[1])

					else:
                                            
                                            #A[0][0] += Float.parseFloat(subStringTok.nextToken());
                                            A[0] += float(subStringTok.nextToken())
                                            if (DEBUG): print "\t\tNot absolute or Not tokens == 1 Block\n\t\tsubStringTok: nextToken Was %f\n\t\tA[0][0] =%f\n" % (A[0], A[0])
						
					    try:
                                                #A[1][0] += Float.parseFloat(subStringTok.nextToken());
                                                A[1] += float(subStringTok.nextToken())
                                                if (DEBUG): print "\t\tTry A[1][0] =%f\n\t\tsubStringTok: nextToken Was %f\n"% (A[1], A[1])
                                            
                                            except Exception:
                                                #A[1][0] = Float.parseFloat(StringTok.nextToken())
                                                A[1] = float(StringTok.nextToken())
                                                if (DEBUG): print "\t\tCatch A[1][0] =%f\n\t\tStringTok: nextToken Was %f\n" % (A[1], A[1])

					A[2] = 1.0

                                        # USE MATRIX -- doesn't work quite right...
					# This should be able to invert the matrix and multiply, but it didn't work for some reason.
					useMatrix = False;
					if (useMatrix):
                                            if (DEBUG): print "Not Implemented Yet"
					    #float M[][] = MatrixBuddy.multiplyMatrices(MatrixBuddy.invertMatrix(transformMatrix(getTransform())), A);
					    #vertex.add(new PointF(Math.abs(M[0][0]), Math.abs(M[1][0])));

					# JUST SHIFT
					# All the matrices I dealt with were just a shift anyhow, so I hacked this.
					else:
					    try:
						#vertex.add(new PointF(Math.abs((A[1][0] + transformMatrix(getTransform())[0][2])), Math.abs((A[0][0] + transformMatrix(getTransform())[1][2]))));
                                                a = math.fabs(A[1] + T[0][2])
                                                b = math.fabs(A[0] + T[1][2])
                                                vertices.append(Vertex(a, b))
                                                if (DEBUG): print "'c' and 'curving' Block:\n\tA[1][0] = %f\n\tA[0][0] = %f\n\ttransform [0][2] = %f\n\ttransform [1][2] = %f\n\tx: %f, y: %f\n\n" % (A[1], A[0], T[0][2], T[1][2], a, b)
					    except Exception:
                                                print "Null Pointer Exception from Java"
    # End of while loop

    # Create the room list
    room = [roomType, roomName, vertices]
    global CURRENTMAP
    #debugFilename =  "floor_%d_%s_vertices(Python).txt" % ((CURRENTMAP + 1), roomName);
    #dumpVertexListToTextFile(roomName, vertices, debugFilename)

    return room

######################################################
# 
######################################################
def dumpVertexListToTextFile(roomName, vertexList, outputFilename):

    # Attempt to open the rooms file for writing
    try:
        #print "Attempting to open file '%s' for writing..." % (outputFilename)
        outputFile = open(outputFilename, 'w')
        outputFile.tell()
    except IOError:
        print "Error opening file for writing '%s'. Exiting..." % (outputFilename)
        return

    writeString = ""
    for v in vertexList:
        writeString += ("%.5f %.5f " % (v.x, v.y));

    writeString += "\nRoom Name: %s\nNumber of Vertices: %d\n" % (roomName, len(vertexList));

    outputFile.write("%s" % writeString)
    outputFile.close() 

######################################################
# Make a new room
######################################################
def makeRoom(roomNode):

    # Check if the attribute 'transform' is part of the current node, this means it is either a room or a hallway
    if "transform" in roomNode.attributes.keys():

        global numberOfRooms
        numberOfRooms += 1

        name      = roomNode.getAttribute('id')
        d         = roomNode.getAttribute('d')
        style     = roomNode.getAttribute('style')
        transform = roomNode.getAttribute("transform")

        #print "----------------------------------------\n"

        #if (name == ""):
##            name = "tag%d" % (numberOfRooms)
            #return None
            
##        print "name: %s" % (name)    
##        print "d: %s" % (d)
##        print "transform: %s" % (transform)
##        print "style: %s" % (style)
##        print "----------------------------------------\n"

        roomData = {'name' : name, 'd' : d, 'style' : style, 'transform' : transform}

        # Check if the current room is red
        if (style.find("fill:#ff0000") != -1) or (style.find("fill:rgb(100%,0%,0%)") != -1):
            room = findRoomVertices("Room", roomData)
            #print "%s is a RED room" % (name)
            return room

        else:
            if (style.find("fill:#ffff00") != -1) or (style.find("fill:rgb(100%,100%,0%)") != -1):
                room = findRoomVertices("Room", roomData)
                #print "%s is a YELLOW room" % (name)
                return room
    
            else:
                if style.find("fill:#") != -1:
                    room = findRoomVertices("Room", roomData)
                    #print "%s is another room that is NOT yellow or red" % (name)
                    return room

                else:
                    if style.find("stroke:#00ff00") != -1:
                        return None
                    else:
                        if (style.find("fill:none") != -1) and (style.find("stroke-width:0.4") != -1):

                            if style.find("stroke:rgb(25.097656%,39.607239%,92.156982%)") != -1:
                                tmp = 1 # Dummy Instruction
                                #print "%s is a HALLWAY" % (name)
                            else:
                                tmp = 1 # Dummy Instruction
                                #print "%s is a HALLWAY" % (name)
                            room = findRoomVertices("Hallway", roomData)
                            return room

    return None
    
######################################################
# Read the SVG file and extract the room data for the
# current floor plan.
######################################################
def parseSVG(svgFile):

    print "Attempting to parse file '%s' to extract the room data..." % (svgFile)

    try:
        svg  = parse(svgFile)
        print "SVG file '%s' was opened successfully." % (svgFile)
    except IOError:
        print "Specifed SVG file '%s' cannot be opened." % (svgFile)
        return

    root = svg.documentElement
    paths = root.getElementsByTagName('path')

    print "Number of Paths found: %d" % (len(paths))

    numRooms    = 0;
    numHallways = 0;

    listOfRooms = []

    i = 0
    for roomNode in paths:

        # For debugging purposes
##        pathId = roomNode.getAttribute('id')
##        if (pathId == debugPathId):
##            print "Found pathId '%s'" % (debugPathId)
##            room = makeRoom(roomNode)
##        else:
##            room = None
        # End if debugging purposes

        room = makeRoom(roomNode)

        # If the node is in fact a room
        if (room is not None):

            roomType = room[0]
            if (roomType == "Hallway"):
                numHallways += 1
            elif (roomType == "Room"):
                numRooms += 1
            else:
                print "NOT A ROOM OR HALLWAY"
            
            listOfRooms.append(room)

    if (len(listOfRooms) > 0):
        print "Rooms: (%d)\tHallways: (%d)" % (numRooms ,numHallways)
        writeRoomsDatafile(listOfRooms, svgFile)
    else:
        print "No Rooms were found in the file '%s'" % (svgFile)

######################################################
# Write the rooms data structure to the _room.txt
# data file.
######################################################
def writeRoomsDatafile(roomList, filename="default"):

    global CURRENTMAP

    print "filename: %s" % (filename)

    # Extract the filename without the extension
    indexOfSlash = filename.rfind("/");
    indexOfPeriod = filename.rfind(".", indexOfSlash, len(filename))
    filenameWithoutExtension =  filename[indexOfSlash+1:indexOfPeriod]
    outputFilename = filenameWithoutExtension + "_room_python_output.txt"

    # Attempt to open the rooms file for writing
    try:
        print "Attempting to open file '%s' for writing..." % (outputFilename)
        outputFile = open(outputFilename, 'w')
        outputFile.tell()
    except IOError:
        print "Error opening file for writing '%s'. Exiting..." % (outputFilename)
        return  

    print "File '%s' opened OK for writing. " % (outputFilename)

    # Scaling data for the SVG file taken from the class MapList.java
    mReadSvg = [[(1/0.795), (1/0.8),  0.0,  0.0],   # NSH Floor 1
                [(1/0.795), (1/0.8), -4.0, 0.0],    # NSH Floor 2
                [(1/0.795), (1/0.8), -4.0, 0.0]]    # NSH Floor 3

    # Scale factor corresponds to the conversion between points and pixels.
    scaleFactorRoomsy = mReadSvg[CURRENTMAP][0]
    scaleFactorRoomsx = mReadSvg[CURRENTMAP][1]

    # Shift factor was chosen to line up the svg better with the map. This number
    # was chosen by using the Show Rooms option in the Admin menu
    shiftFactorRoomsy = mReadSvg[CURRENTMAP][2]
    shiftFactorRoomsx = mReadSvg[CURRENTMAP][3]

    writeString = "";

    finalRoomList = []

    numHallways = 0
    numRooms    = 0

    print "Preparing to re-visit each found room to see if it is valid..."
    # for (int i = 0; i < H.rooms.size();)
    for room in roomList:

        roomType     = room[0]
        roomName     = room[1]
        roomVertices = room[2]

        # The third element in the room (index 2) is a list of vertices that define the room
        numberOfVerticesForRoom = len(roomVertices)

        # If the number of vertices is less than 3, it is not a room. It is either a line or a point.
        # Only process rooms that have 3 or more vertices
        if (numberOfVerticesForRoom > 2):

            if (roomType == "Hallway" or roomType == "Room"):

                if (roomType == "Hallway"):
                    numHallways += 1
                elif (roomType == "Room"):
                    numRooms += 1

                #print "Room/Hallway '%s' is valid. Adding to final list." % (roomName)
                writeString = roomType + " " + roomName + " "
		for v in roomVertices:
                    xValue = scaleFactorRoomsx * v.x + shiftFactorRoomsx
                    yValue = scaleFactorRoomsy * v.y + shiftFactorRoomsy
                    writeString += ("%.5f %.5f " % (xValue, yValue));

                outputFile.write("%s\n" % writeString)

##        else:
##            print "Room/Hallway '%s' not a valid room since it only has 2 or less vertices." % (roomName)

    outputFile.close();
    print "Final Number of Rooms: (%d)\tFinal Number of Hallways: (%d)" % (numRooms, numHallways)

######################################################
# Main Program
######################################################

# This script only works for the original floor plans from the SVN
# repository: svn://server.rcommerce.cs.cmu.edu/GoogleAI/. These
# floor plans include NSH floors one and two.

svgOrignialFileFromSVN_NSH_F1 = "./NavPal_Floor_Plans/Original_SVG_Files_From_SVN/nsh_1_f_vec.svg"

svgFile = svgOrignialFileFromSVN_NSH_F1

parseSVG(svgFile)


