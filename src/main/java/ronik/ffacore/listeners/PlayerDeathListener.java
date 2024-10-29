package ronik.ffacore.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ronik.ffacore.Coords;
import ronik.ffacore.database.DatabaseHandler;
import ronik.ffacore.Zone;
import ronik.ffacore.GameMap;

import static org.bukkit.Bukkit.getLogger;

import java.util.Objects;

public class PlayerDeathListener implements Listener {
    private final GameMap map;

    public PlayerDeathListener(GameMap map) {
        this.map = map;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // if player was killed by another player
        String playerUUID = event.getEntity().getUniqueId().toString();
        DatabaseHandler.addDeath(playerUUID);
        if (event.getEntity().getKiller() != null) {
            String killerUUID = Objects.requireNonNull(event.getEntity().getKiller()).getUniqueId().toString();
            DatabaseHandler.addKill(killerUUID);
        }
        getLogger().info("ZonePVP plugin has been enabled! Woohoo");

        // getting the location of the player's death
        Location deathLocation = event.getEntity().getLocation();
        Coords deathCoords = new Coords(deathLocation.getBlockX(), deathLocation.getBlockY(), deathLocation.getBlockZ());
        Zone deathZone = map.getZone(deathCoords);
        // if the player died in a zone
        if (deathZone != null) {
            if (deathZone.isCoordsInKoth(deathCoords)) {
                deathZone.onPlayerNotInKoth(event.getEntity());
            }
        }

        // debug message
        assert deathZone != null;
        event.getEntity().sendMessage("You died in zone: " + deathZone.getName() + " Remember class change Ronik");
    }
}