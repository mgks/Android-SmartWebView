function readURL(e){if(e.files&&e.files[0]){var r=new FileReader;r.onload=function(e){$("#impr").removeClass("hidden"),$("#impr").attr("src",e.target.result)},r.readAsDataURL(e.files[0])}}
function getUrlVars(){
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}
$(document).ready(function(){
	var loc = getUrlVars()["loc"].split(',');
	$(".locf").replaceWith("Latitude: "+loc[0]+"<br>Longitude: "+loc[1]);
})
