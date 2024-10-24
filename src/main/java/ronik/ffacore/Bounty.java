package ronik.ffacore;

import org.bukkit.boss.BossBar;

import java.util.ArrayList;

public class Bounty {
    private Coords pickUpCoords;
    private ArrayList<String> hunters;
    private ArrayList<String> victims;
    private int reward;
    private int status;
    private int timeLeft;
    private BossBar hunterBossBar;
    private BossBar victimBossBar;

    public Bounty(Coords pickUpCoords, int reward, ArrayList<String> hunters, ArrayList<String> victims) {
        this.pickUpCoords = pickUpCoords;
        this.hunters = hunters;
        this.victims = victims;
        this.reward = reward;
        this.status = 0;
        this.timeLeft = 360;
        this.hunterBossBar = null;
        this.victimBossBar = null;
    }

    // Next thing to work on

}
