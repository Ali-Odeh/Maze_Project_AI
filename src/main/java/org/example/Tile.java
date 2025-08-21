package org.example;

import java.util.*;

public class Tile {

    enum Type { GRASS, WATER, OBSTACLE }
    Type type;
    int elevation;
    int x, y;
    int manhattanToObstacle;

    public Tile(Type type, int elevation, int x, int y) {
        this.type = type;
        this.elevation = elevation;
        this.x = x;
        this.y = y;
        this.manhattanToObstacle = 0;
    }

}
