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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SelectionController.class)
class SelectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HttpSession httpSession;

    @Test
    void ajaxProcessSelection_validInput_shouldReturnHarmonicaWebAndSetSessionAttributes() throws Exception {
        MockHttpSession session = new MockHttpSession();

        String inputJson = """
                {
                    "selectedKey": "C",
                    "selectedTune": "Major",
                    "selectedConcertPitch": "440Hz",
                    "selectedConfidence": "High",
                    "selectedAlgorithm": "AlgorithmA",
                    "expertMode": "true"
                }
                """;

        mockMvc.perform(post("/selection/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedKey").value("C"))
                .andExpect(jsonPath("$.selectedTune").value("Major"));

        HarmonicaWeb harmonicaWeb = (HarmonicaWeb) session.getAttribute("harmonica");
        assert harmonicaWeb != null;
        assert "C".equals(harmonicaWeb.getSelectedKey());
        assert "Major".equals(harmonicaWeb.getSelectedTune());
        assert "440Hz".equals(session.getAttribute("selectedConcertPitch"));
        assert "High".equals(session.getAttribute("selectedConfidence"));
        assert "AlgorithmA".equals(session.getAttribute("selectedAlgorithm"));
        assert "true".equals(session.getAttribute("expertMode"));
    }

    @Test
    void ajaxProcessSelection_missingAttributes_shouldReturnHarmonicaWebAndSetPartialSessionAttributes() throws Exception {
        MockHttpSession session = new MockHttpSession();

        String inputJson = """
                {
                    "selectedKey": "G",
                    "selectedTune": "Minor"
                }
                """;

        mockMvc.perform(post("/selection/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selectedKey").value("G"))
                .andExpect(jsonPath("$.selectedTune").value("Minor"));

        HarmonicaWeb harmonicaWeb = (HarmonicaWeb) session.getAttribute("harmonica");
        assert harmonicaWeb != null;
        assert "G".equals(harmonicaWeb.getSelectedKey());
        assert "Minor".equals(harmonicaWeb.getSelectedTune());
        assert session.getAttribute("selectedConcertPitch") == null;
        assert session.getAttribute("selectedConfidence") == null;
        assert session.getAttribute("selectedAlgorithm") == null;
        assert session.getAttribute("expertMode") == null;
    }

    @Test
    void ajaxProcessSelection_emptyBody_shouldReturnGoodRequest() throws Exception {
        mockMvc.perform(post("/selection/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }
}