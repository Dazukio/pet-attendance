package pet.attendance.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

public class Server {
    private HttpServer server;

    public Server() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);
    }

    public void addHandler(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    public void start() {
        server.start();
        System.out.println("Server started on port 8080!");
    }
}
