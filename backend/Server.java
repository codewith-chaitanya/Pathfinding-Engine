package backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/api/path", new PathHandler());
        server.setExecutor(null);
        System.out.println("Server started on port " + port);
        server.start();
    }

    static class PathHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 1. Handle CORS (Allow frontend to talk to backend)
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            // 2. Read Request (Grid Data)
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes());
            
            // 3. Simple Parser (Because we have no libraries)
            // Expecting body: "0,0|5,5|0:1,1:1" (Start|End|Walls)
            // Format: StartX,StartY | EndX,EndY | WallX:WallY,WallX:WallY...
            
            try {
                String[] parts = body.split("\\|");
                String[] startCoords = parts[0].split(",");
                String[] endCoords = parts[1].split(",");
                
                int rows = 20, cols = 20;
                Node[][] grid = new Node[rows][cols];
                for(int i=0; i<rows; i++) 
                    for(int j=0; j<cols; j++) grid[i][j] = new Node(i, j);

                // Set Walls
                if (parts.length > 2 && !parts[2].isEmpty()) {
                    String[] walls = parts[2].split(",");
                    for (String w : walls) {
                        String[] wc = w.split(":");
                        grid[Integer.parseInt(wc[0])][Integer.parseInt(wc[1])].isWall = true;
                    }
                }

                Node start = grid[Integer.parseInt(startCoords[0])][Integer.parseInt(startCoords[1])];
                Node end = grid[Integer.parseInt(endCoords[0])][Integer.parseInt(endCoords[1])];

                // 4. Run Algorithm
                List<Node> path = Dijkstra.findPath(grid, start, end);

                // 5. Send Response (JSON format)
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < path.size(); i++) {
                    json.append(path.get(i).toString());
                    if (i < path.size() - 1) json.append(",");
                }
                json.append("]");

                byte[] response = json.toString().getBytes();
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                String err = "Error: " + e.getMessage();
                exchange.sendResponseHeaders(500, err.length());
                exchange.getResponseBody().write(err.getBytes());
                exchange.close();
            }
        }
    }
}