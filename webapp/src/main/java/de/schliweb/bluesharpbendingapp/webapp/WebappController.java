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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller responsible for handling requests for various web application pages.
 */
@Controller
public class WebappController {

    /**
     * Index model and view.
     *
     * @return the model and view
     */
    @GetMapping("/index.html")
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }

    /**
     * Impressum model and view.
     *
     * @return the model and view
     */
    @GetMapping("/impressum.html")
    public ModelAndView impressum() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("impressum");

        return modelAndView;
    }

    /**
     * Datenschutz model and view.
     *
     * @return the model and view
     */
    @GetMapping("/datenschutz.html")
    public ModelAndView datenschutz() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("datenschutz");

        return modelAndView;
    }

    /**
     * Error model and view.
     *
     * @return the model and view
     */
    @GetMapping("/error")
    public ModelAndView error() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");

        return modelAndView;
    }

    /**
     * Redirect to the GitHub releases page.
     *
     * @return the redirect view
     */
    @GetMapping("/download.html")
    public String download() {
        return "redirect:https://github.com/egdels/bluesharpbendingapp/releases";
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
        if(supportedConcertPitches==null) {
            supportedConcertPitches = NoteLookup.getSupportedConcertPitches();
        }
        modelAndView.addObject("supportedConcertPitches", supportedConcertPitches);

        Object selectedConcertPitch = session.getAttribute("selectedConcertPitch");
        if(selectedConcertPitch==null) {
            selectedConcertPitch = "442";
        }
        modelAndView.addObject("selectedConcertPitch", selectedConcertPitch);

        Object supportedConfidences = session.getAttribute("supportedConfidences");
        if(supportedConfidences==null) {
            supportedConfidences = new String[]{"0.95", "0.9", "0.85", "0.8", "0.75", "0.7", "0.65", "0.6", "0.55", "0.5", "0.45", "0.4", "0.35", "0.3", "0.25", "0.2", "0.15", "0.1", "0.05"};
        }
        modelAndView.addObject("supportedConfidences", supportedConfidences);

        Object selectedConfidence = session.getAttribute("selectedConfidence");
        if(selectedConfidence==null) {
            selectedConfidence = "0.7";
        }
        modelAndView.addObject("selectedConfidence", selectedConfidence);

        Object supportedAlgorithms = session.getAttribute("supportedAlgorithms");
        if(supportedAlgorithms==null) {
            supportedAlgorithms = new String[]{"YIN", "MPM"};
        }
        modelAndView.addObject("supportedAlgorithms", supportedAlgorithms);

        Object selectedAlgorithm = session.getAttribute("selectedAlgorithm");
        if(selectedAlgorithm==null) {
            selectedAlgorithm= "YIN";
        }
        modelAndView.addObject("selectedAlgorithm", selectedAlgorithm);

        Object expertMode = session.getAttribute("expertMode");
        if(expertMode==null) {
            expertMode = false;
        }
        modelAndView.addObject("expertMode", expertMode);


        modelAndView.setViewName("letsbend");
        return modelAndView;
    }
}
