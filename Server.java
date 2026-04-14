import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import controller.SystemController;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Server {

    static SystemController controller = new SystemController();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // ── Report Lost ──────────────────────────────────
        server.createContext("/reportLost", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { send(exchange, 405, "Method Not Allowed"); return; }
            addCorsHeaders(exchange);
            String body = readBody(exchange);
            // body format: name|category|person|location|description
            String[] parts = body.split("\\|", 5);
            String name = parts.length > 0 ? parts[0].trim() : body.trim();
            System.out.println("Received Lost Item: " + name);
            boolean matched = controller.addLostItem(name, parts);
            send(exchange, 200, matched ? "MATCH_FOUND" : "OK");
        });

        // ── Report Found ─────────────────────────────────
        server.createContext("/reportFound", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { send(exchange, 405, "Method Not Allowed"); return; }
            addCorsHeaders(exchange);
            String body = readBody(exchange);
            String[] parts = body.split("\\|", 5);
            String name = parts.length > 0 ? parts[0].trim() : body.trim();
            System.out.println("Received Found Item: " + name);
            boolean matched = controller.addFoundItem(name, parts);
            send(exchange, 200, matched ? "MATCH_FOUND" : "OK");
        });

        // ── Submit Claim ──────────────────────────────────
        server.createContext("/submitClaim", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { send(exchange, 405, "Method Not Allowed"); return; }
            addCorsHeaders(exchange);
            String body = readBody(exchange);
            // body format: itemName|claimerName|contact|proof
            String[] parts = body.split("\\|", 4);
            String itemName    = parts.length > 0 ? parts[0].trim() : "";
            String claimerName = parts.length > 1 ? parts[1].trim() : "Unknown";
            String contact     = parts.length > 2 ? parts[2].trim() : "";
            String proof       = parts.length > 3 ? parts[3].trim() : "";
            System.out.println("Claim submitted for: " + itemName + " by " + claimerName);
            controller.submitClaim(itemName, claimerName, contact, proof);
            send(exchange, 200, "CLAIM_RECEIVED");
        });

        // ── CORS preflight ────────────────────────────────
        server.createContext("/", exchange -> {
            addCorsHeaders(exchange);
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                send(exchange, 200, "Digital Lost & Found Server");
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("? Server running at http://localhost:8080");
    }

    // ─────────────────── helpers ────────────────────────

    private static String readBody(HttpExchange e) throws IOException {
        try (InputStream is = e.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void send(HttpExchange e, int code, String body) throws IOException {
        addCorsHeaders(e);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        e.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = e.getResponseBody()) { os.write(bytes); }
    }

    private static void addCorsHeaders(HttpExchange e) {
        e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        e.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        e.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
}