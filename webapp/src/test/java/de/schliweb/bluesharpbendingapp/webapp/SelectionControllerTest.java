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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the SelectionController.
 * <p>
 * This class contains unit tests for the SelectionController, focusing on the
 * functionality of the ajaxProcessSelection method. The tests verify that the
 * controller correctly processes user selections, stores them in the session,
 * and returns the appropriate response.
 */
@WebMvcTest(SelectionController.class)
class SelectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HttpSession httpSession;

    /**
     * Tests that the ajaxProcessSelection method correctly processes a valid input with all attributes.
     * <p>
     * This test verifies that when a complete set of selection attributes is provided,
     * the controller correctly:
     * <ul>
     *   <li>Returns a 200 OK status</li>
     *   <li>Returns a HarmonicaWeb object with the correct key and tune</li>
     *   <li>Stores all selection attributes in the session</li>
     * </ul>
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void ajaxProcessSelection_validInput_shouldReturnHarmonicaWebAndSetSessionAttributes() throws Exception {
        MockHttpSession session = new MockHttpSession();

        String inputJson = """
                {
                    "selectedKey": "C",
                    "selectedTune": "RICHTER",
                    "selectedConcertPitch": "440Hz",
                    "selectedConfidence": "High",
                    "selectedAlgorithm": "AlgorithmA",
                    "expertMode": "true"
                }
                """;

        mockMvc.perform(post("/selection/send").contentType(MediaType.APPLICATION_JSON).content(inputJson).session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.selectedKey").value("C")).andExpect(jsonPath("$.selectedTune").value("RICHTER"));

        HarmonicaWeb harmonicaWeb = (HarmonicaWeb) session.getAttribute("harmonica");
        assert harmonicaWeb != null;
        assert "C".equals(harmonicaWeb.getSelectedKey());
        assert "RICHTER".equals(harmonicaWeb.getSelectedTune());
        assert "440Hz".equals(session.getAttribute("selectedConcertPitch"));
        assert "High".equals(session.getAttribute("selectedConfidence"));
        assert "AlgorithmA".equals(session.getAttribute("selectedAlgorithm"));
        assert "true".equals(session.getAttribute("expertMode"));
    }

    /**
     * Tests that the ajaxProcessSelection method correctly processes input with only key and tune attributes.
     * <p>
     * This test verifies that when only the required key and tune attributes are provided,
     * the controller correctly:
     * <ul>
     *   <li>Returns a 200 OK status</li>
     *   <li>Returns a HarmonicaWeb object with the correct key and tune</li>
     *   <li>Stores only the provided attributes in the session, leaving others as null</li>
     * </ul>
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void ajaxProcessSelection_missingAttributes_shouldReturnHarmonicaWebAndSetPartialSessionAttributes() throws Exception {
        MockHttpSession session = new MockHttpSession();

        String inputJson = """
                {
                    "selectedKey": "G",
                    "selectedTune": "NATURALMOLL"
                }
                """;

        mockMvc.perform(post("/selection/send").contentType(MediaType.APPLICATION_JSON).content(inputJson).session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.selectedKey").value("G")).andExpect(jsonPath("$.selectedTune").value("NATURALMOLL"));

        HarmonicaWeb harmonicaWeb = (HarmonicaWeb) session.getAttribute("harmonica");
        assert harmonicaWeb != null;
        assert "G".equals(harmonicaWeb.getSelectedKey());
        assert "NATURALMOLL".equals(harmonicaWeb.getSelectedTune());
        assert session.getAttribute("selectedConcertPitch") == null;
        assert session.getAttribute("selectedConfidence") == null;
        assert session.getAttribute("selectedAlgorithm") == null;
        assert session.getAttribute("expertMode") == null;
    }

    /**
     * Tests that the ajaxProcessSelection method correctly handles a minimal request body.
     * <p>
     * This test verifies that when only the required fields (selectedKey and selectedTune)
     * are provided in the request body, the controller returns a 200 OK status without
     * throwing exceptions. This ensures the controller works with minimal input.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void ajaxProcessSelection_minimalBody_shouldReturnGoodRequest() throws Exception {
        String inputJson = """
                {
                    "selectedKey": "C",
                    "selectedTune": "RICHTER"
                }
                """;
        mockMvc.perform(post("/selection/send").contentType(MediaType.APPLICATION_JSON).content(inputJson)).andExpect(status().isOk());
    }
}
