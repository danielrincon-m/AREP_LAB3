package edu.eci.arep.httpserver.handler;

import edu.eci.arep.httpserver.Request;
import edu.eci.arep.httpserver.Response;

import java.util.function.BiFunction;

public interface Handler<T> {
    Response handle(String prefix, Request req);
}
