package map;

import processing.core.PVector;

import java.util.Random;

public class Walker {
    private PVector loc;
    private PVector dir;
    private int levelWidth;
    private int levelHeight;
    private Random r = new Random();
    private final int SCALE;

    public Walker(int levelWidth, int levelHeight, int scale, int x, int y) {
        this.SCALE = scale;
        this.levelWidth = levelWidth*SCALE;
        this.levelHeight = levelHeight*SCALE;
        loc = new PVector(x, y);
    }

    public void walk() {
        boolean valid = false;

        while (!valid) {
            int newDir = r.nextInt(4);
            switch (newDir) {
                case 0:
                    if (loc.y > SCALE) {
                        dir = new PVector(0, -SCALE);
                        valid = true;
                    }
                    break;
                case 1:
                    if (loc.x < levelWidth - SCALE*2) {
                        dir = new PVector(SCALE, 0);
                        valid = true;
                    }
                    break;
                case 2:
                    if (loc.y < levelHeight - SCALE*2) {
                        dir = new PVector(0, SCALE);
                        valid = true;
                    }
                    break;
                case 3:
                    if (loc.x > SCALE) {
                        dir = new PVector(-SCALE, 0);
                        valid = true;
                    }
                    break;
            }
        }

        loc.add(dir);
    }

    public int getCurrentX() {
        return (int) loc.x;
    }

    public int getCurrentY() {
        return (int) loc.y;
    }
}
