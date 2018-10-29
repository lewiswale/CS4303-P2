package player;

import enemies.Enemy;
import items.armour.*;
import items.magic.Blizzard;
import items.magic.Fireball;
import items.magic.Heal;
import items.magic.Spell;
import items.melee.*;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    private PApplet p;
    private PVector loc;
    private int width;
    private int hp, str, dex, itl, amr, lvl;
    private int currentHP;
    private int exp, expToLvl, totalExp;
    private Armour equippedArmour;
    private Weapon equippedWeapon;
    private ArrayList<Armour> armourInv;
    private ArrayList<Weapon> weaponInv;
    private ArrayList<Spell> spells;
    private double dodgeModifier;
    private Spell spellToCast;


    public Player(PApplet p, int width) {
        this.p = p;
        this.width = width;
        this.equippedArmour = new Shirt();
        this.equippedWeapon = new Fist();
        this.armourInv = new ArrayList<>();
        this.weaponInv = new ArrayList<>();
        this.spells = new ArrayList<>();

        this.hp = 10;
        this.currentHP = hp;
        this.str = 3;
        this.dex = 3;
        this.itl = 3;
        this.amr = equippedArmour.getArmVal();
        this.lvl = 1;
        this.exp = 0;
        this.expToLvl = 200;
    }

    public int getX() {
        return (int) loc.x;
    }

    public int getY() {
        return (int) loc.y;
    }

    public void setLoc(int x, int y) {
        this.loc = new PVector(x, y);
    }

    public PVector getLoc() {
        return loc;
    }

    public static boolean isBetween(int x, int l, int u) {
        return x >= l && x <= u;
    }

    public void addArmour() {
        Random r = new Random();
        Armour amr;
        int n = r.nextInt(100);

        if (isBetween(n, 95, 100) && lvl >= 4) {
            amr = new Dragonscale();
        } else if (isBetween(n, 80, 95) && lvl >= 3) {
            amr = new Steel();
        } else if (isBetween(n, 60, 80) && lvl >= 2) {
            amr = new Chainmail();
        } else if (isBetween(n,35, 60) && lvl >= 1) {
            amr = new Leather();
        } else {
            amr = new Shirt();
        }

        armourInv.add(amr);
    }

    public void addWeapon() {
        Random r = new Random();
        Weapon wpn;
        int n = r.nextInt(100);

        if (isBetween(n, 95, 100) && lvl >= 4) {
            wpn = new FireBlade();
        } else if (isBetween(n,80, 95) && lvl >= 3) {
            wpn = new LongSword();
        } else if (isBetween(n, 60, 80) && lvl >= 2) {
            wpn = new ShortSword();
        } else if (isBetween(n, 35, 60) && lvl >= 1) {
            wpn = new Dagger();
        } else {
            wpn = new Ladle();
        }

        weaponInv.add(wpn);
    }

    public void addSpell() {
        Random r = new Random();
        int spell = r.nextInt(3);
        if (spell == 0) {
            spells.add(new Fireball());
        } else if (spell == 1) {
            spells.add(new Blizzard());
        } else {
            spells.add(new Heal());
        }
    }

    public void printInv() {
        System.out.println("Inv:");
        for (int i = 0; i < armourInv.size(); i++) {
            System.out.println(armourInv.get(i).getName());
        }
        for (int i = 0; i < weaponInv.size(); i++) {
            System.out.println(weaponInv.get(i).getName());
        }
        for (int i = 0; i < spells.size(); i++) {
            System.out.println(spells.get(i).getName());
        }
    }

    public void equipArmour(int i) {
        equippedArmour = armourInv.get(i);
    }

    public void equipWeapon(int i) {
        equippedWeapon = weaponInv.get(i);
    }

    public void unequipArmour() {
        equippedArmour = new Shirt();
    }

    public void unequipWeapon() {
        equippedWeapon = new Fist();
    }

    public void discardArmour(int i) {
        armourInv.remove(i);
    }

    public void discardWeapon(int i) {
        weaponInv.remove(i);
    }

    public void discardSpell(int i) {
        spells.remove(i);
    }

    public ArrayList<Armour> getArmourInv() {
        return armourInv;
    }

    public ArrayList<Weapon> getWeaponInv() {
        return weaponInv;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void gainExp(int expGained) {
        exp += expGained;
        totalExp += expGained;

        if (exp >= expToLvl) {
            exp -= expToLvl;
            expToLvl *= 2;
            levelUp();
        }
    }

    public void levelUp(){
        lvl++;
        hp += 3;
        currentHP = hp;
        str += 2;
        dex += 2;
        itl += 2;
    }

    public int getHp() {
        return hp;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getStr() {
        return str;
    }

    public int getDex() {
        return dex;
    }

    public int getAmr() {
        return amr;
    }

    public int getItl() {
        return itl;
    }

    public int getLvl() {
        return lvl;
    }

    public int getExp() {
        return exp;
    }

    public int getExpToLvl() {
        return expToLvl;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public Armour getEquippedArmour() {
        return equippedArmour;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int dealDamage() {
        Random r = new Random();
        int dmg = r.nextInt(str) + 1;
        dmg += equippedWeapon.getAtkModifier();
        return dmg;
    }

    public String takeDamage(int dmg, String enemyName) {
        Random r = new Random();
        int dexRoll = r.nextInt(dex) + 1;

        if (dexRoll*dodgeModifier <= dmg) {
            currentHP -= dmg - amr;
            dodgeModifier = 1;
            return enemyName + " dealt " + dmg + " damage to You! Your armour blocked "
                    + equippedArmour.getArmVal() + "!";
        } else {
            dodgeModifier = 1;
            return "You dodged an attack from " + enemyName;
        }
    }

    public String setDodgeModifier() {
        Random r = new Random();
        int toModifiy = r.nextInt(10) + 1;
        dodgeModifier = 1 + toModifiy/10.0;
        return "Your dexterity is multiplied by " + dodgeModifier + " this turn!";
    }

    public String castSpell(Enemy enemy, int selectedSpell) {
        spellToCast = spells.get(selectedSpell);
        Random r = new Random();
        int itlRoll = r.nextInt(itl) + 1;

        if (spellToCast instanceof Heal) {
            int toHeal = spellToCast.getDmg() + itlRoll;
            currentHP += toHeal;
            if (currentHP > hp) {
                currentHP = hp;
            }
            discardSpell(selectedSpell);
            return "You healed yourself for " + toHeal + " HP!";
        }

        if (spellToCast instanceof Fireball || spellToCast instanceof Blizzard) {
            discardSpell(selectedSpell);
            return enemy.takeDamage(spellToCast.getDmg() + itlRoll);
        }

        return "";
    }

    public void drawPlayer() {
        p.fill(0);
        p.rect(loc.x, loc.y, width, width);
        p.fill(255);
    }
}
