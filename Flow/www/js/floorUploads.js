var wrapper = $('<div/>').css({height:0,width:0,'overflow':'hidden'});
var fileInput = $(':file').wrap(wrapper);
var THUMBWIDTH = 280.0;
var THUMBHEIGHT = 240.0;
var clickedAway = true
var isVisible = false


function createThumb(file) {
	var reader = new FileReader();
    reader.onload = function(event){
        var floorPlanImg = new Image();
        floorPlanImg.onload = function() {

        	var li = $('<li></li>').addClass('span4')
        	var thumb = $('<div></div>').addClass('thumbnail');
        	var floorPlan = $('<div></div>').addClass('floorPlan');
        	var caption = $('<div></div>').addClass('caption');
        	var label = $('<form class="form-horizontal">\
						  <div class="control-group">\
						    <label class="control-label" for="fileName">File Name:</label>\
						    <div class="controls">\
						      <p style="vertical-align:middle">'+ file.name +'</p>\
						    </div>\
						  </div>\
						  <div class="control-group">\
						    <label class="control-label" for="floorLabel">Floor Number:</label>\
						    <div class="controls">\
						      <input type="text" id="inputPassword" placeholder="###">\
						    </div>\
						  </div>\
						</form>');

			var widthRatio = THUMBWIDTH / floorPlanImg.width;
        	var heightRatio = THUMBHEIGHT / floorPlanImg.height;
        	var ratio = Math.min(widthRatio, heightRatio);
        	var width = floorPlanImg.width * ratio;
        	var height = floorPlanImg.height * ratio;
        	floorPlanImg = $(floorPlanImg).width(width + 'px').height(height + 'px').css('vertical-align', 'middle').addClass('floorPlanImg');
        	floorPlan.append(floorPlanImg);
        	floorPlanImg.popover({
			  html: true,
			  placement: 'top',
			  trigger: 'hover',
			  content: function (width, height) {
			    return $('<img class="hoverzoom" src="'+event.target.result+ '" />').height(height*2 + 'px');
			  },
			});

        	caption.append(label);
        	thumb.append(floorPlan);
        	thumb.append(caption);
            li.append(thumb);

            $('#gallary').append(li);
        }
        floorPlanImg.src = event.target.result;
    }
    reader.readAsDataURL(file);
    console.log(file);
}

$('#file').click(function(){
    fileInput.click();
}).show();

fileInput.change( function(e){
	var files = e.target.files
    for (var i=0; i < files.length; i++) {
    	createThumb(files[i]);
    }
});