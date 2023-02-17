package edu.eci.arep.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class that sets the default path to the static files and handles the file reading
 */
public class StaticFiles {
    private String location;
    public static final String ROOT = "src/main/resources";

    public StaticFiles() {
        location = ROOT;
    }

    /**
     * Checks if a file is in the static files folder
     * @param file file
     * @return yes if the file exists, no otherwise
     */
    public boolean checkFile(String file) {
        boolean res;
        try {
            Files.readAllBytes(Paths.get(location + file));
            res = true;
        } catch (IOException e) {
            res = false;
        }
        return res;
    }

    /**
     * Gets a file from the static files folder
     * @param file file
     * @return String corresponding to the Http response message
     */
    public String getFile(String file) {
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(Paths.get(location + file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String body = new String(fileContent);
        HttpResponse httpResponse = new HttpResponse(body);
        httpResponse.setSpecificType(file);
        return httpResponse.getResponse();
    }

    public byte[] getImg(String file) {
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(Paths.get(location + file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileContent;
    }

    public String location() {
        return location;
    }

    public void location(String location) {
        this.location = ROOT + location;
    }
}
