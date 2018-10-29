package combat;

import enemies.Enemy;
import player.Player;
import processing.core.PApplet;

public class CombatEngine {
    private PApplet p;
    private Player player;
    private Enemy enemy;
    private String eventMessage1, eventMessage2;
    private boolean playerCasting;

    public CombatEngine(PApplet p) {
        this.p = p;
        this.eventMessage1 = "";
        this.eventMessage2 = "";
        this.playerCasting = false;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public void playerAttack() {
        int dmg = player.dealDamage();
        eventMessage1 = enemy.takeDamage(dmg);
    }

    public void enemyAttack() {
        int dmg = enemy.dealDamage();
        eventMessage2 = player.takeDamage(dmg, enemy.getName());
    }

    public void playerDodge() {
        eventMessage1 = player.setDodgeModifier();
    }

    public void playerCast() {
        playerCasting = true;
    }

    public boolean isPlayerCasting() {
        return playerCasting;
    }

    public void makeSelection(int sel1) {
        switch (sel1) {
            case 0:
                playerAttack();
                break;
            case 1:
                playerDodge();
                break;
            case 2:
                playerCast();
                break;
        }
    }

    public void enemyTurn() {
        enemyAttack();
    }

    public void setEventMessage1(String em) {
        this.eventMessage1 = em;
    }

    public void setEventMessage2(String em) {
        this.eventMessage2 = em;
    }

    public void setPlayerCasting(boolean playerCasting) {
        this.playerCasting = playerCasting;
    }

    public void displayCombat(int sel1) {
        p.background(255);
        p.fill(0);
        p.textSize(30);
        p.text("Combat", 540, 40);
        p.textSize(30);
        p.text("Player", 300, 130);
        p.textSize(20);
        p.text("HP: " + player.getCurrentHP() + "/" + player.getHp(), 300, 160);
        p.text("Str: " + player.getStr() + " +" + player.getEquippedWeapon().getAtkModifier(), 300, 190);
        p.text("Dex: " + player.getDex(), 300, 220);
        p.text("Itl: " + player.getItl(), 300, 250);
        p.text("Armour: " + "+" + player.getEquippedArmour().getArmVal(), 300, 280);

        p.textSize(30);
        p.text(enemy.getName(), 800, 130);
        p.textSize(20);
        p.text("HP: " + enemy.getCurrentHP() + "/" + enemy.getHp(), 800, 160);

        p.textSize(30);
        p.text("Attack", 250, 600);
        p.text("Dodge", 550, 600);
        p.text("Cast", 850, 600);

        switch (sel1) {
            case 0:
                p.triangle(225, 600, 245, 585, 225, 570);
                break;
            case 1:
                p.triangle(525, 600, 545, 585, 525, 570);
                break;
            case 2:
                p.triangle(825, 600, 845, 585, 825, 570);
                break;
        }

        p.text(eventMessage1, 150, 700);
        p.text(eventMessage2, 150, 730);

        p.fill(255);
    }
}
