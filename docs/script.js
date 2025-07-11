/*
  Smart WebView v7
  https://github.com/mgks/Android-SmartWebView
*/

// This variable will store the theme detected by the native app
let nativeThemePreference = 'light';

document.addEventListener('DOMContentLoaded', function() {

    const imageInput = document.getElementById('add-img');
    const videoInput = document.getElementById('add-vid');
    const gallery = document.querySelector('.gallery');
    const MAX_WIDTH = 240;

    // Handle image previews
    imageInput.addEventListener('change', function() {
        gallery.innerHTML = ''; // clear previous previews
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
                    gallery.appendChild(canvas);
                }
            };
            reader.readAsDataURL(file);
        }
    });

    // Handle video file name display
    videoInput.addEventListener('change', function() {
        const fileNameDisplay = document.createElement('p');
        fileNameDisplay.className = 'file-name-display';
        if (this.files && this.files.length > 0) {
            fileNameDisplay.textContent = 'Selected: ' + this.files[0].name;
        } else {
            fileNameDisplay.textContent = '';
        }

        // Remove old file name if it exists
        const oldDisplay = this.parentElement.querySelector('.file-name-display');
        if(oldDisplay) {
            oldDisplay.remove();
        }
        this.parentElement.appendChild(fileNameDisplay);
    });

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

function get_cookies(name) {
	const value = `; ${document.cookie}`;
	const parts = value.split(`; ${name}=`);
	if (parts.length === 2) return parts.pop().split(';').shift();
}

function get_location() {
	console.log(window.location);

	let latitude = null;
	let longitude = null;

	// first, try getting location from cookies (online case)
	const latCookie = get_cookies('lat');
	const longCookie = get_cookies('long');

	if (latCookie && longCookie) {  // check if cookies exist and are defined
		latitude = parseFloat(latCookie);
		longitude = parseFloat(longCookie);
	} else {
		// second, if cookies not available (offline), try URL parameters
		const urlParams = new URLSearchParams(window.location.search);
		const locParam = urlParams.get('loc');
		if (locParam) {
			const loc = locParam.split(',');
			if (loc.length === 2) {
				latitude = parseFloat(loc[0]);
				longitude = parseFloat(loc[1]);
			} else {
				console.error("SWVJS Invalid 'loc' parameter format");
			}
		}
	}

	if (latitude !== null && longitude !== null) {
		const locationDiv = document.querySelector('.fetch-loc-area');
        if(locationDiv) {
            locationDiv.innerHTML = "<div class=\"fetch-loc\"><b>Latitude:</b> " + latitude.toFixed(6) + "<br><b>Longitude:</b> " + longitude.toFixed(6) + "</div>";
        }
	}
}

function print_page(){
	window.print();
}

function applyInitialTheme(nativeTheme) {
    if (nativeTheme) {
        nativeThemePreference = nativeTheme;
    }
}

function setTheme(theme, isSystem = false) {
    const body = document.body;
    // Get the themeSwitcher inside the function to ensure it exists when called.
    const themeSwitcher = document.getElementById('theme-switcher');
    let activeTheme = theme;

    if (theme === 'system') {
        localStorage.removeItem('swv-theme');
        activeTheme = nativeThemePreference;
        isSystem = true;
    }

    if (activeTheme === 'dark') {
        body.classList.add('dark-mode');
    } else {
        body.classList.remove('dark-mode');
    }

    if (themeSwitcher) {
        themeSwitcher.querySelectorAll('button').forEach(btn => {
            btn.classList.remove('active');
        });
        const buttonToActivate = isSystem ? 'system' : activeTheme;
        const activeButton = themeSwitcher.querySelector(`[data-theme="${buttonToActivate}"]`);
        if (activeButton) {
            activeButton.classList.add('active');
        }
    }

    if (!isSystem) {
        localStorage.setItem('swv-theme', theme);
    }
}