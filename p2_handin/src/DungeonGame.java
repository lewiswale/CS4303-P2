import map.Block;
import map.Direction;
import map.LevelBuilder;
import processing.core.PApplet;

public class DungeonGame extends PApplet {
    private boolean[] keys = new boolean[128];
    private LevelBuilder levelBuilder;
    private boolean showInv, inCombat, playerCasting, win, lose;
    private int invSel1, invSel2, comSel1;

    public void settings() {
        size(1200, 800);
        levelBuilder = new LevelBuilder(this, 10, 10);
        levelBuilder.buildLevel();
    }

    public void draw() {
        if (!showInv) {
            if (!levelBuilder.isCombat()) {
                inCombat = false;
                win = levelBuilder.isWin();
                lose = levelBuilder.isLose();
                levelBuilder.drawLevel();
            } else {
                inCombat = true;
                if (!levelBuilder.isPlayerCasting()) {
                    levelBuilder.displayCombat(comSel1);
                } else {
                    playerCasting = true;
                    showInv = true;
                }
            }
        } else {
            levelBuilder.displayInv(invSel1, invSel2);
        }
    }

    public void keyPressed() {
        keys[key] = true;
        if (keys['r']) {
            if (showInv) {
                if (playerCasting) {
                    levelBuilder.stopCasting();
                    playerCasting = false;
                    showInv = false;
                } else if (levelBuilder.getInvSize(invSel1) > 0) {
                    levelBuilder.discard(invSel1, invSel2);
                    if (invSel2 > 0) {
                        invSel2--;
                    }
                }
            } else if (win || lose) {
                levelBuilder = new LevelBuilder(this, 10, 10);
                levelBuilder.buildLevel();
                win = false;
                lose = false;
            }
        } else if (keys['d']) {
            if (!showInv) {
                if (!inCombat) {
                    levelBuilder.checkMovement(Direction.RIGHT);
                } else {
                    if (comSel1 < 2) {
                        comSel1++;
                    }
                }
            } else {
                if (invSel1 < 2) {
                    invSel1++;
                    invSel2 = 0;
                }
            }
        } else if (keys['a']) {
            if (!showInv) {
                if (!inCombat) {
                    levelBuilder.checkMovement(Direction.LEFT);
                } else {
                    if (comSel1 > 0) {
                        comSel1--;
                    }
                }
            } else {
                if (invSel1 > 0) {
                    invSel1--;
                    invSel2 = 0;
                }
            }
        } else if (keys['w']) {
            if (!showInv) {
                levelBuilder.checkMovement(Direction.UP);
            } else {
                if (invSel2 > 0) {
                    invSel2--;
                }
            }
        } else if (keys['s']) {
            if (!showInv) {
                levelBuilder.checkMovement(Direction.DOWN);
            } else {
                if (invSel2 < levelBuilder.getInvSize(invSel1) - 1) {
                    invSel2++;
                }
            }
        } else if (keys['i']) {
            showInv = !showInv;
        } else if (keys['e']) {
            if (showInv) {
                if (playerCasting && invSel1 == 2) {
                    levelBuilder.equip(invSel1, invSel2);
                    if (invSel2 > 0) {
                        invSel2--;
                    }
                    playerCasting = false;
                    showInv = false;
                } else {
                    levelBuilder.equip(invSel1, invSel2);
                }
            } else {
                if (inCombat) {
                    levelBuilder.selectCombatOption(comSel1);
                }
            }
        }
    }

    public void keyReleased() {
        keys[key] = false;
    }

    public static void main(String[] args) {
        String[] pArgs = {"DungeonGame"};
        DungeonGame dg = new DungeonGame();
        PApplet.runSketch(pArgs, dg);
    }
}
