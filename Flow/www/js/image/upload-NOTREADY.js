var imageLoader = document.getElementById('imageLoader');
    imageLoader.addEventListener('change', uploadImage, false);;

function uploadImage(e){
    var reader = new FileReader();
    reader.onload = function(event){
	    var base64Encoding = event.target.result;
	    console.log(base64Encoding)
	     $.ajax({ 
                type: "POST", 
                url : '/upload', 
                data: {
                	image: base64Encoding
                }
        }).done(function(res) {
        	console.log(res);
        }); 
    }
    reader.readAsDataURL(e.target.files[0]);     
}

// function uploadImage(e){
//     var reader = new FileReader();
//     reader.onload = function(event){
//         window.FLOOR_PLAN = new Image();
//         FLOOR_PLAN.onload = function() {
// 	        var orgWidth = this.width;
// 	        var orgHeight = this.height;
// 	        var ratio = CANVAS_WIDTH/orgWidth;
// 	        this.width = CANVAS_WIDTH;
// 	        this.height = orgHeight * ratio;
// 	        CANVAS_HEIGHT = orgHeight * ratio;
// 	        $('#canvas').height(CANVAS_HEIGHT)
// 	        $('#buttons').height(CANVAS_HEIGHT)
// 	        $('#canvas').width(CANVAS_WIDTH)
// 	        can.width = CANVAS_WIDTH;
// 			can.height = CANVAS_HEIGHT;
// 			CANVAS.width = CANVAS_WIDTH;
// 			CANVAS.height = CANVAS_HEIGHT;
// 			CANVAS.x = can.offsetLeft;
// 			CANVAS.y = can.offsetTop;
// 	    }
// 	    FLOOR_PLAN.src = event.target.result;
//     }
//     reader.readAsDataURL(e.target.files[0]);     
// }