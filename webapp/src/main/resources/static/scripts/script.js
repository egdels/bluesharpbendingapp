/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

document.addEventListener("DOMContentLoaded", function () {
    // Apply empty-cell class for browsers without :has() support
    applyEmptyCellClass();

    // Mobile menu functionality
    const navbarToggler = document.querySelector(".navbar-toggler");
    const menu = document.querySelector(".menu");
    const menuUl = document.querySelector(".menu ul");

    // Toggle menu when hamburger icon is clicked
    navbarToggler.addEventListener("click", function (e) {
        e.stopPropagation();
        menu.classList.toggle("open");

        // Toggle aria-expanded for accessibility
        const isExpanded = menu.classList.contains("open");
        navbarToggler.setAttribute("aria-expanded", isExpanded);

        // Add animation to hamburger icon
        if (isExpanded) {
            navbarToggler.classList.add("active");
        } else {
            navbarToggler.classList.remove("active");
        }
    });

    // Close menu when clicking outside
    document.addEventListener("click", function (e) {
        if (menu.classList.contains("open") && !menu.contains(e.target)) {
            menu.classList.remove("open");
            navbarToggler.classList.remove("active");
            navbarToggler.setAttribute("aria-expanded", "false");
        }
    });

    // Close menu when pressing Escape key
    document.addEventListener("keydown", function (e) {
        if (e.key === "Escape" && menu.classList.contains("open")) {
            menu.classList.remove("open");
            navbarToggler.classList.remove("active");
            navbarToggler.setAttribute("aria-expanded", "false");
        }
    });

    // Handle orientation changes
    window.addEventListener("orientationchange", function () {
        // Close menu on orientation change
        if (menu.classList.contains("open")) {
            menu.classList.remove("open");
            navbarToggler.classList.remove("active");
            navbarToggler.setAttribute("aria-expanded", "false");
        }

        // Adjust UI after orientation change
        setTimeout(function() {
            adjustUIForOrientation();
        }, 300); // Small delay to allow orientation to complete
    });

    // Add touch events for mobile
    if ('ontouchstart' in window) {
        // Add touch-specific classes
        document.body.classList.add("touch-device");

        // Make menu items work better with touch
        const menuItems = document.querySelectorAll(".menu ul li a");
        menuItems.forEach(function(item) {
            item.addEventListener("touchstart", function() {
                this.classList.add("touch-active");
            });

            item.addEventListener("touchend", function() {
                this.classList.remove("touch-active");
            });
        });
    }

    // Initial UI adjustment
    adjustUIForOrientation();
});

/**
 * Adjusts UI elements based on current orientation and screen size
 */
function adjustUIForOrientation() {
    const isLandscape = window.innerWidth > window.innerHeight;
    const isMobile = window.innerWidth <= 480;
    const isTablet = window.innerWidth <= 768 && window.innerWidth > 480;

    // Get the grid container
    const gridContainer = document.querySelector(".grid-container");

    if (gridContainer) {
        // Clear any previously set inline styles to let CSS handle the base styling
        gridContainer.style.height = "";

        // Add classes based on device width and orientation instead of setting inline styles
        gridContainer.classList.remove("landscape", "portrait", "mobile", "tablet");

        if (isLandscape) {
            gridContainer.classList.add("landscape");
        } else {
            gridContainer.classList.add("portrait");
        }

        if (isMobile) {
            gridContainer.classList.add("mobile");
        } else if (isTablet) {
            gridContainer.classList.add("tablet");
        }

        // Adjust container width for very narrow devices
        if (window.innerWidth < 350) {
            gridContainer.style.width = "100%";
            gridContainer.style.padding = "2px";
            gridContainer.style.gap = "2px";
        }
    }
}


/**
 * Handles the change in selection for dropdown menus and updates the server with the selected key and tune.
 * The method retrieves values from the dropdowns with IDs `#supportedKeys` and `#supportedTunes`.
 * If both dropdowns have valid selections, it sends a POST request to the server with the selected data
 * in JSON format. The server response, if successful, is expected to contain the updated Harmonica object,
 * which is then used to update the interface.
 *
 * @return {void} This method does not return any value but performs UI and server update actions.
 */
