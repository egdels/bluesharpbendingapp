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
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * The type Webapp controller.
 */
@Controller
public class WebappController {

    /**
     * The Download directory.
     */
    @Value("${application.download-directory}")
    private String downloadDirectory;

    /**
     * The Download href.
     */
    @Value("${application.download-href}")
    private String downloadHref;

    /**
     * The Download directory old.
     */
    @Value("${application.download-directory-old}")
    private String downloadDirectoryOld;

    /**
     * The Download href old.
     */
    @Value("${application.download-href-old}")
    private String downloadHrefOld;

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
     * Download model and view.
     *
     * @return the model and view
     */
    @GetMapping("/download.html")
    public ModelAndView download() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("download");


        List<DownloadFile> downloadFiles = fetchDownloadFiles();
        modelAndView.addObject("downloadFiles", downloadFiles);
        return modelAndView;
    }

    /**
     * Fetch download files list.
     *
     * @return the list
     */
    private List<DownloadFile> fetchDownloadFiles() {
        List<DownloadFile> files = new ArrayList<>();
        File directoryPath = new File(downloadDirectory);

        for (File file : Objects.requireNonNull(directoryPath.listFiles())) {
            if (file.isDirectory()) continue;
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setOldVersion(false);
            downloadFile.setName(file.getName());
            downloadFile.setPath(file.getAbsolutePath());
            downloadFile.setSize(getSizeOfFile(file.getAbsolutePath()));
            downloadFile.setCreateDate(getCreationTimeOfFile(file.getAbsolutePath()));
            downloadFile.setHref(getHrefOfFile(downloadFile));

            files.add(downloadFile);
        }

        directoryPath = new File(downloadDirectoryOld);

        for (File file : Objects.requireNonNull(directoryPath.listFiles())) {
            if (file.isDirectory()) continue;
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setOldVersion(true);
            downloadFile.setName(file.getName());
            downloadFile.setPath(file.getAbsolutePath());
            downloadFile.setSize(getSizeOfFile(file.getAbsolutePath()));
            downloadFile.setCreateDate(getCreationTimeOfFile(file.getAbsolutePath()));
            downloadFile.setHref(getHrefOfFile(downloadFile));

            files.add(downloadFile);
        }

        files.sort(new DownloadFileComperator());

        return files;
    }

    /**
     * Gets href of file.
     *
     * @param downloadFile the download file
     * @return the href of file
     */
    private String getHrefOfFile(DownloadFile downloadFile) {
        String href = downloadHref;
        if (downloadFile.isOldVersion) {
            href = downloadHrefOld;
        }
        if (!"/".equals(downloadHref.substring(downloadHref.length() - 1))) {
            href = href + "/";
        }
        File file = new File(downloadFile.getPath());
        if (!file.isDirectory()) {
            href = href + file.getName();
        }
        return href;
    }

    /**
     * Gets size of file.
     *
     * @param pathname the pathname
     * @return the size of file
     */
    private String getSizeOfFile(String pathname) {
        String size = "-";
        File file = new File(pathname);
        if (!file.isDirectory()) {
            // File size in bytes
            long bytes = file.length();
            double kilobytes = (double) bytes / 1024;
            size = localizeNumber(kilobytes) + " KB";
        }
        return size;
    }

    /**
     * Gets creation time of file.
     *
     * @param pathname the pathname
     * @return the creation time of file
     */
    private String getCreationTimeOfFile(String pathname) {
        try {
            FileTime creationTime = (FileTime) Files.getAttribute(Path.of(pathname), "creationTime");
            return localizeDate(creationTime);
        } catch (IOException ex) {
            return "-";
        }
    }

    /**
     * Localize date string.
     *
     * @param fileTime the file time
     * @return the string
     */
    private String localizeDate(final FileTime fileTime) {
        Locale locale = LocaleContextHolder.getLocale();
        // Convert the FileTime to LocalDateTime
        LocalDateTime ldt = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());

        return ldt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale));
    }

    /**
     * Localize number string.
     *
     * @param number the number
     * @return the string
     */
    private String localizeNumber(final Double number) {
        Locale locale = LocaleContextHolder.getLocale();
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        return numberFormat.format(number);
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

        Harmonica harmonica = (Harmonica) session.getAttribute("harmonica");

        if (harmonica == null) {
            harmonica = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);
        }
        modelAndView.addObject("harmonica", harmonica);
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

        modelAndView.setViewName("letsbend");
        return modelAndView;
    }
}
