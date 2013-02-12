var imageLoader = $('#uploadButton');
imageLoader.click(uploadImage);

function uploadImage(e){
	var url = "authoringTool.html";    
	$(location).attr('href',url);
	$(document).ready(function () 
	{
	    var reader = new FileReader();
	    reader.onload = function(event){
	        window.FLOOR_PLAN = new Image();
	        window.FLOOR_PLAN.src = event.target.result;
	    }
	    reader.readAsDataURL(e.target.files[0]); 
	}    
}