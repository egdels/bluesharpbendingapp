package de.schliweb.bluesharpbendingapp.model;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;

/**
 * The ModelStorageService class offers functionalities for persisting and retrieving
 * instances of the MainModel class to and from a temporary file storage.
 * This service simplifies the process of saving application state or configurations
 * while ensuring proper handling of file system operations.
 * <p>
 * The service performs serialization of MainModel to a temporary file and
 * deserialization back into an object. It also creates necessary directories
 * if they do not exist, ensuring seamless file storage.
 * <p>
 * The temporary file is stored in the user's home directory with a predefined sub-path.
 */
@Slf4j
public class ModelStorageService {


    /**
     * Represents the path to a temporary directory used for storing
     * temporary files or data during the application's runtime.
     * The value is intended to remain constant and should not be modified after initialization.
     */
    private final String tempDirectory;

    /**
     * Represents the name or path of a temporary file.
     * This variable is immutable and cannot be modified after being initialized.
     * It is used to store or reference the file location for temporary operations.
     */
    private final String tempFile;


    /**
     * Constructs a new ModelStorageService with the specified temporary directory and file.
     *
     * @param tempDirectory the directory path where temporary files will be stored
     * @param tempFile      the name of the temporary file
     */
    public ModelStorageService(String tempDirectory, String tempFile) {
        this.tempDirectory = tempDirectory;
        this.tempFile = tempFile;
    }

    /**
     * Persists the given MainModel instance to a temporary file for storage.
     * The method ensures that the required directory and file are created if they do not exist.
     * The model's data is serialized into a string format and written to the file.
     *
     * @param model The MainModel object containing the data to be stored.
     */
    public void storeModel(MainModel model) {

        File directory = new File(tempDirectory);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) log.debug("Directory created by storeModel()");
        }
        File file = new File(tempDirectory + FileSystems.getDefault().getSeparator() + tempFile);
        try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
            boolean isCreated = file.createNewFile();
            if (isCreated) log.debug("Filed created");

            bw.write(model.getString());

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    /**
     * Reads and returns a MainModel instance from a temporary file storage location.
     * The method checks for the existence of a predefined directory and file for storing the model.
     * If the file exists, it deserializes the model's configuration from the file's content.
     * If the file or directory does not exist, a new instance of MainModel is returned.
     *
     * @return A MainModel object populated with data read from the file,
     * or a new MainModel instance if the file is not available or cannot be read.
     */
    public MainModel readModel() {

        MainModel model = new MainModel();
        File directory = new File(tempDirectory);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) log.debug("Directory created by readModel()");
        }
        File file = new File(tempDirectory + FileSystems.getDefault().getSeparator() + tempFile);
        if (file.exists()) {
            try (FileInputStream fos = new FileInputStream(file); BufferedReader bw = new BufferedReader(new InputStreamReader(fos))) {

                String line = bw.readLine();
                if (line != null) model = createFromString(line);

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return model;
    }


    /**
     * Creates a MainModel instance from a given string representation.
     * The input string is expected to be in a specific format, where
     * key-value pairs are defined as "getMethodName:value" and separated
     * by a delimiter. The method dynamically invokes corresponding setters
     * in the MainModel class to populate its fields based on the provided input.
     *
     * @param string the string representation of the MainModel object,
     *               containing key-value pairs to initialize the fields.
     * @return a newly created MainModel instance populated with values
     * based on the input string.
     */
    private MainModel createFromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        MainModel model = new MainModel();
        Method[] methods = model.getClass().getMethods();

        for (String entry : strings) {
            entry = entry.replaceFirst("get", "set");
            if (entry.contains(":")) {
                String m = entry.split(":")[0];
                String p = entry.split(":")[1];
                for (Method method : methods) {
                    if (method.getName().equals(m)) {
                        try {
                            method.invoke(model, Integer.parseInt(p));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return model;
    }

}
