import re
import math

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

######################################################
# Convert the string representation of the transform
# to an actual matrix
######################################################
def getTransform(T):

    st = StringTokenizer(T, ' |,|\(|\)')

    print st.getList()
    print st.dStr

    nextToken = st.nextToken()
    if st.hasMoreTokens() and ( nextToken == "matrix"):

        A = [[0.0, 0.0, 0.0], [0.0, 0.0, 0.0], [0.0, 0.0, 0.0]]

        i = -1
	while (st.hasMoreTokens() and i < 6):
            i += 1
            nextToken = st.nextToken()
            print nextToken
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
def findVertices(roomType, roomData):

    # Get the name of the room
    roomName = roomData['name']

    # Prepare the 'd' string for parsing so the room geometry can be extracted
    d = roomData['d']
    StringTok = StringTokenizer(d, ',| ')

    # Convert the transform into an actual matrix
    transform = roomData['transform'];
    T = getTransform(transform)

    absolute = False;
    curving  = True;
    A = [0.0, 0.0, 0.0]
    P = [0.0, 0.0]
    tokens = 0;

    vertices = []

    while (StringTok.hasMoreTokens()):

        Token = StringTok.nextToken()
        tokens += 1

        if (Token == "m"):
            print "m"
            absolute = False
        else:
            if (Token == "M"):
                print "M"
                absolute = True
            else:
                if (Token.lower() == "z"):
                    print "z"
                    if (curving):
                        curving = False
                        # TODO: Add a new vertex to the vertex list
                        a = math.fabs(A[1] + T[0][2])
                        b = math.fabs(A[0] + T[1][2])
                        vertices.append(Vertex(a, b))
                        #print "(%f, %f)" % (a, b)
                    break
                else:
                    if (Token == "L"):
                        print "L"
                        if (curving):
                            curving = False
                            # TODO: Add a new vertex to the vertex list
                            #vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
                            a = math.fabs(A[1] + T[0][2])
                            b = math.fabs(A[0] + T[1][2])
                            vertices.append(Vertex(a, b))
                            
                        absolute = True
                    else:
                        if (Token == "l"):
                            print "l"
                            if (curving):
                                curving = false;
                                # TODO: Add a new vertex to the vertex list
                                #vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
                                a = math.fabs(A[1] + T[0][2])
                                b = math.fabs(A[0] + T[1][2])
                                vertices.append(Vertex(a, b))
                                
                            absolute = False;
                        else:
                            if (Token == "C"):
                                print "C"
                                if (curving):
                                    curving = false
                                    # TODO: Add a new vertex to the vertex list
                                    #vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
                                    a = math.fabs(A[1] + T[0][2])
                                    b = math.fabs(A[0] + T[1][2])
                                    vertices.append(Vertex(a, b))
                                
                                Token = StringTok.nextToken()
                                absolute = True
                                curving = True
                            else:
				if (Token == "c"):
                                    print "c"
                                    if (curving):
                                        curving = false;
					# TODO: Add a new vertex to the vertex list
					#vertex.add(new PointF(Math.abs((int) (P[1] + transformMatrix(getTransform())[0][2])), Math.abs((int) (P[0] + transformMatrix(getTransform())[1][2]))));
				    absolute = False;
				    curving = True;
				else:
                                    if (not curving):
                                        P[0] = A[0]; # In Java code this is a 3x1 matrix
                                        P[1] = A[1];
                                        subStringTok = StringTokenizer(Token, ' ,');
                                        
					if (absolute) or (tokens == 1):

                                            #A[0][0] = Float.parseFloat(subStringTok.nextToken());
                                            A[0] = float(subStringTok.nextToken())
                                            #print "A[0][0] =  %f" % (A[0])
                                            
					    try:
                                                #A[1][0] = Float.parseFloat(subStringTok.nextToken());
                                                A[1] = float(subStringTok.nextToken())
                                                #print "A[1][0] =  %f" % (A[1])
                                                
                                            except Exception:	
                                                #A[1][0] = Float.parseFloat(StringTok.nextToken());
                                                A[1] = float(StringTok.nextToken())
                                                #print "A[1][0] =  %f" % (A[1])
					else:
                                            
                                            #A[0][0] += Float.parseFloat(subStringTok.nextToken());
                                            A[0] += float(subStringTok.nextToken())
					    try:
                                                #A[1][0] += Float.parseFloat(subStringTok.nextToken());
                                                A[1] += float(subStringTok.nextToken())
                                                #print "A[1][0] =  %f" % (A[1])
                                            
                                            except Exception:
                                                #A[1][0] = Float.parseFloat(StringTok.nextToken())
                                                A[1] = float(StringTok.nextToken())
                                                #print "A[1][0] =  %f" % (A[1])

					A[2] = 1.0

                                        # USE MATRIX -- doesn't work quite right...
					# This should be able to invert the matrix and multiply, but it didn't work for some reason.
					useMatrix = False;
					if (useMatrix):
                                            print "Not Implemented Yet"
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
					    except Exception:
                                                print "Null Pointer Exception from Java"
    # End of while loop

    # Create the room list
    room = [roomType, roomName, vertices]

    return room

