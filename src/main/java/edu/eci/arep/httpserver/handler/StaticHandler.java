package edu.eci.arep.httpserver.handler;

import edu.eci.arep.httpserver.Request;
import edu.eci.arep.httpserver.Response;
import edu.eci.arep.nanosparkweb.NanoSparkServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StaticHandler implements Handler<String> {
    @Override
    public Response handle(String prefix, Request req) {
        Response res = new Response();
        String path = (prefix + req.getRequestAttribute("GET")).substring(1);
        System.out.println("Path: " + path);
        try {
            res.body(NanoSparkServer.getFile(path));
            res.status("200 OK");
            res.header("Content-Type", NanoSparkServer.getMimeType(path));
        } catch (IOException e) {
            res.status("404 Not Found");
            res.body("<html><body><h1>404!</h1></body></html>".getBytes(StandardCharsets.UTF_8));
        }
        return res;
    }
}
