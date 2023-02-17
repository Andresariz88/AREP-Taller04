package edu.eci.arep.webapps;

import edu.eci.arep.server.HttpServer;

import java.io.IOException;

public class WebApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        HttpServer server = HttpServer.getInstance();

        server.staticFiles.location("/public");

        server.get("/hello", (req, res) -> "Hello World");
        server.get("/get-json", (req, res) -> {
            res.type("application/json");
            return "{\"name\": \"Daniel\"}";
        });
        server.get("/get-css", (req, res) -> {
            res.type("text/css");
            return "* {\n" +
                    "    font-family: sans-serif;\n" +
                    "    background-color: #f5f6fa;\n" +
                    "}";
        });

        server.post("/json-post", (req, res) -> {
            res.type("application/json");
            return "{\"name\": \"Daniel\"}";
        });



        server.run(args);

    }
}
