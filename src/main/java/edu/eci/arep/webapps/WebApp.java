package edu.eci.arep.webapps;

import edu.eci.arep.Annotations.Component;
import edu.eci.arep.server.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WebApp { //java -cp target/classes edu.eci.arep.webapps.WebApp
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

        server.run(getComponents());

    }

    /**
     * Method that obtains all the Classes that has the Component annotation
     * @return List of classes names
     * @throws IOException
     * @throws ClassNotFoundException if class not found
     */
    public static List<String> getComponents() throws IOException, ClassNotFoundException {
        String path = "edu/eci/arep/webapps";
        List<String> componentClasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            File[] files = directory.listFiles();
            for (File file : files) {
                String fileName = file.getName();
                if (file.getName().endsWith(".class")) {
                    String className = fileName.substring(0, fileName.length() - 6);
                    Class<?> clase = Class.forName(path.replace("/", ".") + "." + className);
                    if (clase.isAnnotationPresent(Component.class)) {
                        componentClasses.add(clase.getName());
                    }
                }
            }
        }
        return componentClasses;
    }
}
