package map;

public class Block {
    private BlockType type;
    private int x, y, gridX, gridY;
    private int width;
    private boolean hasTreasure;
    private double distFromPlayer;

    public Block(BlockType type, int gridX, int gridY, int x, int y, int width) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.gridX = gridX;
        this.gridY = gridY;
        this.width = width;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public void setDistFromPlayer(double dist) {
        this.distFromPlayer = dist;
    }

    public double getDistFromPlayer() {
        return distFromPlayer;
    }

    public void moveX(int x) {
        this.x += x;
    }

    public void moveY(int y) {
        this.y += y;
    }

    public BlockType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public boolean getHasTreasure() {
        return hasTreasure;
    }

    public void setHasTreasure(boolean hasTreasure) {
        this.hasTreasure = hasTreasure;
    }
}
