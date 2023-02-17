package edu.eci.arep.server;

import java.util.Base64;

/**
 * Class corresponding to the Http Server Responses.
 * By default, it is a Text/HTML response.
 */
public class HttpResponse {
    int SC_OK = 200;
    int SC_BAD_REQUEST = 400;
    private String status;
    private String type;
    private String body;

    public HttpResponse() {
        status = "HTTP/1.1 200 OK";
        type = "Content-type: text/html";
    }

    public HttpResponse(String body) {
        status = "HTTP/1.1 200 OK";
        type = "Content-type: text/html";
        this.body = body;
    }

    /**
     * Sets the type of the Http message according to the file extension
     * @param file
     */
    public void setSpecificType(String file) {
        String extension = file.split("\\.")[1];
        if (extension.equalsIgnoreCase("html")) {
            type = "Content-type: text/html";
        } else if (extension.equalsIgnoreCase("css")) {
            type = "Content-type: text/css";
        } else if (extension.equalsIgnoreCase("js")) {
            type = "Content-type: application/javascript";
        } else if (extension.equalsIgnoreCase("png")) {
            type = "Content-type: image/png";
            body = Base64.getEncoder().encodeToString(HttpServer.getInstance().staticFiles.getImg(file));
        }
    }

    public String getResponse() {
        return status + "\r\n" + type + "\r\n" + "\r\n" + body;
    }

    public String status() {
        return status;
    }

    public void status(String status) {
        this.status = status;
    }

    public String type() {
        return type;
    }

    public void type(String type) {
        this.type = "Content-type: " + type;
    }

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
    }
}
