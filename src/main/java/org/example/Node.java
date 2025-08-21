package org.example;

public class Node implements Comparable<Node> {

    int x, y, g, f;
    Node parent;

    public Node(int x, int y, int g, int f, Node parent) {
        this.x = x;
        this.y = y;
        this.g = g;
        this.f = f;
        this.parent = parent;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.f, other.f);
    }

}
