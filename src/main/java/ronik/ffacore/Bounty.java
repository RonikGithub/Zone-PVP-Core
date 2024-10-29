package ronik.ffacore;

import org.bukkit.boss.BossBar;
import java.util.ArrayList;

public class Bounty {
    private Coords pickUpCoords;
    private ArrayList<String> hunters; //Array of Hunter UUID's
    private ArrayList<String> targets; //Array of Hunter UUID's
    private int reward;
    private int status;
    private int timeLeft;
    private BossBar hunterBossBar;
    private BossBar victimBossBar;

    public Bounty(Coords pickUpCoords, int reward, ArrayList<String> hunters, ArrayList<String> targets) {
        this.pickUpCoords = pickUpCoords;
        this.hunters = hunters;
        this.targets = targets;
        this.reward = reward;
        this.status = 0;
        this.timeLeft = 360;
        this.hunterBossBar = null;
        this.victimBossBar = null;
    }

    public void setHunters(String[] playerUUIDs) {
        for (String playerUUID : playerUUIDs) {
            hunters.add(playerUUID);
        }
    }

    public void setTargets(String[] playerUUIDs) {
        for (String playerUUID : playerUUIDs) {
            targets.add(playerUUID);
        }
    }

    public int updateTimeRemaining() {
        timeLeft--;
        return timeLeft;
    }
}
