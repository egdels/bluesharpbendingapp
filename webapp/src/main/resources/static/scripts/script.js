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
    let navbarToggler = document.querySelector(".navbar-toggler");
    let menu = document.querySelector(".menu ul");
    navbarToggler.addEventListener("click", function () {
        if (!menu.style.display || menu.style.display === "none") {
            menu.style.display = "block";
        } else {
            menu.style.display = "none";
        }
    });
});


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

    const key = document.querySelector('#supportedKeys').value;
    const tune = document.querySelector('#supportedTunes').value;
    const concertPitch = document.querySelector('#supportedConcertPitches').value;
    const confidence = document.querySelector('#supportedConfidences').value;

    // Only send data if all dropdowns have a valid selection
    if (key && tune && concertPitch && confidence) {
        fetch('/selection/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Send data as JSON
            },
            body: JSON.stringify({
                selectedKey: key,
                selectedTune: tune,
                selectedConcertPitch: concertPitch,
                selectedConfidence: confidence
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