# End of findVertices()
######################################################

######################################################
# Write the rooms data structure to the _room.txt
# data file.
######################################################
def writeRoomsDatafile(roomList, filename="default"):

    # Attempt to open the file for writing
    try:
        outputFile = open(filename, 'w')
        outputFile.tell()
    except IOError:
        print "Error opening file for writing '%s'. Exiting..." % (filename)
        return  

    # Scaling data for the SVG file taken from the class MapList.java
    mReadSvg = [[(1/0.795), (1/0.8),  0.0,  0.0],
                [(1/0.795), (1/0.8), -4.0, 0.0]]

    CURRENTMAP = 0

    print mReadSvg

    # Extract the filename without the extension
    indexOfPeriod = filename.find('.')
    filenameWithoutExtension =  filename[0:indexOfPeriod]
    print "File Opened OK. Creating file '%s_rooms.txt'" % (filenameWithoutExtension)
	    
    # Scale factor corresponds to the conversion between points and pixels.
    scaleFactorRoomsy = mReadSvg[CURRENTMAP][0]
    scaleFactorRoomsx = mReadSvg[CURRENTMAP][1]

    # Shift factor was chosen to line up the svg better with the map. This number
    # was chosen by using the Show Rooms option in the Admin menu
    shiftFactorRoomsy = mReadSvg[CURRENTMAP][2]
    shiftFactorRoomsx = mReadSvg[CURRENTMAP][3]

    writeString = [];
    res = 13;

    finalRoomList = []

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

                writeString = roomType + " " + roomName
		for v in roomVertices:
                    xValue = scaleFactorRoomsx * v.x + shiftFactorRoomsx
                    yValue = scaleFactorRoomsy * v.y + shiftFactorRoomsy
                    writeString += xValue + " " + yValue + " ";

                outputFile.write("%s\n" % writeString)
		
    outputFile.close();

def testRoomDataStructure():

    roomList = []
    vertices = []
    vertices.append(Vertex(10, 10))
    vertices.append(Vertex(20, 20))
    vertices.append(Vertex(30, 30))
    vertices.append(Vertex(40, 40))
    room = ["Room", "path1000", vertices]
    roomList.append(room)

    vertices = []
    vertices.append(Vertex(100, 100))
    vertices.append(Vertex(200, 200))
    vertices.append(Vertex(300, 300))
    vertices.append(Vertex(400, 400))
    room = ["Room", "path1001", vertices]
    roomList.append(room)

    vertices = []
    vertices.append(Vertex(11, 11))
    vertices.append(Vertex(22, 22))
    vertices.append(Vertex(33, 33))
    vertices.append(Vertex(44, 44))
    room = ["Room", "path1002", vertices]
    roomList.append(room)

    vertices = []
    vertices.append(Vertex(15, 15))
    vertices.append(Vertex(25, 25))
    vertices.append(Vertex(35, 35))
    vertices.append(Vertex(45, 45))
    room = ["Room", "path1003", vertices]
    roomList.append(room)

    for room in roomList:
        roomType = room[0]
        roomName = room[1]
        vertices = room[2]
        for v in vertices:
            print "\t(%f, %f)" % (v.x, v.y)

        print "\n"

        print "Room Type: %s, Room Name: %s" % (roomType, roomName)

######################################################
# Main Program
######################################################

# Sample room data from XML File
name="path9311"
d="m 4.6875e-4,0.00125 0,21.058594 4.50000025,0 0,4.5 -4.50000025,0 0,29.21875 45.66015625,0 0,-54.777344 z m 0,0"
style="fill:#ffff00;fill-opacity:1;fill-rule:evenodd;stroke:#6b6b6b;stroke-width:0.41800001;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:10;stroke-opacity:1"
transform="matrix(0,1,1,0,315.78,384.48)"

roomData = {'name' : name, 'd' : d, 'style' : style, 'transform' : transform}

##print getTransform(transform)

##findVertices(type, roomData)


##filename = "mytestfile.txt"
##listToWrite = ['How', 'Now', 'Brown', 'Cow']
##writeRoomsDatafile(listToWrite, filename)

testRoomDataStructure()


##
## Parsing Test
##
##str1 = "1,2 3, 4, 5, 6 7 8 9 0"
##
##strTok1 = StringTokenizer(str1, ',| ')
##strTok2 = StringTokenizer(str1, ', ')
##
##print strTok1.getList()
##print strTok2.getList()
