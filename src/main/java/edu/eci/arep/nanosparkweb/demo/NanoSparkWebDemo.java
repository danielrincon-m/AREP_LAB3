package edu.eci.arep.nanosparkweb.demo;

import static edu.eci.arep.nanosparkweb.NanoSpark.*;

public class NanoSparkWebDemo {

    public static void main(String[] args) {
        get("/hello", (req, res) -> "<html><body><h2>Funciona!</h2> <img src='holis.png'/></body></html>");
        staticFiles("/static");
        startServer();
    }

}
