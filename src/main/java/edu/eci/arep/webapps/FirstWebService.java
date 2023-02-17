package edu.eci.arep.webapps;

import edu.eci.arep.Annotations.RequestMapping;

public class FirstWebService {

    @RequestMapping("/hello")
    public static String index() {
        return "Greetings from Spring Boot!";
    }
}
