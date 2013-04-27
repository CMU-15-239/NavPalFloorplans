window.util = {}

window.util.exists = function(obj){
    return obj !== undefined && obj !== null;
}

window.util.getTapEvent = function(){
	if(this.isAndroid()) {return " touchleave";}
	else {return " touchstart";}
};

window.util.isIOS = function(){
    return  !!(navigator.userAgent.match(/iPhone/i) ||
            navigator.userAgent.match(/iPod/i) ||
            navigator.userAgent.match(/iPad/i));
}

window.util.isAndroid = function(){
    return !!(navigator.userAgent.match(/Android/));
}

window.util.isChrome = function(){
    return !!(navigator.userAgent.match(/Chrome/));
}

window.util.patchFnBind = function(){
    if (Function.prototype.bind === undefined){
       Function.prototype.bind = function (bind) {
            var self = this;
            return function () {
                var args = Array.prototype.slice.call(arguments);
                return self.apply(bind || null, args);
            };
        };
    }
}

window.util.patchRequestAnimationFrame = function(){
    window.requestAnimationFrame =
            window.requestAnimationFrame ||
            window.mozRequestAnimationFrame ||
            window.webkitRequestAnimationFrame ||
            window.msRequestAnimationFrame ||
            function(fn){
                setTimeout(function(){
                    fn(Date.now());
                }, 1000/60);
            };
}

window.util.deltaTimeRequestAnimationFrame = function(toBeCalled){
    var addTimeDiff = function(time){
        if (window.util.lastRequestAnimationFrame !== undefined){
            toBeCalled(time - window.util.lastRequestAnimationFrame);
        }
        window.util.lastRequestAnimationFrame = time;
        window.requestAnimationFrame(addTimeDiff);
    }
    window.requestAnimationFrame(addTimeDiff);
}

window.util.isEventSupported = (function(){
    var TAGNAMES = {
      'select':'input','change':'input',
      'submit':'form','reset':'form',
      'error':'img','load':'img','abort':'img'
    }
    function isEventSupported(eventName) {
      var el = document.createElement(TAGNAMES[eventName] || 'div');
      eventName = 'on' + eventName;
      var isSupported = (eventName in el);
      if (!isSupported) {
        el.setAttribute(eventName, 'return;');
        isSupported = typeof el[eventName] == 'function';
      }
      el = null;
      return isSupported;
    }
    return isEventSupported;
})();

window.util.makeAspectRatioCanvas = function(parent, aspectRatio){
    var canvas = $("<canvas></canvas>");
    var calculateDims = function(aspectRatio, maxWidth, maxHeight){
        var width = Math.min(maxWidth, maxHeight*aspectRatio);
        var height = Math.min(maxHeight, maxWidth/aspectRatio);
        return { 'width': width, 'height': height };
    };
    parent.append(canvas);
    var dimensions = calculateDims(aspectRatio, parent.width(), parent.height());
    canvas.prop('width', dimensions.width);
    canvas.prop('height', dimensions.height);
    canvas.html("Your browser doesn't support canvas :(");
    return canvas;
};

window.util.isValidEmail = function(email) {
    var regex = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
    if(regex.test(email)) {
        return true;
    } else {
        return false;
    }
};

window.util.isValidInput = function(input) {
    if(input.match(/^[a-zA-Z0-9]+$/)) {
        return input;
    } else {
        return "";
    }

};

window.util.distanceSquared = function(x1, y1, x2, y2) {
	var dx = x2-x1;
	var dy = y2-y1;
	return dx*dx + dy*dy;
};

window.util.distance = function(x1, y1, x2, y2) {
	return Math.sqrt(this.distanceSquared(x1, y1, x2, y2));
};

/**
 * Summary: creates a hash for a string
 * Parameters: none
 * Returns: hash value
**/
String.prototype.hashCode = function() {
    var hash = 0, i, char;
    if (this.length == 0) return hash;
    for (i = 0; i < this.length; i++) {
        char = this.charCodeAt(i);
        hash = ((hash<<5)-hash)+char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return "h" + hash;
};

Array.prototype.alphanumSort = function(caseInsensitive) {
  for (var z = 0, t; t = this[z]; z++) {
    this[z] = [];
    var x = 0, y = -1, n = 0, i, j;

    while (i = (j = t.charAt(x++)).charCodeAt(0)) {
      var m = (i == 46 || (i >=48 && i <= 57));
      if (m !== n) {
        this[z][++y] = "";
        n = m;
      }
      this[z][y] += j;
    }
  }

  this.sort(function(a, b) {
    for (var x = 0, aa, bb; (aa = a[x]) && (bb = b[x]); x++) {
      if (caseInsensitive) {
        aa = aa.toLowerCase();
        bb = bb.toLowerCase();
      }
      if (aa !== bb) {
        var c = Number(aa), d = Number(bb);
        if (c == aa && d == bb) {
          return c - d;
        } else return (aa > bb) ? 1 : -1;
      }
    }
    return a.length - b.length;
  });

  for (var z = 0; z < this.length; z++)
    this[z] = this[z].join("");
}
