package edu.eci.arep.httpserver;

import edu.eci.arep.httpserver.handler.StaticHandler;
import edu.eci.arep.httpserver.handler.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class HttpServer {

    private final Map<String, Handler<String>> handlers = new HashMap<>();

    public HttpServer() {
        super();
    }

    public void registerHandler(Handler<String> h, String prefix) {
        handlers.put(prefix, h);
    }

    public void startServer() throws IOException {
        ServerSocket serverSocket = null;
        int port = HttpServer.getPort();
        handlers.put("/", new StaticHandler());
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
            System.exit(1);
        }

        while (true) {
            acceptRequest(serverSocket);
        }
//        serverSocket.close();
    }

    private void acceptRequest(ServerSocket serverSocket) throws IOException {
        Socket clientSocket = null;

        try {
            System.out.println("Listo para recibir ...");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        Request request = ParseRequest(in);

        if (request != null) {
            System.out.println("Requested: " + request.getRequestAttribute("GET"));
            //byte[] output = HttpServer.getFileResopnse(request.getRequestAttribute("GET"));
            Response response = useHandler(request);
            byte[] output = constructResponse(response);
            clientSocket.getOutputStream().write(output);
        }

        in.close();
        clientSocket.close();
    }

    private Request ParseRequest(BufferedReader in) throws IOException {
        String inputLine = in.readLine();
        String[] splittedInput;
        Request request = new Request();

        try {
            splittedInput = Arrays.stream(inputLine.split(" ")).map(String::trim).toArray(String[]::new);
            request.setRequestAttribute(splittedInput[0], splittedInput[1]);
            while ((inputLine = in.readLine()) != null) {
                splittedInput = Arrays.stream(inputLine.split(":")).map(String::trim).toArray(String[]::new);
                if (splittedInput.length >= 2) request.setRequestAttribute(splittedInput[0], splittedInput[1]);
                if (!in.ready()) {
                    break;
                }
            }
            return request;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private Response useHandler(Request request) {
        String prefix = "/" + request.getRequestAttribute("GET").split("/")[1];
        if (handlers.containsKey(prefix)) {
            return handlers.get(prefix).handle(prefix, request);
        } else {
            System.out.println("Default handler");
            return handlers.get("/").handle("", request);
        }
    }

    private byte[] constructResponse(Response res) {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("HTTP/1.1 ").append(res.status()).append("\r\n");
        for (Map.Entry<String, String> headerItem : res.header().entrySet()) {
            headerBuilder.append(headerItem.getKey()).append(": ").append(headerItem.getValue()).append("\r\n");
        }
        headerBuilder.append("\r\n");

        byte[] header = headerBuilder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] output = new byte[res.body().length + header.length];

        for (int i = 0; i < output.length; ++i) {
            output[i] = i < header.length ? header[i] : res.body()[i - header.length];
        }
        return output;
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 36000; //returns default port if heroku-port isn't set
    }
}
