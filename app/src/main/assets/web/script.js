/*
  Smart WebView v8 - Offline Script
  https://github.com/mgks/Android-SmartWebView
*/

// This variable will store the theme detected by the native app
let nativeThemePreference = 'light';

// Track the most recently uploaded file so we can share it via SharePlugin.
let lastUploadedFileUri = null;

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
                lastUploadedFileUri = null;
                if (!this.files) return;

                for (const file of Array.from(this.files)) {
                    // Remember the URI of the first file for the Share button.
                    if (lastUploadedFileUri === null) {
                        try { lastUploadedFileUri = URL.createObjectURL(file); } catch (e) {}
                    }
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
    window.updateLocationDisplay = function(lat, long, source) {
        const locElement = document.querySelector('.fetch-loc');
        if (locElement) {
            if (lat && long) {
                const note = source === 'cache' ? ' <small>(from cache — fixes #387)</small>' : '';
                locElement.innerHTML = "<b>Latitude:</b> " + lat.toFixed(6) +
                        "<br><b>Longitude:</b> " + long.toFixed(6) + note;
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

    // --- NetworkInfoPlugin wiring (live network status) ---
    if (window.NetworkInfo) {
        window.NetworkInfo.getStatus(renderNetworkStatus);
        window.NetworkInfo.onChange(renderNetworkStatus);
    }

    // --- GeolocationCachePlugin wiring: warm the cache every page load ---
    if (window.GeolocationCache) {
        window.GeolocationCache.getLastKnown(function(lat, lng, ageMs) {
            if (lat !== null && lng !== null && ageMs >= 0) {
                console.log('[GeolocationCache] warm hit:', lat, lng, ageMs + 'ms ago');
            }
        });
    }
});

function renderNetworkStatus(state) {
    const el = document.getElementById('network-status');
    const pill = document.getElementById('net-status-pill');
    if (!el) return;
    const online = state && state.online;
    el.innerHTML = online
        ? '<b style="color:#0a8a3a">Online</b> via ' + (state.type || 'unknown')
        : '<b style="color:#a33">Offline</b>';
    if (pill) {
        pill.style.background = online ? 'rgba(10,138,58,0.15)' : 'rgba(170,51,51,0.15)';
    }
}

function fetchLocation() {
    const locElement = document.querySelector('.fetch-loc') || document.querySelector('.fetch-loc-area');
    if (locElement) {
        locElement.innerHTML = "<div class='fetch-loc'>Fetching location from device...</div>";
    }
    // Call the new, non-conflicting object name
    if (window.SWVLocation) {
        window.SWVLocation.getCurrentPosition(function(lat, lng, error) {
            const displayDiv = document.querySelector('.fetch-loc') || document.querySelector('.fetch-loc-area');
            if (error) {
                // Primary path failed. Try the GeolocationCachePlugin fallback (#387).
                if (window.GeolocationCache) {
                    window.GeolocationCache.getLastKnown(function(cLat, cLng, ageMs) {
                        if (cLat !== null && cLng !== null && ageMs >= 0) {
                            window.updateLocationDisplay(cLat, cLng, 'cache');
                            return;
                        }
                        displayDiv.innerHTML = "<div class='fetch-loc'><b>Error:</b> " + error + "</div>";
                    });
                } else {
                    displayDiv.innerHTML = "<div class='fetch-loc'><b>Error:</b> " + error + "</div>";
                }
                return;
            }
            if (lat && lng) {
                // Cache the fresh fix for next time.
                if (window.GeolocationCache) window.GeolocationCache.cache(lat, lng);
                window.updateLocationDisplay(lat, lng, 'gps');
            }
        });
    } else {
        alert("Location feature is not available.");
    }
}

function doClipboardCopy() {
    const v = document.getElementById('clip-input').value;
    if (window.Clipboard) {
        window.Clipboard.copy(v);
        alert('Copied to clipboard.');
    } else {
        alert('ClipboardPlugin not enabled.');
    }
}

function doClipboardPaste() {
    if (window.Clipboard) {
        window.Clipboard.paste(function(text) {
            document.getElementById('clip-input').value = text;
        });
    } else {
        alert('ClipboardPlugin not enabled.');
    }
}

function shareFirstImage() {
    if (!window.Share) {
        alert('SharePlugin not enabled.');
        return;
    }
    if (!lastUploadedFileUri) {
        alert('Pick an image first.');
        return;
    }
    window.Share.file(lastUploadedFileUri, 'image/*', 'Shared from Smart WebView', 'Share image via');
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