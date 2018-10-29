package map;

import combat.CombatEngine;
import enemies.*;
import items.armour.Armour;
import items.magic.Spell;
import items.melee.Weapon;
import player.Player;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

public class LevelBuilder {
    private PApplet p;
    private int width, height;
    private Block grid[][];
    private Walker w;
    private int scale;
    private Block start, finish;
    private Player player;
    private int treasureCount;
    private int amrEqX, amrEqY, wpnEqX, wpnEqY;
    private int amrEquipped = -1;
    private int wpnEquipped = -1;
    private int floorNo;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private boolean combat;
    private Enemy enemyToFight;
    private CombatEngine ce;
    private boolean playerCasting;
    private String eventMessage = "";
    private int timer = 0;
    private boolean win, lose;

    public LevelBuilder(PApplet p, int x, int y) {
        this.p = p;
        this.width = x;
        this.height = y;
        this.scale = 50;
        this.player = new Player(p, scale);
        this.floorNo = 1;
        this.ce = new CombatEngine(p);
        this.win = false;
        this.lose = false;
    }

    public void buildLevel() {
        grid = new Block[width][height];
        enemies = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Block(BlockType.WALL, i, j,i* scale, j* scale, scale);
            }
        }

        Random r = new Random();
        int walkerX = (r.nextInt(width-2) + 1)* scale;
        int walkerY = (r.nextInt(height-2) + 1)* scale;
        w = new Walker(width, height, scale, walkerX, walkerY);

        boolean enough = false;
        while (!enough) {
            int floors = countFloors();
            double proportion = (double)floors/(width*height);

            if (proportion < 0.5)
                moveWalker();
            else enough = true;
        }

        start = setStart();

        finish = setFinish();

        centreStart();

        player.setLoc(start.getX(), start.getY());

        treasureCount = 0;

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if (grid[i][j].getType() == BlockType.FLOOR) {
                    int neighbours = countFloorNeighbours(i, j);
                    if (neighbours == 1) {
                        int chanceForTreasure = r.nextInt(5);
                        if (chanceForTreasure == 4) {
                            grid[i][j].setHasTreasure(true);
                            treasureCount++;
                        }
                    }
                }
            }
        }

        while (treasureCount < 2) {
            int rx = r.nextInt(width);
            int ry = r.nextInt(height);

            if (grid[rx][ry].getType() == BlockType.FLOOR && !grid[rx][ry].getHasTreasure()) {
                grid[rx][ry].setHasTreasure(true);
                treasureCount++;
            }
        }

        while (enemies.size() < floorNo - 1) {
            int rx = r.nextInt(width);
            int ry = r.nextInt(height);

            Block currentBlock = grid[rx][ry];

            if (currentBlock.getType() == BlockType.FLOOR) {
                if (countFloorNeighbours(rx, ry) == 8) {
                    switch (floorNo) {
                        case 2:
                            enemies.add(new Rat(p, rx, ry, player, grid, floorNo));
                            break;
                        case 3:
                            if (enemies.size() == 0) {
                                enemies.add(new Rat(p, rx, ry, player, grid, floorNo));
                            } else {
                                enemies.add(new Goblin(p, rx, ry, player, grid, floorNo));
                            }
                            break;
                        case 4:
                            if (enemies.size() <= 1) {
                                enemies.add(new Goblin(p, rx, ry, player, grid, floorNo));
                            } else {
                                enemies.add(new Knight(p, rx, ry, player, grid, floorNo));
                            }
                            break;
                        case 5:
                            if (enemies.size() <= 2) {
                                enemies.add(new Knight(p, rx, ry, player, grid, floorNo));
                            } else {
                                enemies.add(new Dragon(p, rx, ry, player, grid, floorNo));
                            }
                    }
                }
            }
        }
    }

    public int countFloors() {
        int count = 0;
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if (grid[i][j].getType() == BlockType.FLOOR) {
                    count++;
                }
            }
        }
        return count;
    }

    public void moveWalker() {
        int x = w.getCurrentX()/ scale;
        int y = w.getCurrentY()/ scale;
        grid[x][y].setType(BlockType.FLOOR);
        w.walk();
    }

    public void moveMap(Direction direction) {
        switch (direction) {
            case RIGHT:
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        grid[i][j].moveX(scale *-1);
                    }
                }

                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).moveXWithMap(scale*-1);
                }
                break;
            case LEFT:
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        grid[i][j].moveX(scale);
                    }
                }

                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).moveXWithMap(scale);
                }
                break;
            case UP:
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        grid[i][j].moveY(scale);
                    }
                }
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).moveYWithMap(scale);
                }
                break;
            case DOWN:
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        grid[i][j].moveY(scale *-1);
                    }
                }
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).moveYWithMap(scale*-1);
                }
                break;
        }
    }

    public void checkMovement(Direction dir) {
        int playerCurrentX = getPlayerGridX();
        int playerCurrentY = getPlayerGridY();
        //Checking movement to make is valid
        switch (dir) {
            case RIGHT:
                if (grid[playerCurrentX+1][playerCurrentY].getType() != BlockType.WALL) {
                    moveMap(Direction.RIGHT);
                }
                break;
            case DOWN:
                if (grid[playerCurrentX][playerCurrentY+1].getType() != BlockType.WALL) {
                    moveMap(Direction.DOWN);
                }
                break;
            case UP:
                if (grid[playerCurrentX][playerCurrentY-1].getType() != BlockType.WALL) {
                    moveMap(Direction.UP);
                }
                break;
            case LEFT:
                if (grid[playerCurrentX-1][playerCurrentY].getType() != BlockType.WALL) {
                    moveMap(Direction.LEFT);
                }
                break;
        }
        //Checks made after making move
        Block currentBlock = grid[getPlayerGridX()][getPlayerGridY()];
        Random r = new Random();

        if (currentBlock.getHasTreasure()) {
            int treasureChance = r.nextInt(4);
            if (treasureChance == 1) {
                player.addArmour();
                player.gainExp(100);
                eventMessage = "You picked up some armour! Gain 100xp!";
            } else if (treasureChance == 2) {
                player.addWeapon();
                player.gainExp(100);
                eventMessage = "You picked up a weapon! Gain 100xp!";
            } else if (treasureChance == 3){
                player.addSpell();
                player.gainExp(100);
                eventMessage = "You picked up a spell! Gain 100xp!";
            } else {
                System.out.println("Nothing found. Unlucky m8.");
                eventMessage = "Nothing but dust...";
            }
            currentBlock.setHasTreasure(false);
            player.printInv();
        }

        if (currentBlock.getType() == BlockType.FINISH) {
            player.gainExp(200);
            eventMessage = "You found the floor's exit! Gain 200xp!";
            floorNo++;
            if (floorNo == 6) {
                win = true;
            } else {
                setSize(width + 10);
                buildLevel();
            }
        }
    }

    public boolean isWin() {
        return win;
    }

    public int getPlayerGridX() {
        for (int i = 0; i < width; i++) {
            if (grid[i][0].getX() == player.getX()) {
                return i;
            }
        }
        return 0;
    }

    public int getPlayerGridY() {
        for (int i = 0; i < height; i++) {
            if (grid[0][i].getY() == player.getY()) {
                return i;
            }
        }
        return 0;
    }

    public Block setStart() {
        int x = 0;
        int y = 0;

        while (true) {
            for (int i = 0; i <= x; i++) {
                if (grid[x][y].getType() == BlockType.FLOOR) {
                    grid[x][y].setType(BlockType.START);
                    return grid[x][y];
                }
                else if (y != 0){
                    x++;
                    y--;
                }
            }
            y = x+1;
            x = 0;
        }
    }

    public void centreStart() {
        while (start.getX() != 600) {
            if (start.getX() < 600) {
                moveMap(Direction.LEFT);
            } else if (start.getX() > 600) {
                moveMap(Direction.RIGHT);
            }
        }

        while (start.getY() != 400) {
            if (start.getY() < 400) {
                moveMap(Direction.UP);
            } else if (start.getY() > 400) {
                moveMap(Direction.DOWN);
            }
        }
    }

    public Block setFinish() {
        int x = 0;
        int y = 0;

        while (true) {
            for (int i = 0; i <= x; i++) {
                Block space = grid[width - 1 - x][height - 1 - y];
                if (space.getType() == BlockType.FLOOR) {
                    space.setType(BlockType.FINISH);
                    return space;
                }
                else if (y != 0){
                    x++;
                    y--;
                }
            }
            y = x+1;
            x = 0;
        }
    }

    public int countFloorNeighbours(int x, int y) {
        int count = 0;

        if (grid[x+1][y+1].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x+1][y].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x+1][y-1].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x][y+1].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x][y-1].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x-1][y+1].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x-1][y].getType() == BlockType.FLOOR) {
            count++;
        }

        if (grid[x-1][y-1].getType() == BlockType.FLOOR) {
            count++;
        }

        return count;
    }

    public void setSize(int n) {
        this.width = n;
        this.height = n;
    }

    public void drawLevel() {
        p.noStroke();
        p.background(128);
        if (!win && !lose) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int playerDiffX = player.getX() - grid[i][j].getX();
                    int playerDiffY = player.getY() - grid[i][j].getY();
                    grid[i][j].setDistFromPlayer(Math.sqrt((playerDiffX * playerDiffX) + (playerDiffY * playerDiffY)));
                    switch (grid[i][j].getType()) {
                        case WALL:
                            p.fill(30);
                            p.rect(grid[i][j].getX(), grid[i][j].getY(), scale, scale);
                            p.fill(255);
                            break;
                        case FLOOR:
                            if (grid[i][j].getHasTreasure()) {
                                p.fill(255, 255, 0);
                                p.rect(grid[i][j].getX(), grid[i][j].getY(), scale, scale);
                                p.fill(255);
                            } else {
                                p.fill(100);
                                p.rect(grid[i][j].getX(), grid[i][j].getY(), scale, scale);
                                p.fill(255);
                            }
                            break;
                        case START:
                            p.fill(0, 255, 0);
                            p.rect(grid[i][j].getX(), grid[i][j].getY(), scale, scale);
                            p.fill(255);
                            break;
                        case FINISH:
                            p.fill(255, 0, 0);
                            p.rect(grid[i][j].getX(), grid[i][j].getY(), scale, scale);
                            p.fill(255);
                            break;
                    }
                }
            }
            player.drawPlayer();

            if (enemies.size() > 0) {
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy e = enemies.get(i);
                    e.drawEnemy();

                    if (e.getX() > player.getX() && e.getX() < player.getX() + scale
                            && e.getY() > player.getY() && e.getY() < player.getY() + scale) {
                        combat = true;
                        enemyToFight = e;
                    }
                }
            }

            if (timer < 300 && !eventMessage.equals("")) {
                p.textSize(15);
                p.text(eventMessage, 10, 790);
                timer++;
            } else {
                eventMessage = "";
                timer = 0;
            }
        } else {
            p.textSize(50);
            if (win) {
                p.fill(0, 100, 0);
                p.text("CONGRATULATIONS!", 375, 300);
            } else if (lose) {
                p.fill(128,0,0);
                p.text("YOU DIED", 525, 300);
            }
            p.fill(255);
            p.textSize(30);
            p.text("Your final score was: " + player.getTotalExp() + "!", 450, 380);
            p.text("Press [r] to play again!", 480, 410);
        }
    }

    public boolean isCombat() {
        return combat;
    }

    public void displayInv(int invSel1, int invSel2) {
        p.background(0,0,139);
        p.fill(255);
        p.textSize(30);
        p.text("Inventory", 10, 40);
        p.textSize(20);
        p.text("Armour", 20, 80);
        p.textSize(15);

        ArrayList<Armour> armInv = player.getArmourInv();
        int x = 20;
        int y = 110;

        for (int i = 0; i < armInv.size(); i++) {
            p.text(armInv.get(i).getName(), x, y);
            p.text("amr +" + armInv.get(i).getArmVal(), x+200, y);
            y += 20;
        }

        p.textSize(20);
        p.text("Weapons", 400, 80);
        p.textSize(15);

        ArrayList<Weapon> wpnInv = player.getWeaponInv();
        x = 400;
        y = 110;

        for (int i = 0; i < wpnInv.size(); i++) {
            p.text(wpnInv.get(i).getName(), x, y);
            p.text("atk +" + wpnInv.get(i).getAtkModifier(), x+200, y);
            y += 20;
        }

        p.textSize(20);
        p.text("Spells", 800, 80);
        p.textSize(15);

        ArrayList<Spell> splInv = player.getSpells();
        x = 800;
        y = 110;

        for (int i = 0; i < splInv.size(); i++) {
            p.text(splInv.get(i).getName(), x, y);
            p.text("mgc: " + splInv.get(i).getDmg(), x+200, y);
            y += 20;
        }

        int selectionY = 110 + (20*invSel2);

        switch (invSel1) {
            case 0:
                p.triangle(5, 80, 15, 70, 5, 60);
                if (armInv.size() > 0) {
                    p.triangle(5, selectionY-2, 15, selectionY - 8, 5, selectionY - 14);
                }
                break;
            case 1:
                p.triangle(385, 80, 395, 70, 385, 60);
                if (wpnInv.size() > 0) {
                    p.triangle(385, selectionY-2, 395, selectionY - 8, 385, selectionY - 14);
                }
                break;
            case 2:
                p.triangle(785, 80, 795, 70, 785, 60);
                if (splInv.size() > 0) {
                    p.triangle(785, selectionY-2, 795, selectionY - 8, 785, selectionY - 14);
                }
        }

        if (amrEquipped >= 0) {
            p.stroke(0,255,0);
            p.noFill();
            p.strokeWeight(3);
            p.rect(amrEqX, amrEqY, 350, 22);
            p.noStroke();
            p.fill(255);
        }

        if (wpnEquipped >= 0) {
            p.stroke(0,255,0);
            p.noFill();
            p.strokeWeight(3);
            p.rect(wpnEqX, wpnEqY, 350, 22);
            p.noStroke();
            p.fill(255);
        }

        p.textSize(30);
        p.text("Player", 10, 450);
        p.textSize(15);

        p.text("HP: " + player.getCurrentHP() + "/" + player.getHp(), 10, 480);
        p.text("Level: " + player.getLvl(), 10, 500);
        p.text("Exp: " + player.getExp() + "/" + player.getExpToLvl(), 10, 520);
        p.text("Str: " + player.getStr(), 10, 540);
        p.text("Dex: " + player.getDex(), 10, 560);
        p.text("Itl: " + player.getItl(), 10, 580);
        p.text("Armour: " + player.getEquippedArmour().getName() + " +" + player.getEquippedArmour().getArmVal(), 10, 600);
        p.text("Weapon: " + player.getEquippedWeapon().getName() + " +" + player.getEquippedWeapon().getAtkModifier(), 10, 620);
    }

    public int getInvSize(int inv) {
        switch (inv) {
            case 0: return player.getArmourInv().size();
            case 1: return player.getWeaponInv().size();
            case 2: return player.getSpells().size();
            default: return 0;
        }
    }

    public void equip(int invSel1, int invSel2) {
        int selectionY = 110 + (20*invSel2);

        switch (invSel1) {
            case 0:
                if (getInvSize(0) > 0) {
                    player.equipArmour(invSel2);
                    amrEquipped = invSel2;
                    amrEqX = 0;
                    amrEqY = selectionY-18;
                }
                break;
            case 1:
                if (getInvSize(1) > 0) {
                    player.equipWeapon(invSel2);
                    wpnEquipped = invSel2;
                    wpnEqX = 380;
                    wpnEqY = selectionY - 18;
                }
                break;
            case 2:
                if (playerCasting) {
                    ce.setEventMessage1(player.castSpell(enemyToFight, invSel2));
                    ce.setPlayerCasting(false);
                    playerCasting = false;
                }
        }
    }

    public void stopCasting() {
        ce.setPlayerCasting(false);
        playerCasting = false;
    }

    public void discard(int invSel1, int invSel2) {
        int selectionY = 110 + (20*invSel2);
        switch (invSel1) {
            case 0:
                if (amrEquipped == invSel2) {
                    amrEquipped = -1;
                    player.unequipArmour();
                }
                player.discardArmour(invSel2);
                if (amrEquipped > invSel2) {
                    amrEquipped--;
                    amrEqY = selectionY-18;
                }
                break;
            case 1:
                if (wpnEquipped == invSel2) {
                    wpnEquipped = -1;
                    player.unequipWeapon();
                }
                player.discardWeapon(invSel2);
                if (wpnEquipped > invSel2) {
                    wpnEquipped--;
                    wpnEqY = selectionY-18;
                }
                break;
            case 2:
                player.discardSpell(invSel2);
                break;
        }
    }

    public void displayCombat(int sel1) {
        ce.setPlayer(player);
        ce.setEnemy(enemyToFight);
        if (enemyToFight.getCurrentHP() <= 0) {
            ce.setEventMessage1("");
            ce.setEventMessage2("");
            eventMessage = "You beat a " + enemyToFight.getName() + "! Gain " + enemyToFight.getExpWorth() + "xp!";
            player.gainExp(enemyToFight.getExpWorth());
            enemies.remove(enemyToFight);
            combat = false;
        }
        ce.displayCombat(sel1);
    }

    public void selectCombatOption(int sel1) {
        ce.makeSelection(sel1);
        if (ce.isPlayerCasting()) {
            playerCasting = true;
        } else {
            if (enemyToFight.getCurrentHP() > 0) {
                ce.enemyTurn();
                if (player.getCurrentHP() <= 0) {
                    lose = true;
                    combat = false;
                }
            }
        }
    }

    public boolean isLose() {
        return lose;
    }

    public boolean isPlayerCasting() {
        return playerCasting;
    }
}
