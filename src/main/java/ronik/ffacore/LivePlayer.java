package ronik.ffacore;

import org.bukkit.entity.Player;

public class LivePlayer {
    private Player player;
    private String currentZoneName;

    public LivePlayer(Player player) {
        this.player = player;
        this.currentZoneName = null;
    }

    public String getCurrentZoneName() { return currentZoneName; }

    public void setCurrentZone(String currentZone) { this.currentZoneName = currentZone; }

    public Player getPlayer() { return player; }
}
