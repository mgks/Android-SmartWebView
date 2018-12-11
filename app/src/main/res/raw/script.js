$(document).ready(function(){
    var imagesPreview = function(input, placeToInsertImagePreview) {
        if (input.files) {
            var filesAmount = input.files.length;
            for (i = 0; i < filesAmount; i++) {
                var reader = new FileReader();
                reader.onload = function(event) {
                    $($.parseHTML('<img>')).attr('src', event.target.result).appendTo(placeToInsertImagePreview);
                }
                reader.readAsDataURL(input.files[i]);
            }
        }
    };
    $('#gallery-photo-add').on('change', function() {
    	$(".gallery").html("");
        imagesPreview(this, 'div.gallery');
    });
	var loc = getUrlVars()["loc"].split(',');
	$(".locf").replaceWith("Latitude: "+loc[0]+"<br>Longitude: "+loc[1]);
})
