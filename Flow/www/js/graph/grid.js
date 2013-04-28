/*
grid.js
By Vansi Vallabhaneni
*/

function Grid(spaceNode, width) {
  this.topLeftPt;
  this.layout = [];
  this.spaceNode = spaceNode;
  this.walls = [];
  
  var inSign = '1';
  var outSign = '0';
  
  if(util.exists(spaceNode) && util.exists(spaceNode.walls) && spaceNode.walls.length > 0) {
    this.walls = spaceNode.walls;
    var startingPt = this.walls[0].p1;
    if(util.exists(startingPt)) {
      var tempLayout = [];
      var topLen = -1;
      var bottomLen = 1;
      
      //this is a special case since the starting point may be a vertex
      this.fill(startingPt.y + 0, tempLayout, 0, width, inSign, outSign);
      
      while(this.fill(startingPt.y + topLen, tempLayout, topLen,
              width, inSign, outSign)) {
        topLen--;
      }
      
      
      while(this.fill(startingPt.y + bottomLen, tempLayout, bottomLen,
              width, inSign, outSign)) {
        bottomLen++
      }
      
      console.log(bottomLen);
      
      for(var i = topLen+1; i < bottomLen; i++) {
        this.layout.push(tempLayout[i]);
      }
      
      this.topLeftPt = new Point(0, startingPt.y + topLen + 1);
    }
  }
  
  this.spaceNode = null;
}

/**
  * Summary: Adds the edge to the physical layout.
  * Paramters: otherNode: FloorNode
  * Returns: undefined
**/
Grid.prototype.addEdge = function(otherNode) {
  var pts = [];
  if(util.exists(otherNode.lineRep)) {
    pts = otherNode.lineRep.getPointsRep();
  } else if(util.exists(otherNode.pointRep)) {
    var pts = [otherNode.pointRep];
  }
  
  for(var p = 0; p < pts.length; p++) {
    var pt = pts[p];
    var y = pt.y - this.topLeftPt.y;
    var x = pt.x - this.topLeftPt.x
    
    if(0 <= y && y < this.layout.length && util.exists(this.layout[y])
        && 0 <= x && x < this.layout[y].length) {
      
      this.layout[y][x] = otherNode.id;
    }
  }
};

/**
  * Summary: Fills the given 'layout' with 'inSign' at the given 'at' based off the walls' 'y'
      using ray casting with a max search width of 'width' and the rest is filled with 'outSign'
  * Parameters: y: int
                layout: [[String]], 2d array of Strings
                at: int
                width: int
                inSign: String
                outSign: String
  * Returns: boolean, if it filled anything
**/
Grid.prototype.fill = function(y, layout, at, width, inSign, outSign) {
  var filled = false;
  var row = 11;
  layout[at] = [];
  
  var wallXs = [];
  
  for(var xr = 0; xr < width; xr++) {
    if(this.spaceNode.pointOnWalls(new Point(xr, y), 0.5)) {
      if(wallXs.length > 0 && (xr - wallXs[wallXs.length-1]) === 1) {
        wallXs.pop();
      }
      
      wallXs.push(xr);
    }
  }
  
  console.log("y: " + y + " x: " + JSON.stringify(wallXs));
  
  var inShape = false;
  if(wallXs.length % 2 !== 0) {
    wallXs.pop();
  }
  
  var compareIdx = 0;
  for(var xr = 0; xr < width; xr++) {
    if(xr === wallXs[compareIdx]) {
      inShape = !inShape;
      layout[at][xr] = outSign;
      compareIdx++;
    } else if(inShape) {
      layout[at][xr] = inSign;
      filled = true;
    } else {
      layout[at][xr] = outSign;
    }
  }
  
  return filled;
};

/**
  * Summary: Determines if the 'line' is different from the 'lines' are "different" based off our metric.
  * Parameters: line: Line
                lines: [Line], array of Lines
  * Returns: boolean
**/
Grid.prototype.isDifferentLine = function(line, lines) {
  if(line.isParallelToOne(lines)) {return false;}
    
  var lineLeftMostX = Math.min(line.p1.x, line.p2.x);
  
  for(var l = 0; l < lines.length; l++) {
    var thisLeftMostX = Math.min(lines[l].p1.x, lines[l].p2.x);
    if(lineLeftMostX === thisLeftMostX) {
      return false;
    }
  }
  
  return true;
};

/**
  * Summary: Finds the number of 'lines' with in the 'radius' of the 'point'.
  * Parameters: lines: [Line], array of Lines
                point: Point
                radius: int
  * Returns: int
**/
Grid.prototype.pointNearLines = function(lines, point, radius) {
  var nearLines = [];
  if(util.exists(lines)) {
    for(var l = 0; l < lines.length; l++) {
      if(lines[l].pointNearLine(point, radius)) {
      
        if(this.isDifferentLine(lines[l], nearLines)) {
          nearLines.push(lines[l]);
        }
      }
    }
  }
  
  
	return nearLines.length;
};

/**
  * Summary: Constructs a JSON object reperesenting the grid.
  * Parameters: undefined
  * Returns: Object
**/
Grid.prototype.toOutput = function() {
  return {
    topLeftPt: this.topLeftPt.toOutput(),
    layout: this.layout
  };
};

/**
  * Summary: Draws the grid for debugging.
**/
Grid.prototype.draw = function($out) {
  for(var y = 0; y < this.layout.length; y++) {
    $out.append('<br>');
    for(var x = 0; x < this.layout[y].length; x++) {
      var val = this.layout[y][x];

      $out.append(val[0].toUpperCase() + ' ');
    }
  }
};

