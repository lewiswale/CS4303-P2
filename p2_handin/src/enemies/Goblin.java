package enemies;

import map.Block;
import player.Player;
import processing.core.PApplet;

public class Goblin extends Enemy {
    public Goblin(PApplet p, int gridX, int gridY, Player player, Block[][] grid, int sightRad) {
        super(p, gridX, gridY, player, grid, sightRad);
        this.name = "Goblin";
        this.hp = 10;
        this.currentHP = hp;
        this.str = 5;
        this.dex = 4;
        this.expWorth = 200;
    }
}
