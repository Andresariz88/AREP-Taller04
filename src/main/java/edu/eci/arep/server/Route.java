package edu.eci.arep.server;

public interface Route {
    /**
     * Handles and return the body of the HttpResponse
     * @param req Client request
     * @param res HttpServer response
     * @return the body of the HttpServer response
     */
    public String handle(String req, HttpResponse res);
}
