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

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller responsible for handling HTTP requests for various web application pages.
 * This controller manages the routing and rendering of different views in the application,
 * including the main functional pages, legal information pages, and error handling.
 * <p>
 * It also manages session attributes related to harmonica configuration and audio
 * processing settings, ensuring a consistent user experience across page navigations.
 */
@Controller
public class WebappController {

    @Autowired
    private Environment environment;

    /**
     * Handles requests for the index page, which serves as the main landing page
     * of the web application. This page provides an overview of the application
     * and its features.
     *
     * @return a ModelAndView object configured to render the index page
     */
    @GetMapping("/index.html")
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }

    /**
     * Handles GET requests to the "/doc.html" endpoint and returns a view named "doc".
     *
     * @return a ModelAndView object with the view name set to "doc"
     */
    @GetMapping("/doc.html")
    public ModelAndView doc() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("doc");

        return modelAndView;
    }

    /**
     * Handles requests for the user guide page via the legacy "/user-guide" endpoint.
     * This method provides backward compatibility for links to the old URL pattern
     * by redirecting to the documentation page.
     *
     * @return a ModelAndView object configured to render the documentation page
     */
    @GetMapping("/user-guide")
    public ModelAndView docOld() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("doc");

        return modelAndView;
    }

    /**
     * Handles requests for the impressum (imprint) page, which contains legal information
     * about the website operator, contact details, and other information required by
     * German law for website operators.
     *
     * @return a ModelAndView object configured to render the impressum page
     */
    @GetMapping("/impressum.html")
    public ModelAndView impressum() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("impressum");

        return modelAndView;
    }

    /**
     * Handles requests for the datenschutz (privacy policy) page, which outlines how
     * user data is collected, processed, and stored by the application. This page
     * provides information required by data protection regulations such as the GDPR.
     *
     * @return a ModelAndView object configured to render the privacy policy page
     */
    @GetMapping("/datenschutz.html")
    public ModelAndView datenschutz() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("datenschutz");

        return modelAndView;
    }

    /**
     * Handles requests for the error page, which is displayed when an exception occurs
     * or when a user attempts to access a non-existent resource. This page provides
     * user-friendly error information and possible troubleshooting steps.
     *
     * @return a ModelAndView object configured to render the error page
     */
    @GetMapping("/error")
    public ModelAndView error() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");

        return modelAndView;
    }

    /**
     * Handles requests for the download page by redirecting users to the GitHub releases page.
     * <p>
     * Instead of hosting download files directly on the web server, this method redirects
     * users to the official GitHub repository's releases page where they can download the
     * latest version of the application. This approach ensures users always get the most
     * up-to-date version and reduces the maintenance burden of managing download files
     * on the web server.
     *
     * @return a string indicating a redirect to the GitHub releases URL
     */
    @GetMapping("/download.html")
    public String download() {
        return "redirect:https://github.com/egdels/bluesharpbendingapp/releases";
    }

    /**
     * Handles requests for the tuner page, which provides functionality for tuning
     * musical instruments. This page allows users to detect the pitch of notes played
     * into their microphone and displays visual feedback to help with tuning.
     *
     * @param session the HTTP session used to retrieve or save settings
     * @return a ModelAndView object configured to render the tuner page
     */
    @GetMapping("/tuner.html")
    public ModelAndView getTunerPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        // Add necessary objects for the tuner page
        Object supportedConfidences = session.getAttribute("supportedConfidences");
        if (supportedConfidences == null) {
            supportedConfidences = new String[]{"0.95", "0.9", "0.85", "0.8", "0.75", "0.7", "0.65", "0.6", "0.55", "0.5", "0.45", "0.4", "0.35", "0.3", "0.25", "0.2", "0.15", "0.1", "0.05"};
        }
        modelAndView.addObject("supportedConfidences", supportedConfidences);

        Object selectedConfidence = session.getAttribute("selectedConfidence");
        if (selectedConfidence == null) {
            selectedConfidence = "0.7";
        }
        modelAndView.addObject("selectedConfidence", selectedConfidence);

        Object supportedAlgorithms = session.getAttribute("supportedAlgorithms");
        if (supportedAlgorithms == null) {
            supportedAlgorithms = new String[]{"YIN", "MPM", "HYBRID"};
        }
        modelAndView.addObject("supportedAlgorithms", supportedAlgorithms);

        Object selectedAlgorithm = session.getAttribute("selectedAlgorithm");
        if (selectedAlgorithm == null) {
            selectedAlgorithm = "YIN";
        }
        modelAndView.addObject("selectedAlgorithm", selectedAlgorithm);

        Object supportedConcertPitches = session.getAttribute("supportedConcertPitches");
        if (supportedConcertPitches == null) {
            supportedConcertPitches = NoteLookup.getSupportedConcertPitches();
        }
        modelAndView.addObject("supportedConcertPitches", supportedConcertPitches);

        Object selectedConcertPitch = session.getAttribute("selectedConcertPitch");
        if (selectedConcertPitch == null) {
            selectedConcertPitch = "442";
        }
        modelAndView.addObject("selectedConcertPitch", selectedConcertPitch);

        modelAndView.setViewName("tuner");
        return modelAndView;
    }

    /**
     * Handles requests for the version.txt file, which returns the current version of the application
     * as plain text. This endpoint is used by clients to check for updates.
     *
     * @return the application version as plain text
     */
    @GetMapping(value = "/download/version.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getVersion() {
        return environment.getProperty("spring.application.version");
    }

    /**
     * Handles the request to display the audio stream page. This method initializes
     * the harmonica object in the session if it is not already present and adds necessary
     * data to the ModelAndView for rendering the audio stream page.
     *
     * @param session the HTTP session used to retrieve or save the harmonica instance
     * @return a ModelAndView object containing the view name and the required data for
     * rendering the audio stream page
     */
    @GetMapping("/letsbend.html")
    public ModelAndView getAudioStreamPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        HarmonicaWeb harmonicaWeb = (HarmonicaWeb) session.getAttribute("harmonica");

        if (harmonicaWeb == null) {
            harmonicaWeb = new HarmonicaWeb(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);
        }

        modelAndView.addObject("harmonica", harmonicaWeb);
        modelAndView.addObject("supportedTunes", AbstractHarmonica.getSupportedTunes());
        modelAndView.addObject("supportedKeys", AbstractHarmonica.getSupporterKeys());

        Object supportedConcertPitches = session.getAttribute("supportedConcertPitches");
        if (supportedConcertPitches == null) {
            supportedConcertPitches = NoteLookup.getSupportedConcertPitches();
        }
        modelAndView.addObject("supportedConcertPitches", supportedConcertPitches);

        Object selectedConcertPitch = session.getAttribute("selectedConcertPitch");
        if (selectedConcertPitch == null) {
            selectedConcertPitch = "442";
        }
        modelAndView.addObject("selectedConcertPitch", selectedConcertPitch);

        Object supportedConfidences = session.getAttribute("supportedConfidences");
        if (supportedConfidences == null) {
            supportedConfidences = new String[]{"0.95", "0.9", "0.85", "0.8", "0.75", "0.7", "0.65", "0.6", "0.55", "0.5", "0.45", "0.4", "0.35", "0.3", "0.25", "0.2", "0.15", "0.1", "0.05"};
        }
        modelAndView.addObject("supportedConfidences", supportedConfidences);

        Object selectedConfidence = session.getAttribute("selectedConfidence");
        if (selectedConfidence == null) {
            selectedConfidence = "0.7";
        }
        modelAndView.addObject("selectedConfidence", selectedConfidence);

        Object supportedAlgorithms = session.getAttribute("supportedAlgorithms");
        if (supportedAlgorithms == null) {
            supportedAlgorithms = new String[]{"YIN", "MPM", "HYBRID"};
        }
        modelAndView.addObject("supportedAlgorithms", supportedAlgorithms);

        Object selectedAlgorithm = session.getAttribute("selectedAlgorithm");
        if (selectedAlgorithm == null) {
            selectedAlgorithm = "YIN";
        }
        modelAndView.addObject("selectedAlgorithm", selectedAlgorithm);

        Object expertMode = session.getAttribute("expertMode");
        if (expertMode == null) {
            expertMode = false;
        }
        modelAndView.addObject("expertMode", expertMode);


        modelAndView.setViewName("letsbend");
        return modelAndView;
    }
}
