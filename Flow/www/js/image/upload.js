var imageLoader = document.getElementById('imageLoader');
    imageLoader.addEventListener('change', uploadImage, false);;

function uploadImage(e){
    var reader = new FileReader();
    reader.onload = function(event){
        window.FLOOR_PLAN = new Image();
        FLOOR_PLAN.src = event.target.result;
        console.log(FLOOR_PLAN);
    }
    reader.readAsDataURL(e.target.files[0]);     
}
