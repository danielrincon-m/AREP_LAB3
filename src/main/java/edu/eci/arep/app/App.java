package edu.eci.arep.app;

import static edu.eci.arep.nanosparkweb.NanoSpark.*;

public class App {

    /**
     * Método principal del programa que se encarga de registrar los servicios web
     *
     * @param args .
     */
    public static void main(String[] args) {
        staticFiles("/static");
        get("/register", (req, res) -> file2String("/static/register.html"));
        get("/registerAction", Database::RegisterUser);
        get("/get", (req, res) -> file2String("/static/get.html"));
        get("/getAction", Database::GetUsers);
        startServer();
    }

}
