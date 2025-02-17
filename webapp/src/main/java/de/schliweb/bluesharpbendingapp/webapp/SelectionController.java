package de.schliweb.bluesharpbendingapp.webapp;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Harmonica> ajaxVerarbeiteAuswahl(@RequestBody Map<String, String> selection, HttpSession session) {
        // Retrieve the selected key and tune from the request body
        String selectedKey = selection.get("supportedKey");
        String selectedTune = selection.get("supportedTune");

        // Processing the received values (e.g., logging, applying logic, or saving them)
        System.out.println("Key: " + selectedKey);
        System.out.println("Tune: " + selectedTune);

        // Create a new Harmonica instance based on the selected key and tune
        Harmonica harmonica = AbstractHarmonica.create(selectedKey, selectedTune);

        // Save the Harmonica object in the user's session
        session.setAttribute("harmonica", harmonica);

        // Return the Harmonica object as a success response
        return ResponseEntity.ok(harmonica);
    }


    /**
     * Retrieves the Harmonica object from the user's session.
     * If the object is not found, returns a 404 Not Found response.
     * If the object is found, returns it with a 200 OK response.
     *
     * @param session the HTTP session from which the Harmonica object is retrieved.
     * @return a ResponseEntity containing the Harmonica object if present in the session,
     *         otherwise a 404 Not Found response.
     */
    @GetMapping("/session/harmonica")
    public ResponseEntity<Harmonica> getHarmonicaFromSession(HttpSession session) {
        // Retrieve the Harmonica object from the session
        Harmonica harmonica = (Harmonica) session.getAttribute("harmonica");

        // If no Harmonica object is found in the session, return a 404 Not Found response
        if (harmonica == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // If the Harmonica object is found, return it with a 200 OK response
        return ResponseEntity.ok(harmonica);
    }


}

