package edu.eci.arep.httpserver;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private final Map<String, String> requestMap = new HashMap<>();

    public void setRequestAttribute(String key, String value) {
        requestMap.put(key, value);
    }

    public String getRequestAttribute(String key) {
        return requestMap.get(key);
    }
}
