package edu.eci.arep.httpserver;

import java.util.HashMap;

public class Response {
    private final HashMap<String, String> header = new HashMap<>();
    private byte[] body = new byte[0];
    private String status;

    public void header(String key, String value) {
        header.put(key, value);
    }

    HashMap<String, String> header() {
        return header;
    }

    public void body(byte[] body) {
        this.body = body;
    }

    public byte[] body() {
        return body;
    }

    public void status(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
