/*
  Smart WebView v8 - Offline Script
  https://github.com/mgks/Android-SmartWebView
*/

// This variable will store the theme detected by the native app
let nativeThemePreference = 'light';

document.addEventListener('DOMContentLoaded', function() {

    const imageInput = document.getElementById('add-img');
    const gallery = document.querySelector('.gallery');
    const MAX_WIDTH = 240;

    if (imageInput) {
        imageInput.addEventListener('change', function() {
            // The 'gallery' element might not exist on all pages (like error pages).
            const gallery = document.querySelector('.gallery');
            if (gallery) {
                gallery.innerHTML = ''; // Clear previous previews only if gallery exists.
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
                            // The gallery is guaranteed to exist inside this block.
                            gallery.appendChild(canvas);
                        }
                    };
                    reader.readAsDataURL(file);
                }
            }
        });
    }

    // This function can be called by native code after location is fetched.
    window.updateLocationDisplay = function(lat, long) {
        const locElement = document.querySelector('.fetch-loc');
        if (locElement) {
            if (lat && long) {
                locElement.innerHTML = "<b>Latitude:</b> " + lat.toFixed(6) + "<br><b>Longitude:</b> " + long.toFixed(6);
            } else {
                locElement.innerHTML = "Could not retrieve location. Please ensure GPS is enabled and permissions are granted.";
            }
        }
    };

    // Theme switcher logic
    const themeSwitcher = document.getElementById('theme-switcher');
    if (themeSwitcher) {
        themeSwitcher.addEventListener('click', (event) => {
            if (event.target.tagName === 'BUTTON') {
                const theme = event.target.dataset.theme;
                setTheme(theme);
            }
        });
    }
    const savedTheme = localStorage.getItem('swv-theme');
    if (savedTheme && savedTheme !== 'system') {
        setTheme(savedTheme);
    } else {
        setTheme(nativeThemePreference, true);
    }
});

function fetchLocation() {
    const locElement = document.querySelector('.fetch-loc') || document.querySelector('.fetch-loc-area');
    if (locElement) {
        locElement.innerHTML = "<div class='fetch-loc'>Fetching location from device...</div>";
    }
    // Call the new, non-conflicting object name
    if (window.SWVLocation) {
        window.SWVLocation.getCurrentPosition(function(lat, lng, error) {
            // In offline.html, updateLocationDisplay is global.
            // In docs/script.js, this logic is inside fetchLocation.
            // We'll make it robust for both.
            const displayDiv = document.querySelector('.fetch-loc') || document.querySelector('.fetch-loc-area');
            if (error) {
                displayDiv.innerHTML = "<div class='fetch-loc'><b>Error:</b> " + error + "</div>";
                return;
            }
            if (lat && lng) {
                displayDiv.innerHTML = "<div class='fetch-loc'><b>Latitude:</b> " + lat.toFixed(6) + "<br><b>Longitude:</b> " + lng.toFixed(6) + "</div>";
            }
        });
    } else {
        alert("Location feature is not available.");
    }
}

function applyInitialTheme(nativeTheme) {
    if (nativeTheme) {
        nativeThemePreference = nativeTheme;
    }
}

function setTheme(theme, isSystem = false) {
    const body = document.body;
    const themeSwitcher = document.getElementById('theme-switcher');
    let activeTheme = theme;
    let buttonToActivate = theme;

    if (theme === 'system') {
        localStorage.removeItem('swv-theme');
        activeTheme = nativeThemePreference;
        isSystem = true;
        buttonToActivate = 'system';
    } else {
        localStorage.setItem('swv-theme', theme);
    }

    if (activeTheme === 'dark') {
        body.classList.add('dark-mode');
    } else {
        body.classList.remove('dark-mode');
    }

    if (themeSwitcher) {
        themeSwitcher.querySelectorAll('button').forEach(btn => btn.classList.remove('active'));
        const activeButton = themeSwitcher.querySelector(`[data-theme="${buttonToActivate}"]`);
        if (activeButton) activeButton.classList.add('active');
    }
    if (window.AndroidInterface && typeof window.AndroidInterface.setNativeTheme === 'function') {
        window.AndroidInterface.setNativeTheme(theme);
    }
}