package ronik.ffacore;

import org.bukkit.Location;

public class GameMap {

    private Zone[] zones;
    public GameMap(Zone[] zones) {
        this.zones = zones;
    }
    public Zone[] getZones() { return zones; }
    public void setZones(Zone[] zones) { this.zones = zones; }

    public String getZoneName(Coords playerCoords) {

        for (Zone zone : zones) {
            if (zone.isPlayerInZone(playerCoords)) {
                return zone.getName();
            }
        }
        return "No zone found";
    }

    public Zone getZone(Coords playerCoords) {
        for (Zone zone : zones) {
            if (zone.isPlayerInZone(playerCoords)) {
                return zone;
            }
        }
        return null;
    }

    public Zone getZoneFromZoneName(String zoneName) {
        for (Zone zone : zones) {
            if (zone.getName().equals(zoneName)) {
                return zone;
            }
        }
        return null;
    }
}
