package de.schliweb.bluesharpbendingapp.webapp;
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
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller responsible for handling selection-related operations and providing
 * functionalities for sending and retrieving harmonica preferences.
 * It exposes endpoints for processing user selections and managing session data.
 */
@RestController
@RequestMapping("/selection")
public class SelectionController {


    /**
     * Processes the user's selection for a harmonica by extracting key and tune
     * information and creating a Harmonica object. The created object is stored in
     * the user's session and returned as the response.
     *
     * @param selection a map containing the user's selected key and tune, with keys
     *                  "supportedKey" and "supportedTune" respectively.
     * @param session the HTTP session to store the created Harmonica object.
     * @return a ResponseEntity containing the created Harmonica object.
     */
    @PostMapping("/send")
    public ResponseEntity<HarmonicaWeb> ajaxProcessSelection (@RequestBody Map<String, String> selection, HttpSession session) {
        // Retrieve the selected key and tune from the request body
        String selectedKey = selection.get("selectedKey");
        String selectedTune = selection.get("selectedTune");
        // Create a new Harmonica instance based on the selected key and tune
        HarmonicaWeb harmonicaWeb = new HarmonicaWeb(selectedKey, selectedTune);

        // Save the Harmonica object in the user's session
        session.setAttribute("harmonica", harmonicaWeb);


        String selectedConcertPitch = selection.get("selectedConcertPitch");
        session.setAttribute("selectedConcertPitch", selectedConcertPitch);

        String selectedConfidence = selection.get("selectedConfidence");
        session.setAttribute("selectedConfidence", selectedConfidence);

        String selectedAlgorithm = selection.get("selectedAlgorithm");
        session.setAttribute("selectedAlgorithm", selectedAlgorithm);

        String expertMode = selection.get("expertMode");
        session.setAttribute("expertMode", expertMode);

        // Return the Harmonica object as a success response
        return ResponseEntity.ok(harmonicaWeb);
    }
}

