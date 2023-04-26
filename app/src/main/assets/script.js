/*
 * Smart WebView 7.0 (May 2023)
 * Smart WebView is an Open Source project that integrates native features into webview to help create advanced hybrid applications. Available on GitHub (https://github.com/mgks/Android-SmartWebView).
 * Initially developed by Ghazi Khan (https://github.com/mgks) under MIT Open Source License.
 * This program is free to use for private and commercial purposes under MIT License (https://opensource.org/licenses/MIT).
 * Please mention project source or developer credits in your Application's License(s) Wiki.
 * Contribute to the project (https://github.com/mgks/Android-SmartWebView/discussions)
 * Sponsor the project (https://github.com/sponsors/mgks)
 * Giving right credits to developers encourages them to keep improving their projects :)
 */

document.querySelector(document).ready(function(){
    var imagesPreview = function(input, placeToInsertImagePreview) {
        if (input.files) {
            var filesAmount = input.files.length;
            for (i = 0; i < filesAmount; i++) {
                var reader = new FileReader();
                reader.onload = function(event) {
                    document.querySelector($.parseHTML('<img>')).attr('src', event.target.result).appendTo(placeToInsertImagePreview);
                }
                reader.readAsDataURL(input.files[i]);
            }
        }
    };
    document.querySelector('#gallery-photo-add').addEventListener('change', function() {
    	document.querySelector(".gallery").html("");
        imagesPreview(this, 'div.gallery');
    });
	var loc = getUrlVars()["loc"].split(',');
	document.querySelector(".locf").replaceWith("Latitude: "+loc[0]+"<br>Longitude: "+loc[1]);
})
function getUrlVars(){
	for(var t,e=[],i=window.location.href.slice(window.location.href.indexOf("?")+1).split("&"),r=0;r<i.length;r++)t=i[r].split("="),e.push(t[0]),e[t[0]]=t[1];return e
}
