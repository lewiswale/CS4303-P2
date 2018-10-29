package enemies;

import map.Block;
import player.Player;
import processing.core.PApplet;

public class Dragon extends Enemy {
    public Dragon(PApplet p, int gridX, int gridY, Player player, Block[][] grid, int sightRad) {
        super(p, gridX, gridY, player, grid, sightRad);
        this.name = "Dragon";
        this.hp = 50;
        this.currentHP = hp;
        this.str = 15;
        this.dex = 15;
        this.expWorth = 1000;
    }
}
