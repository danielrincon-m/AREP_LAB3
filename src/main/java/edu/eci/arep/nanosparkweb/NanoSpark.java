package edu.eci.arep.nanosparkweb;

import edu.eci.arep.httpserver.HttpServer;
import edu.eci.arep.httpserver.Request;
import edu.eci.arep.httpserver.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.function.BiFunction;

/**
 * Clase que actúa como interfaz entre el servidor de Spark y el cliente, dándole al programador una
 * API amigable de métodos estáticos con los cuales trabajar
 *
 * @author Daniel Rincón
 */
public class NanoSpark {

    /**
     * Establece un supplier para una ruta específica al momento de recibir una petición GET
     *
     * @param path     La ruta a registrar
     * @param supplier El supplier que manejará la petición
     */
    public static void get(String path, BiFunction<Request, Response, String> supplier) {
        NanoSparkServer nanosrv = NanoSparkServer.getInstance();
        nanosrv.get(path, supplier);
    }

    /**
     * Establece la ruta en donde se almacenarán los archivso estáticos
     *
     * @param path La ruta
     */
    public static void staticFiles(String path) {
        NanoSparkServer nanosrv = NanoSparkServer.getInstance();
        nanosrv.staticFiles(path);
    }

    /**
     * Inicia el servidor web y comienza la escucha activa de peticiones
     */
    public static void startServer() {
        NanoSparkServer nanosrv = NanoSparkServer.getInstance();
        nanosrv.startServer();
    }

    /**
     * Toma un archivo html y lo convierte en String para ser devuelto al cliente
     *
     * @param path La ruta del archivo
     * @return El archivo en una cadena
     */
    public static String file2String(String path) {
        StringBuilder contentBuilder = new StringBuilder();
        path = path.startsWith("/") ? path.substring(1) : path;
        try {
            String decodedPath = URLDecoder.decode(
                    HttpServer.class.getClassLoader().getResource(path).getPath(), "UTF-8");
            FileReader fileReader = new FileReader(decodedPath);
            BufferedReader in = new BufferedReader(fileReader);
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str).append('\n');
            }
            in.close();
        } catch (NullPointerException | IOException e) {
            return path + " not found.";
        }
        return contentBuilder.toString();
    }
}
