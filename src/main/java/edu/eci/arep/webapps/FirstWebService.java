package edu.eci.arep.webapps;

import edu.eci.arep.Annotations.Component;
import edu.eci.arep.Annotations.RequestMapping;

@Component
public class FirstWebService {

    @RequestMapping("/hello")
    public static String json() {
        return  "HTTP/1.1 200 OK\r\n" +
                "Content-type: application/json\r\n" +
                "\r\n" +
                "Greetings from Spring Boot!";
    }

    @RequestMapping("/html")
    public static String html() {
        return  "HTTP/1.1 200 OK\r\n" +
                "Content-type: text/html\r\n" +
                "\r\n"
                + "Esto es un html";
    }

    @RequestMapping("/css")
    public static String css() {
        return  "HTTP/1.1 200 OK\r\n" +
                "Content-type: text/css\r\n" +
                "\r\n"
                + "\n" +
                "* {\n" +
                "    font-family: \"Roboto\", sans-serif;\n" +
                "    background-color: #f5f6fa;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "    margin: 0;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "    padding: 5px;\n" +
                "    margin: 7px 2px;\n" +
                "    font-size: 2.5em;\n" +
                "    background-color: white;\n" +
                "}";
    }
}
