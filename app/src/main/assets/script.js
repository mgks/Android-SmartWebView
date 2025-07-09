/*
  Smart WebView v7 - Offline Script
  https://github.com/mgks/Android-SmartWebView
*/

document.addEventListener('DOMContentLoaded', function() {

    const imageInput = document.getElementById('add-img');
    const gallery = document.querySelector('.gallery');
    const MAX_WIDTH = 240;

    if (imageInput) {
        imageInput.addEventListener('change', function() {
            if (gallery) gallery.innerHTML = ''; // clear previous previews
            if (!this.files) return;

            for (const file of Array.from(this.files)) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.onload = function() {
                        const canvas = document.createElement('canvas');
                        const ctx = canvas.getContext('2d');
                        let width = img.width;
                        let height = img.height;

                        if (width > MAX_WIDTH) {
                            height *= MAX_WIDTH / width;
                            width = MAX_WIDTH;
                        }
                        canvas.width = width;
                        canvas.height = height;
                        ctx.drawImage(img, 0, 0, width, height);
                        if (gallery) gallery.appendChild(canvas);
                    }
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // This function is triggered by the getloc: link
    window.get_location = function() {
        // The native code will handle the location fetching and push the result back.
        // We can listen for a custom event or have native code directly manipulate the DOM.
        // For simplicity, let's assume native code will call a function like `updateLocationDisplay`.
        const locElement = document.querySelector('.fetch-loc');
        if (locElement) {
            locElement.innerHTML = "Fetching location from device...";
        }
    };

    // This function can be called by native code after location is fetched.
    window.updateLocationDisplay = function(lat, long) {
        const locElement = document.querySelector('.fetch-loc');
        if (locElement) {
            locElement.innerHTML = "<b>Latitude:</b> " + lat.toFixed(6) + "<br><b>Longitude:</b> " + long.toFixed(6);
        }
    };
});