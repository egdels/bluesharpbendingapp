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

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configuration class for internationalization (i18n) and message handling in the web application.
 * This class sets up the necessary beans for locale resolution and message source configuration,
 * enabling the application to support multiple languages and localized messages.
 * <p>
 * The configuration includes:
 * - A locale resolver that determines the user's locale based on the Accept-Language header
 * - A message source that loads localized messages from properties files
 */
@Configuration
public class MessageConfig implements WebMvcConfigurer {

    /**
     * Creates and configures a LocaleResolver bean for the application.
     * <p>
     * This method sets up an AcceptHeaderLocaleResolver that determines the user's
     * locale based on the Accept-Language header in HTTP requests. If no locale
     * is specified in the header, it defaults to US English (Locale.US).
     * <p>
     * The locale resolver is essential for internationalization as it determines
     * which language resources should be used for each request.
     *
     * @return a configured AcceptHeaderLocaleResolver instance
     */
    @Bean
    public LocaleResolver localeResolver() {
        final AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }

    /**
     * Creates and configures a MessageSource bean for the application.
     * <p>
     * This method sets up a ResourceBundleMessageSource that loads localized messages
     * from properties files in the classpath. The message source is configured to:
     * <ul>
     *   <li>Look for message files with the base name "language/messages"
     *       (e.g., messages_en.properties, messages_de.properties)</li>
     *   <li>Use UTF-8 encoding for reading the properties files</li>
     * </ul>
     * <p>
     * The message source is a core component of Spring's internationalization support,
     * allowing the application to retrieve localized messages based on the current locale.
     *
     * @return a configured ResourceBundleMessageSource instance
     */
    @Bean("messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource =
                new ResourceBundleMessageSource();
        messageSource.setBasenames("language/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
