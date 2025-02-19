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

    // Only send data if both dropdowns have a valid selection
    if (key && tune) {
        fetch('/selection/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // Send data as JSON
            },
            body: JSON.stringify({supportedKey: key, supportedTune: tune}) // Selection data
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
 * Handles changes to dynamically update the visual representation of elements within a grid based on data input.
 * It adjusts the position and color of a line element related to a specific grid cell, based on the input's confidence and cents value.
 *
 * @param {Object} data - The input data containing information for updating elements.
 * @param {string} data.noteName - The name of the note to match.
 * @param {number} data.cents - The cents value used to calculate position and color.
 * @param {number} data.confidence - A confidence level used to determine if updates should be applied.
 * @param {number} [precision=0.7] - The minimum confidence threshold required to apply updates.
 *
 * @return {void} This function does not return a value.
 */
function handleChange(data, precision = 0.7) {
    // Select all span elements within elements that have the class "grid-cell"
    const spans = document.querySelectorAll('.grid-cell span');

    // Iterate through each span to find the one with text matching data.noteName
    spans.forEach(span => {

        const lineElement = span.parentElement.parentElement.querySelector('.line');
        lineElement.style.visibility = 'hidden';

        // Check if the text content of the span equals data.noteName
        if (data.confidence >= precision && span.textContent.trim() === data.noteName) {
            console.log("Matching cell found:", span.parentElement.parentElement);

            // Retrieve the value from data.cents
            const centsValue = data.cents; // Value from your object

            // Define the limits for calculation
            const minCents = -50; // Top (upper boundary)
            const maxCents = 50;  // Bottom (lower boundary)

            // Normalize the value from -50 to 50 into a range between 0 and 1
            const normalized = (centsValue - minCents) / (maxCents - minCents);

            // Interpolate color from green (center) to red (top and bottom)
            const red = Math.abs(2 * normalized - 1) * 255; // Red intensity increases away from the center
            const green = (1 - Math.abs(2 * normalized - 1)) * 255; // Green intensity decreases away from the center

            // Create the dynamic color
            const color = `rgb(${Math.round(red)}, ${Math.round(green)}, 0)`;

            // Calculate the dynamic top position in percentage
            const minPercent = 0; // 0% = Top edge
            const maxPercent = 100; // 100% = Bottom edge
            const normalizedTop = normalized * (maxPercent - minPercent) + minPercent;

            // Set the dynamic top position in percentage
            lineElement.style.top = `${normalizedTop}%`; // Value in percent
            lineElement.style.visibility = 'visible';

            // Set the dynamic color of the line
            lineElement.style.backgroundColor = color;

            console.log(`Cents: ${centsValue}, Normalized: ${normalized}, Color: ${color}`);
        }

    });
}