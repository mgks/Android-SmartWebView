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
function getUrlVars(){for(var t,e=[],i=window.location.href.slice(window.location.href.indexOf("?")+1).split("&"),r=0;r<i.length;r++)t=i[r].split("="),e.push(t[0]),e[t[0]]=t[1];return e}
