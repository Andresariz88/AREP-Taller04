package edu.eci.arep.server;

import edu.eci.arep.Annotations.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java server which exposes the movie search services
 */
public class HttpServer {
    private static HttpServer _instance = new HttpServer();

    private HttpServer (){}
    public static HttpServer getInstance() {
        return _instance;
    }
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "https://omdbapi.com/?t=%S&apikey=1d53bda9";
    public static final ConcurrentHashMap<String, String> movieCache = new ConcurrentHashMap<>();

    private Map<String, HttpResponse> gets = new HashMap<>();
    private Map<String, HttpResponse> posts = new HashMap<>();
    public final StaticFiles staticFiles = new StaticFiles();
    private Map<String, Method> methods = new HashMap<>();

    /**
     * Method that starts the server and handle the requests according to what is required
     * @throws IOException
     */
    public void run(List<String> args) throws IOException, ClassNotFoundException {

            for (String className : args) {
                // Cargar clase con forName
                Class<?> c = Class.forName(className);
                Method[] classMethos = c.getMethods();
                for (Method m : classMethos) {  // Extraer una instancia del método
                    if (m.isAnnotationPresent(RequestMapping.class)) { // Extraer métodos con @RequestMapping
                        String path = m.getAnnotation(RequestMapping.class).value(); // Extraer el valor del path (value de la anotación)
                        methods.put(path, m); // Poner en la tabla el método con llave path
                    }
                }
            }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean firstLine = true;
            String request = "/form";
            String method = "GET";
            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    request = inputLine.split(" ")[1];
                    method = inputLine.split(" ")[0];
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            String requestedMovie;
            if (method.equalsIgnoreCase("GET")) {
                try {
                    if (request.equalsIgnoreCase("/")) {
                    outputLine = staticFiles.getFile("/apps/form.html");
                    } else if (request.startsWith("/spring/")) { // Métodos inyectados
                        System.out.println("ME METÍ");
                        outputLine = "HTTP/1.1 200 OK\r\n" +
                                "Content-type: text/html\r\n" +
                                "\r\n" + methods.get(request.substring(7)).invoke(null);
                        System.out.println(outputLine);
                    } else if (staticFiles.checkFile(request)) {
                        System.out.println("ESTÁ EN STATIC");
                        outputLine = staticFiles.getFile(request);
                    } else {
                        outputLine = gets.get(request).getResponse();
                    }
                }
                catch (NullPointerException e) {
                    outputLine = staticFiles.getFile("/404.html");
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else /*if (method.equalsIgnoreCase("POST"))*/ {
                try {
                    if (request.startsWith("/form?")) {
                        requestedMovie = request.replace("/form?s=", "");
                        outputLine = "HTTP/1.1 200 OK\r\n" +
                                "Content-type: application/json\r\n" +
                                "\r\n"
                                + getMovie(requestedMovie.toLowerCase());
                    } else {
                        System.out.println("DEVOLVIENDO: " + posts.get(request).getResponse());
                        outputLine = posts.get(request).getResponse();
                    }
                } catch (NullPointerException e) {
                    outputLine = "";
                }
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * Makes a resource accessible via get method
     * @param path resource path
     * @param route resource
     */
    public void get(String path, Route route) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.body(route.handle("req", httpResponse));
        gets.put(path, httpResponse);
    }

    /**
     * Makes a resource accessible via post method
     * @param path resource path
     * @param route resource
     */
    public void post(String path, Route route) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.body(route.handle("req", httpResponse));
        posts.put(path, httpResponse);
    }

    /**
     * Method that gets the movie entered by the user, either from the cache or from the API
     * @param movie movie to look for
     * @return String corresponding to the movie information in JSON format
     * @throws IOException
     */
    public String getMovie(String movie) throws IOException {
        String reqMovie = "";
        if (movieCache.containsKey(movie)) {
            reqMovie = movieCache.get(movie);
            return reqMovie;
        }
        String formatted = String.format(GET_URL, movie);
        URL obj = new URL(formatted);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            reqMovie = response.toString();
            movieCache.put(movie, reqMovie);
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
        return reqMovie;
    }
}
