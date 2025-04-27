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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main Spring Boot application class for the BlueSharp Bending web application.
 * This class serves as the entry point for the Spring Boot application and is responsible
 * for bootstrapping and launching the web application.
 * 
 * By extending SpringBootServletInitializer, this application can be deployed as a traditional
 * WAR file and run in external servlet containers, in addition to being runnable as a
 * standalone application with an embedded server.
 */
@SpringBootApplication
public class WebappApplication extends SpringBootServletInitializer {

    /**
     * The main entry point of the application. This method starts the Spring Boot application,
     * initializes the Spring application context, and launches the embedded web server.
     *
     * @param args command line arguments passed to the application. These can include
     *             standard Spring Boot configuration parameters such as --server.port
     *             to specify the port on which the application should run.
     */
    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }

}
