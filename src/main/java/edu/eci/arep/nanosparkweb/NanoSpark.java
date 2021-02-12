package edu.eci.arep.nanosparkweb;

import edu.eci.arep.httpserver.Request;
import edu.eci.arep.httpserver.Response;

import java.util.function.BiFunction;

public class NanoSpark {

    public static void get (String path, BiFunction<Request, Response, String> supplier) {
        NanoSparkServer nanosrv = NanoSparkServer.getInstance();
        nanosrv.get(path, supplier);
    }

    public static void staticFiles(String path) {
        NanoSparkServer nanosrv = NanoSparkServer.getInstance();
        nanosrv.staticFiles(path);
    }

    public static void startServer() {
        NanoSparkServer nanosrv = NanoSparkServer.getInstance();
        nanosrv.startServer();
    }

    public static void file2String (String path) {

    }
}
