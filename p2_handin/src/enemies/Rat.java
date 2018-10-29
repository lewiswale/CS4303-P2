package enemies;

import map.Block;
import player.Player;
import processing.core.PApplet;

public class Rat extends Enemy {
    public Rat(PApplet p, int gridX, int gridY, Player player, Block[][] grid, int sightRad) {
        super(p, gridX, gridY, player, grid, sightRad);
        this.name = "Hungry Rat";
        this.hp = 5;
        this.currentHP = hp;
        this.str = 2;
        this.dex = 2;
        this.expWorth = 100;
    }
}
