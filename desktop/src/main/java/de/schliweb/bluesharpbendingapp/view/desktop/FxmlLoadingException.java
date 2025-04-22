package de.schliweb.bluesharpbendingapp.view.desktop;
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


import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;


/**
 * This exception is thrown when an FXML file fails to load. It extends the
 * {@link RuntimeException} class, making it an unchecked exception. This allows
 * applications to handle FXML-related errors more flexibly.
 * <p>
 * FxmlLoadingException is typically used in scenarios where FXML markup
 * parsing or runtime loading encounters an issue, such as malformed XML,
 * missing resources, or I/O errors during the loading of the FXML file.
 * <p>
 * The constructor for this exception allows for the inclusion of an error
 * message and a cause, which enables better debugging and troubleshooting
 * when an exception occurs.
 */
public class FxmlLoadingException extends RuntimeException {

    /**
     * Constructs a new FxmlLoadingException with the specified detail message and cause.
     * This constructor allows for an exception to be created with both an explanatory
     * error message and a wrapped cause, which may aid in debugging issues related to
     * FXML file loading.
     *
     * @param message the detail message explaining the reason for the exception.
     * @param cause   the underlying cause of the exception (e.g., an IOException or
     *                parsing error). This can be null if no cause is available or applicable.
     */
    public FxmlLoadingException(String message, Throwable cause) {
        super(message, cause);
        LoggingUtils.logError("Error while loading the FXML", cause);
    }
}

