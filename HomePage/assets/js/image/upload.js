$(document).ready(function () 
{
    var imageLoader = document.getElementById('imageLoader');
    imageLoader.addEventListener('change', uploadImage, false);
    var canvas = document.getElementById('canvas');
    var ctx = canvas.getContext('2d');
}

function uploadImage(e){
    var reader = new FileReader();
    reader.onload = function(event){
        window.FLOOR_PLAN = new Image();
        FLOOR_PLAN.onload = function(){
            ctx.drawImage(FLOOR_PLAN,0,0, canvas.width, canvas.height);
        }
        FLOOR_PLAN.src = event.target.result;
    }
    reader.readAsDataURL(e.target.files[0]);     
}
