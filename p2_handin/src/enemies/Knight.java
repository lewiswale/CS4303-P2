package enemies;

import map.Block;
import player.Player;
import processing.core.PApplet;

public class Knight extends Enemy {
    public Knight(PApplet p, int gridX, int gridY, Player player, Block[][] grid, int sightRad) {
        super(p, gridX, gridY, player, grid, sightRad);
        this.name = "Death Knight";
        this.hp = 20;
        this.currentHP = hp;
        this.str = 8;
        this.dex = 5;
        this.expWorth = 400;
    }
}
