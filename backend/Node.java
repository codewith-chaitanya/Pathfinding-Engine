package backend;

public class Node implements Comparable<Node> {
    public int x, y;
    public int distance = Integer.MAX_VALUE;
    public Node parent = null;
    public boolean isWall = false;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance, other.distance);
    }
    
    @Override
    public String toString() {
        return "{\"x\": " + x + ", \"y\": " + y + "}";
    }
}