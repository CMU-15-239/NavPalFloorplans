//grid.js

function Grid(walls, width) {
  this.topLeftPt;
  this.layout = [];
  this.walls = walls;
  
  var inSign = '1';
  var outSign = '0';
  
  if(util.exists(this.walls) && this.walls.length > 0) {
    var startingPt = this.walls[0].p1;
    //console.log(startingPt);
    if(util.exists(startingPt)) {
      var tempLayout = [];
      var topLen = -1;
      var bottomLen = 1;
      
      
      this.fill(startingPt.y + 0, tempLayout, 0, width, inSign, outSign);
      
      while(this.fill(startingPt.y + topLen, tempLayout, topLen,
              width, inSign, outSign)) {
        topLen--;
      }
      
      //console.log(topLen);
      
      
      while(this.fill(startingPt.y + bottomLen, tempLayout, bottomLen,
              width, inSign, outSign)) {
        bottomLen++
      }
      
      //console.log(bottomLen);
      
      for(var i = topLen+1; i <= bottomLen; i++) {
        //console.log(tempLayout[i]);
        this.layout.push(tempLayout[i]);
      }
      
      this.topLeftPt = new Point(0, startingPt.y + topLen + 1);
    }
  }
}

Grid.prototype.addEdge = function(otherNode) {
  if(util.exists(otherNode.lineRep)) {
    var pts = otherNode.lineRep.getPointsRep();
    for(var p = 0; p < pts.length; p++) {
      var pt = pts[p];
      var y = pt.y - this.topLeftPt.y;
      var x = pt.x - this.topLeftPt.x
      
      if(0 <= y && y < this.layout.length && util.exists(this.layout[y])
          && 0 <= x && x < this.layout[y].length) {
        
        this.layout[y][x] = otherNode.id;
      }
    }
    
  } else if(util.exists(otherNode.pointRep)) {
    var y = otherNode.pointRep.y - this.topLeftPt.y;
    var x = otherNode.pointRep.x;
    if(x >= 0 && y >= 0) {
      if(util.exists(this.layout[y]) && x < this.layout[y].length) {
        this.layout[y][x] = otherNode.id;
      }
    } else {
      console.log("edge not in layout");
    }
  }
};

Grid.prototype.fill = function(y, layout, at, width, inSign, outSign) {
  var inShape = false;
  var filled = false;
  var row = 1;
  layout[at] = [];
  for(var xr = 0; xr < width; xr++) {
    var numLines = util.pointNearLines(this.walls, new Point(xr, y), 0);
    //console.log(numLines);
    if(numLines > 0) {
      if(numLines === 1) {
        inShape = !inShape
      } else {
        inShape = false;
        console.log("numLines: " + numLines + " " + xr + ", " + y);
      }
      
      layout[at][xr] = outSign; //walls are obstacles too!
      if(y == row) {
        console.log('out1 ' + xr + ', ' + y);
      }
    } else if(inShape) {
      if(y == row) {
        console.log('numLines: ' + numLines + ' in ' + xr + ', ' + y);
      }
      layout[at][xr] = inSign;
      filled = true;
    } else {
      if(y == row) {
        console.log('numLines: ' + numLines + ' out2 ' + xr + ', ' + y);
      }
      layout[at][xr] = outSign;
    }
  }
  
  return filled;
};

Grid.prototype.toOutput = function() {
  return {
    topLeftPt: this.topLeftPt.toOutput(),
    layout: this.layout
  };
};

//for debugging
Grid.prototype.draw = function($out) {
  for(var y = 0; y < this.layout.length; y++) {
    $out.append('<br>');
    for(var x = 0; x < this.layout[y].length; x++) {
      var val = this.layout[y][x];

      $out.append(val[0].toUpperCase() + ' ');
    }
  }
};

