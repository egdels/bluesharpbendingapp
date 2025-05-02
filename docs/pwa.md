
# Adding Progressive Web App (PWA) Capabilities

To add Progressive Web App capabilities to the Bluesharp Bending App, we need to implement several key features that will allow the web application to work offline, be installable on devices, and provide a more app-like experience. Here's a comprehensive implementation plan:

## 1. Complete the Web App Manifest

The project already has a `site.webmanifest` file at `webapp/src/main/resources/static/site.webmanifest`, but it's missing some key information. Update it with the following content:

```json
{
  "name": "Blues Harp Bending App",
  "short_name": "Harp Bender",
  "description": "Learn and practice blues harmonica bending techniques",
  "start_url": "/index.html",
  "icons": [
    {
      "src": "/images/android-chrome-192x192.png",
      "sizes": "192x192",
      "type": "image/png"
    },
    {
      "src": "/images/android-chrome-512x512.png",
      "sizes": "512x512",
      "type": "image/png"
    }
  ],
  "theme_color": "#0066cc",
  "background_color": "#ffffff",
  "display": "standalone",
  "orientation": "portrait",
  "scope": "/",
  "lang": "en-US"
}
```

## 2. Create a Service Worker

Create a new file at `webapp/src/main/resources/static/scripts/service-worker.js` with the following content:

```javascript
const CACHE_NAME = 'blues-harp-bending-app-v1';
const ASSETS_TO_CACHE = [
  '/',
  '/index.html',
  '/styles/style.css',
  '/scripts/script.js',
  '/scripts/NoteLookup.js',
  '/scripts/NoteUtils.js',
  '/scripts/PitchProcessor.js',
  '/scripts/MPMPitchDetector.js',
  '/scripts/YINPitchDetector.js',
  '/images/logo.png',
  '/images/android-chrome-192x192.png',
  '/images/android-chrome-512x512.png',
  '/images/apple-touch-icon.png',
  '/images/favicon-16x16.png',
  '/images/favicon-32x32.png',
  '/images/favicon.ico',
  '/site.webmanifest'
];

// Install event - cache assets
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        console.log('Caching app assets');
        return cache.addAll(ASSETS_TO_CACHE);
      })
  );
});

// Activate event - clean up old caches
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.filter(cacheName => {
          return cacheName !== CACHE_NAME;
        }).map(cacheName => {
          return caches.delete(cacheName);
        })
      );
    })
  );
});

// Fetch event - serve from cache or network
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        // Return cached response if found
        if (response) {
          return response;
        }
        
        // Clone the request
        const fetchRequest = event.request.clone();
        
        // Make network request and cache the response
        return fetch(fetchRequest).then(response => {
          // Check if valid response
          if (!response || response.status !== 200 || response.type !== 'basic') {
            return response;
          }
          
          // Clone the response
          const responseToCache = response.clone();
          
          // Cache the fetched response
          caches.open(CACHE_NAME)
            .then(cache => {
              cache.put(event.request, responseToCache);
            });
            
          return response;
        });
      })
  );
});
```

## 3. Register the Service Worker

Add the following code to the beginning of `webapp/src/main/resources/static/scripts/script.js`:

```javascript
// Register service worker for PWA functionality
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/scripts/service-worker.js')
      .then(registration => {
        console.log('Service Worker registered with scope:', registration.scope);
      })
      .catch(error => {
        console.error('Service Worker registration failed:', error);
      });
  });
}
```

## 4. Update the HTML Head

Modify the `head` fragment in `webapp/src/main/resources/templates/fragments/general.html` to add the theme-color meta tag and ensure proper PWA support:

```html
<head th:fragment="head">
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta content="Let's Bend - Blues Harp Bending App for all devices" name="description"/>
    <meta content="Blues Harp Bending App, Blues Harp Bending App for Android, Blues Harp Bending App for Linux, Blues Harp Bending App for Windows, Blues Harp Bending App for macOS"
          name="keywords"/>
    <meta name="theme-color" content="#0066cc">
    <title data-th-text="#{index.title}"></title>
    <link href="/images/apple-touch-icon.png" rel="apple-touch-icon" sizes="180x180">
    <link href="/images/favicon-32x32.png" rel="icon" sizes="32x32" type="image/png">
    <link href="/images/favicon-16x16.png" rel="icon" sizes="16x16" type="image/png">
    <link href="/site.webmanifest" rel="manifest">
    <link th:href="${'/styles/style.css?v=' + @environment.getProperty('spring.application.version')}" rel="stylesheet">
    <script th:src="${'/scripts/script.js?v=' + @environment.getProperty('spring.application.version')}" ></script>
    <!-- Add iOS specific PWA meta tags -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
    <!-- Rest of the head content remains the same -->
    <!-- ... -->
</head>
```

## 5. Add an Offline Page

Create a new file at `webapp/src/main/resources/static/offline.html`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta name="theme-color" content="#0066cc">
    <title>Offline - Blues Harp Bending App</title>
    <link href="/images/apple-touch-icon.png" rel="apple-touch-icon" sizes="180x180">
    <link href="/images/favicon-32x32.png" rel="icon" sizes="32x32" type="image/png">
    <link href="/images/favicon-16x16.png" rel="icon" sizes="16x16" type="image/png">
    <link href="/site.webmanifest" rel="manifest">
    <link href="/styles/style.css" rel="stylesheet">
    <style>
        .offline-message {
            text-align: center;
            padding: 2rem;
            margin: 2rem auto;
            max-width: 600px;
        }
        .offline-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <nav class="navbar">
                <div class="logo">
                    <img alt="Logo" src="/images/logo.png">
                </div>
            </nav>
        </div>
    </header>
    <main>
        <div class="container">
            <div class="offline-message">
                <div class="offline-icon">📶</div>
                <h1>You're Offline</h1>
                <p>It looks like you've lost your internet connection. Some features may be unavailable until you're back online.</p>
                <p>You can still access previously loaded content.</p>
            </div>
        </div>
    </main>
    <footer>
        <div class="container">
            <p>© Blues Harp Bending App</p>
        </div>
    </footer>
</body>
</html>
```

## 6. Update the Service Worker to Handle Offline Scenarios

Add the following to the service worker's fetch event handler in `service-worker.js`:

```javascript
// Inside the fetch event handler, modify the catch block
return fetch(fetchRequest).then(response => {
  // ... existing code ...
}).catch(() => {
  // If the request is for an HTML page, return the offline page
  if (event.request.headers.get('accept').includes('text/html')) {
    return caches.match('/offline.html');
  }
});
```

## Testing the PWA Implementation

After implementing these changes, you can test the PWA capabilities using the following methods:

1. Use Chrome DevTools' Lighthouse to audit the PWA features
2. Test offline functionality by disabling network in DevTools
3. Verify that the app can be installed on devices by looking for the "Add to Home Screen" prompt
4. Check that the app works properly when launched from the home screen

These changes will transform your web application into a Progressive Web App with offline capabilities, installability, and a more native app-like experience for users.