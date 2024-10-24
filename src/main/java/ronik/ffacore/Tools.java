package ronik.ffacore;

import org.bukkit.Location;

public class Tools {
    public static double reMap(int input, double oldMin, double oldMax, double newMin, double newMax) {
        return (input - oldMin) * (newMax - newMin) / (oldMax - oldMin) + newMin;
    }

    public static Location coordsToLocation(Coords coords) {
        return new Location(null, coords.getX(), coords.getY(), coords.getZ());
    }

    public static Coords locationToCoords(Location location) {
        return new Coords(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static String whitespace(int length) {
        String whitespace = "";
        for (int i = 0; i < length; i++) {
            whitespace += " ";
        }
        return whitespace;
    }

    public static boolean coordsInPolygon(Coords coords, Coords[] polygon) {
        int i, j;
        boolean c = false;
        for (i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
            if (((polygon[i].getZ() > coords.getZ()) != (polygon[j].getZ() > coords.getZ())) &&
                (coords.getX() < (polygon[j].getX() - polygon[i].getX()) * (coords.getZ() - polygon[i].getZ()) / (polygon[j].getZ() - polygon[i].getZ()) + polygon[i].getX())) {
                c = !c;
            }
        }
        return c;
    }
}