function handleSelectionChange() {
    // Check if we're on the letsbend.html page by looking for required elements
    const keyElement = document.querySelector('#supportedKeys');
    if (!keyElement) {
        // We're likely on tuner.html or another page that doesn't have these elements
        // Let the page-specific handleSelectionChange function handle it
        return;
    }

    const key = keyElement.value;
    const tune = document.querySelector('#supportedTunes').value;
    const concertPitch = document.querySelector('#supportedConcertPitches').value;
    const confidence = document.querySelector('#supportedConfidences').value;
    const algorithm = document.querySelector('#supportedAlgorithms').value;
    const expertMode = !!document.querySelector('#expertMode').checked;

    // Only send data if all dropdowns have a valid selection
    if (key && tune && concertPitch && confidence && algorithm) {
        fetch('/selection/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Send data as JSON
            },
            body: JSON.stringify({
                selectedKey: key,
                selectedTune: tune,
                selectedConcertPitch: concertPitch,
                selectedConfidence: confidence,
                selectedAlgorithm: algorithm,
                expertMode: expertMode
            }) // Selection data
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error updating the selection');
                }
                return response.json(); // The updated Harmonica object
            })
            .then(harmonica => {
                console.log('Updated Harmonica object:', harmonica);

                // Use the updated Harmonica object or adjust the UI accordingly
                updateInterfaceWithHarmonica(harmonica); // Example function
            })
            .catch(error => console.error('Error:', error)); // Error handling
    }
}

/**
 * Updates the user interface with the specified harmonica object.
 * Currently, the method reloads the page to reflect changes.
 * Future implementation may dynamically update specific UI elements with harmonica details.
 *
 * @param {Object} harmonica - The harmonica object containing details for the update.
 * @param {string} harmonica.keyName - The key name of the harmonica.
 * @param {string} harmonica.tuneName - The tuning name of the harmonica.
 * @return {void} This function does not return a value.
 */
function updateInterfaceWithHarmonica(harmonica) {
    // Reload the page
    window.location.reload();

    /* TODO
    // Dynamically update the UI with the new Harmonica values
    document.querySelector('#harmonicaKey').textContent = harmonica.keyName;
    document.querySelector('#harmonicaTune').textContent = harmonica.tuneName;

    console.log('Interface updated with new Harmonica values.');
    */
}

/**
 * Applies the 'empty-cell' class to grid cells with empty spans.
 * This provides a fallback for browsers that don't support the :has() selector.
 */
function applyEmptyCellClass() {
    // Get all grid cells
    const gridCells = document.querySelectorAll('.grid-cell');

    // Check each cell for empty spans
    gridCells.forEach(function(cell) {
        const span = cell.querySelector('span');
        if (span && span.textContent.trim() === '') {
            cell.classList.add('empty-cell');
        }
    });

    // Apply cents-border-container class to elements with cents-border-text attribute
    applyCentsBorderClass();

    // Set up a MutationObserver to handle dynamically added cells
    if (window.MutationObserver) {
        const gridContainer = document.querySelector('.grid-container');
        if (gridContainer) {
            const observer = new MutationObserver(function(mutations) {
                mutations.forEach(function(mutation) {
                    if (mutation.type === 'childList') {
                        mutation.addedNodes.forEach(function(node) {
                            if (node.nodeType === 1) {
                                // Handle empty cells
                                if (node.classList.contains('grid-cell')) {
                                    const span = node.querySelector('span');
                                    if (span && span.textContent.trim() === '') {
                                        node.classList.add('empty-cell');
                                    }
                                }

                                // Handle cents-border-text elements
                                if (node.hasAttribute && node.hasAttribute('cents-border-text')) {
                                    node.classList.add('cents-border-container');
                                }

                                // Check children for cents-border-text attribute
                                const centsBorderElements = node.querySelectorAll 
                                    ? node.querySelectorAll('[cents-border-text]') 
                                    : [];

                                for (let i = 0; i < centsBorderElements.length; i++) {
                                    centsBorderElements[i].classList.add('cents-border-container');
                                }
                            }
                        });
                    }
                });
            });

            observer.observe(gridContainer, { childList: true, subtree: true });
        }
    }
}

/**
 * Applies the 'cents-border-container' class to elements with the 'cents-border-text' attribute.
 * This provides a fallback for browsers that don't support custom attribute selectors.
 */
function applyCentsBorderClass() {
    try {
        // Get all elements with cents-border-text attribute
        const centsBorderElements = document.querySelectorAll('[cents-border-text]');

        // Add cents-border-container class to each element
        centsBorderElements.forEach(function(element) {
            element.classList.add('cents-border-container');
        });
    } catch (e) {
        console.log('Browser may not support custom attribute selectors. Using alternative method.');

        // Alternative approach for older browsers
        const allElements = document.getElementsByTagName('*');
        for (let i = 0; i < allElements.length; i++) {
            if (allElements[i].getAttribute && allElements[i].getAttribute('cents-border-text') !== null) {
                allElements[i].classList.add('cents-border-container');
            }
        }
    }
}
