package edu.eci.arep.webapps;

import edu.eci.arep.Annotations.Component;
import edu.eci.arep.Annotations.RequestMapping;

@Component
public class FirstWebService {

    @RequestMapping("/hello")
    public static String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/bye")
    public static String bye() {
        return "Bye from Spring Boot!";
    }
}
