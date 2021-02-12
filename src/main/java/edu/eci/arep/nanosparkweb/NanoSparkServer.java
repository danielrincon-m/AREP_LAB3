package edu.eci.arep.nanosparkweb;

import edu.eci.arep.httpserver.HttpServer;
import edu.eci.arep.httpserver.Request;
import edu.eci.arep.httpserver.Response;
import edu.eci.arep.httpserver.handler.Handler;
import edu.eci.arep.httpserver.handler.StaticHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class NanoSparkServer implements Handler<String> {

    private static final NanoSparkServer _theInstance = new NanoSparkServer();

    public static NanoSparkServer getInstance() {
        return _theInstance;
    }

    public static byte[] getFile(String path) throws IOException {
        try {
            URL resource = HttpServer.class.getClassLoader().getResource(path);
            File file = new File(URLDecoder.decode(resource.getPath(), "UTF-8"));
            return Files.readAllBytes(file.toPath());
        } catch (NullPointerException e) {
            throw new IOException("No se ha encontrado el archivo");
        }
    }

    public static String getMimeType(String path) throws IOException {
        try {
            URL resource = HttpServer.class.getClassLoader().getResource(path);
            File file = new File(URLDecoder.decode(resource.getPath(), "UTF-8"));
            return URLConnection.getFileNameMap().getContentTypeFor(file.getName());
        } catch (NullPointerException e) {
            throw new IOException("No se ha encontrado el archivo");
        }
    }


    private final HttpServer httpServer = new HttpServer();
    private final Map<String, BiFunction<Request, Response, String>> bodyMap = new HashMap<>();
    private String staticFiles = "/";

    private NanoSparkServer() {
        httpServer.registerHandler(this, "/Apps");
    }

    public void startServer() {
        try {
            httpServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void staticFiles(String path) {
        staticFiles = path;
    }

    public void get(String path, BiFunction<Request, Response, String> supplier) {
        bodyMap.put(path, supplier);
    }

    private BiFunction<Request, Response, String> getValue(String path) {
        return bodyMap.get(path);
    }

    @Override
    public Response handle(String prefix, Request req) {
        Response res = new Response();
        String fullPath = req.getRequestAttribute("GET");
        String path = fullPath.replaceFirst(Pattern.quote(prefix), "");

        try {
            res.body(getValue(path).apply(req, res).getBytes(StandardCharsets.UTF_8));
            res.status("200 OK");
            res.header("Content-Type", "text/html");
        } catch (NullPointerException e) {
            req.setRequestAttribute("GET", path);
            return new StaticHandler().handle(staticFiles, req);
        }
        return res;
    }
}
