/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.schliweb.bluesharpbendingapp.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test class for the WebappApplication.
 *
 * <p>This class contains tests that verify the Spring application context loads correctly with all
 * required beans and configurations. These tests ensure that the application can start up without
 * errors and that all components are properly wired together.
 */
@SpringBootTest
class WebappApplicationTests {

  /**
   * Tests that the Spring application context loads successfully.
   *
   * <p>This test verifies that the Spring application context can be initialized without errors,
   * which confirms that all beans are properly configured and wired together. It's a basic sanity
   * check that ensures the application's core configuration is valid.
   *
   * <p>The test passes if the context loads without throwing any exceptions.
   */
  @Test
  void contextLoads() {
    // This test ensures that the Spring application context loads successfully.
  }
}
