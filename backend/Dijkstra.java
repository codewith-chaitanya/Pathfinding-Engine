package backend;

import java.util.*;

public class Dijkstra {
    public static List<Node> findPath(Node[][] grid, Node start, Node end) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        start.distance = 0;
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current == end) return reconstructPath(end);

            for (Node neighbor : getNeighbors(grid, current)) {
                if (neighbor.isWall) continue;
                
                // Distance calculation (Edge weight = 1)
                int newDist = current.distance + 1;
                if (newDist < neighbor.distance) {
                    neighbor.distance = newDist;
                    neighbor.parent = current;
                    pq.add(neighbor);
                }
            }
        }
        return new ArrayList<>(); // Empty list if no path found
    }

    private static List<Node> getNeighbors(Node[][] grid, Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[] dx = {0, 0, 1, -1}; // Right, Left, Down, Up
        int[] dy = {1, -1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int newX = node.x + dx[i];
            int newY = node.y + dy[i];

            if (newX >= 0 && newX < grid.length && newY >= 0 && newY < grid[0].length) {
                neighbors.add(grid[newX][newY]);
            }
        }
        return neighbors;
    }

    private static List<Node> reconstructPath(Node end) {
        List<Node> path = new ArrayList<>();
        Node cur = end;
        while (cur != null) {
            path.add(cur);
            cur = cur.parent;
        }
        Collections.reverse(path);
        return path;
    }
}