package enemies;

import map.Block;
import map.BlockType;
import player.Player;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    public String name;
    public int hp, str, dex, currentHP, expWorth;
    private PVector loc, startingLoc;
    private PVector dir, dirNorm;
    private int width;
    private PApplet p;
    private int movementPath, xMoved, yMoved;
    private boolean movingRight, movingUp;
    private Player player;
    private Block[][] grid;
    private double xDiff, yDiff;
    private int sightRad;
    private Block toMoveTo;
    private boolean moving;
    private int gridX, gridY, newGridX, newGridY;
    private boolean playerSeen = false;

    public Enemy(PApplet p, int gridX, int gridY, Player player, Block[][] grid, int sightRad) {
        this.p = p;
        this.gridX = gridX;
        this.gridY = gridY;
        this.grid = grid;
        Block block = grid[gridX][gridY];
        setLoc(block.getX(), block.getY());
        this.startingLoc = new PVector(block.getX(), block.getY());
        this.width = 50;
        Random r = new Random();
        this.movementPath = r.nextInt(3);
        this.movingRight = true;
        this.movingUp = true;
        this.player = player;
        this.sightRad = sightRad+1;
    }

    public void setLoc(int x, int y) {
        loc = new PVector(x, y);
    }

    public void moveXWithMap(int x) {
        PVector toAdd = new PVector(x, 0);
        loc.add(toAdd);
        startingLoc.add(toAdd);
    }

    public void moveX(int x) {
        loc.add(new PVector(x, 0));
    }

    public void moveYWithMap(int y) {
        PVector toAdd = new PVector(0, y);
        loc.add(toAdd);
        startingLoc.add(toAdd);
    }

    public void moveY(int y) {
        loc.add(new PVector(0, y));
    }

    public void moveEnemy() {
        int speed = 5;
        if (calcDist(player.getX(), player.getY(), loc.x, loc.y) < width * sightRad && !playerSeen) {
            setLoc((int) startingLoc.x, (int) startingLoc.y);
            playerSeen = true;
        }

        if (playerSeen) {
            if (!moving) {
                toMoveTo = findBlockToMoveTo();
                dir = new PVector(loc.x - toMoveTo.getX(), loc.y - toMoveTo.getY());
                dirNorm = dir.normalize();
                moving = true;
            } else {
                if (loc.x == toMoveTo.getX() && loc.y == toMoveTo.getY()) {
                    moving = false;
                    gridX = newGridX;
                    gridY = newGridY;
                } else {
                    if (dirNorm.x < 0) {
                        moveX(speed);
                    } else if (dirNorm.x > 0) {
                        moveX(speed*-1);
                    }

                    if (dirNorm.y < 0) {
                        moveY(speed);
                    } else if (dirNorm.y > 0) {
                        moveY(speed*-1);
                    }
                }
            }
        } else {
            switch (movementPath) {
                case 0:
                    if (movingRight) {
                        if (xMoved < width) {
                            moveX(1);
                            xMoved++;
                        } else {
                            movingRight = false;
                        }
                    } else {
                        if (xMoved > -width) {
                            moveX(-1);
                            xMoved--;
                        } else {
                            movingRight = true;
                        }
                    }
                    break;
                case 1:
                    if (movingUp) {
                        if (yMoved < width) {
                            moveY(-1);
                            yMoved++;
                        } else {
                            movingUp = false;
                        }
                    } else {
                        if (yMoved > -width) {
                            moveY(1);
                            yMoved--;
                        } else {
                            movingUp = true;
                        }
                    }
                    break;
            }
        }
    }

    public Block findBlockToMoveTo() {
        ArrayList<Block> neighbours = new ArrayList<>();
        neighbours.add(grid[gridX][gridY - 1]);
        neighbours.add(grid[gridX + 1][gridY]);
        neighbours.add(grid[gridX - 1][gridY]);
        neighbours.add(grid[gridX][gridY + 1]);
        double dist = 9999999;
        Block toGo = null;

        for (int i = 0; i < neighbours.size(); i++) {
            Block check = neighbours.get(i);
            if (check.getType() == BlockType.FLOOR && check.getDistFromPlayer() < dist) {
                toGo = check;
                dist = check.getDistFromPlayer();
                newGridX = check.getGridX();
                newGridY = check.getGridY();
            }
        }

        return toGo;
    }

    public double calcDist(double x1, double y1, double x2, double y2) {
        xDiff = x1 - x2;
        yDiff = y1 - y2;
        return Math.sqrt((xDiff*xDiff) + (yDiff*yDiff));
    }

    public double getX() {
        return loc.x + width/2;
    }

    public double getY() {
        return loc.y + width/2;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getCurrentHP() {
        return currentHP;
    }

     public int getExpWorth() {
        return expWorth;
     }

    public String takeDamage(int dmg) {
        Random r = new Random();
        int dexRoll = r.nextInt(dex) + 1;
        if (!(dexRoll > dmg)) {
            currentHP -= dmg;
            return "You dealt " + dmg + " damage to " + name + "!";
        } else {
            return "Oh no! " + name + " dodged your attack!";
        }
    }

    public int dealDamage() {
        Random r = new Random();
        int dmg = r.nextInt(str) + 1;
        return dmg;
    }

    public void drawEnemy() {
        p.fill(75, 0, 130);
        p.ellipse(loc.x + width/2, loc.y + width/2, width, width);
        p.fill(255);
        moveEnemy();
    }
}
