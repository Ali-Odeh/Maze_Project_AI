package org.example;
import java.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {

    public static List<Node> findPath(Tile[][] maze, int startX, int startY, int endX, int endY,
                                      Perceptron perceptron, List<String> testedPath) {
        int width = maze.length;
        int height = maze[0].length;
        boolean[][] closed = new boolean[width][height];
        PriorityQueue<Node> open = new PriorityQueue<>();

        open.add(new Node(startX, startY, 0, heuristic(startX, startY, endX, endY), null));

        while (!open.isEmpty()) {
            Node current = open.poll();
            testedPath.add("(" + current.x + ", " + current.y + ")");

            if (current.x == endX && current.y == endY) {
                List<Node> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path;
            }

            closed[current.x][current.y] = true;

            int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx >= 0 && ny >= 0 && nx < width && ny < height && !closed[nx][ny]) {
                    Tile neighbor = maze[nx][ny];
                    double[] features = {
                            neighbor.type == Tile.Type.GRASS ? 0 : 1,
                            neighbor.elevation,
                            neighbor.manhattanToObstacle
                    };
                    if (perceptron.predict(features) == 1) {
                        int g = current.g + 1;
                        int f = g + heuristic(nx, ny, endX, endY);
                        open.add(new Node(nx, ny, g, f, current));
                    }
                }
            }
        }

        return null;
    }

    private static int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

}
