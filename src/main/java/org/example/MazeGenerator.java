package org.example;
import java.util.Random;

public class MazeGenerator {

    public static Tile[][] generateMaze(int width, int height) {
        Tile[][] maze = new Tile[width][height];
        Random rand = new Random();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile.Type type;
                int roll = rand.nextInt(100);
                if (roll < 60) type = Tile.Type.GRASS;
                else if (roll < 80) type = Tile.Type.WATER;
                else type = Tile.Type.OBSTACLE;

                int elevation = rand.nextInt(11);
                maze[x][y] = new Tile(type, elevation, x, y);
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int minDist = Integer.MAX_VALUE;
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (maze[i][j].type == Tile.Type.OBSTACLE) {
                            int dist = Math.abs(x - i) + Math.abs(y - j);
                            if (dist < minDist) minDist = dist;
                        }
                    }
                }
                maze[x][y].manhattanToObstacle = minDist;
            }
        }

        return maze;
    }

    public static int[] getRandomValidPosition(Tile[][] maze, Random rand) {
        int width = maze.length;
        int height = maze[0].length;
        int x, y;
        do {
            x = rand.nextInt(width);
            y = rand.nextInt(height);
        } while (maze[x][y].type == Tile.Type.OBSTACLE);
        return new int[]{x, y};
    }

}
