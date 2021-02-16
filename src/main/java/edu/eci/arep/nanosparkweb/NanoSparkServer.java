package edu.eci.arep.nanosparkweb;

import com.github.amr.mimetypes.MimeTypes;
import edu.eci.arep.httpserver.HttpServer;
import edu.eci.arep.httpserver.Request;
import edu.eci.arep.httpserver.Response;
import edu.eci.arep.httpserver.handler.Handler;
import edu.eci.arep.httpserver.handler.StaticHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * Framework web en miniatura que presenta servicios básicos para publicar un sitio web de manera sencilla
 */
public class NanoSparkServer implements Handler<String> {

    private static final NanoSparkServer _theInstance = new NanoSparkServer();

    public static NanoSparkServer getInstance() {
        return _theInstance;
    }

    /**
     * Intenta buscar un archivo en los recursos del servidor
     *
     * @param path La ruta al archivo buscado
     * @return Un arreglo de bytes con la información del archivo en binario
     * @throws IOException Cuando no existe el archivo buscado
     */
    public static byte[] getFile(String path) throws IOException {
        try {
            URL resource = HttpServer.class.getClassLoader().getResource(path);
            File file = new File(URLDecoder.decode(resource.getPath(), "UTF-8"));
            return Files.readAllBytes(file.toPath());
        } catch (NullPointerException e) {
            throw new IOException("No se ha encontrado el archivo");
        }
    }

    /**
     * Obtiene el MimeType de un archivo según su extensión, utiliza una librería que contiene
     * una gran base de datos de MimeTypes
     *
     * @param path La ruta al archivo
     * @return El MymeType del archivo
     */
    public static String getMimeType(String path) {
        String[] splPath = path.split("\\.");
        String ext = splPath[splPath.length - 1];
        return MimeTypes.getInstance().getByExtension(ext).getMimeType();
    }


    private final HttpServer httpServer = new HttpServer();
    private final Map<String, BiFunction<Request, Response, String>> bodyMap = new HashMap<>();
    private String staticFiles = "/";

    private NanoSparkServer() {
        httpServer.registerHandler(this, "/Apps");
    }

    /**
     * Inicia el servidor web y comienza la escucha activa de peticiones
     */
    public void startServer() {
        try {
            httpServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Establece la ruta en donde se almacenarán los archivso estáticos
     *
     * @param path La ruta
     */
    public void staticFiles(String path) {
        staticFiles = path.startsWith("/") ? path : "/" + path;
    }

    /**
     * Establece un supplier para una ruta específica al momento de recibir una petición GET
     *
     * @param path     La ruta a registrar
     * @param supplier El supplier que manejará la petición
     */
    public void get(String path, BiFunction<Request, Response, String> supplier) {
        bodyMap.put(path, supplier);
    }

    /**
     * Obtiene el supplier registrado para una ruta específica
     *
     * @param path La ruta del supplier
     */
    private BiFunction<Request, Response, String> getValue(String path) {
        return bodyMap.get(path);
    }

    @Override
    public Response handle(String prefix, Request req) {
        Response res = new Response();
        String fullPath = req.getRequestURL();
        String path = fullPath.replaceFirst(Pattern.quote(prefix), "");

        if (bodyMap.containsKey(path)) {
            res.body(getValue(path).apply(req, res).getBytes(StandardCharsets.UTF_8));
            res.status("200 OK");
            res.header("Content-Type", "text/html");
        } else {
            req.removePrefix(prefix);
            return new StaticHandler().handle(staticFiles, req);
        }
        return res;
    }
}
