package ronik.ffacore;

import java.util.ArrayList;

public class KothBox {
    private final Coords corner1;
    private final Coords corner2;
    private final Coords corner3;
    private final Coords corner4;
    private final ArrayList<Coords> corners;


    public KothBox(Coords corner1, Coords corner3) {
        this.corner1 = corner1;
        this.corner3 = corner3;
        this.corner2 = new Coords(corner3.getX(), 0, corner1.getZ());
        this.corner4 = new Coords(corner1.getX(), 0, corner3.getZ());
        this.corners = new ArrayList<>();
        corners.add(corner1);
        corners.add(corner2);
        corners.add(corner3);
        corners.add(corner4);

    }

    public Coords getCorner1() { return corner1; }
    public Coords getCorner2() { return corner2; }
    public Coords getCorner3() { return corner3; }
    public Coords getCorner4() { return corner4; }

    public boolean contains(Coords playerCoords) {
        int i, j;
        boolean c = false;
        for (i = 0, j = corners.size() - 1; i < corners.size(); j = i++) {
            if (((corners.get(i).getZ() > playerCoords.getZ()) != (corners.get(j).getZ() > playerCoords.getZ())) &&
                    (playerCoords.getX() < (corners.get(j).getX() - corners.get(i).getX()) * (playerCoords.getZ() - corners.get(i).getZ()) / (corners.get(j).getZ() - corners.get(i).getZ()) + corners.get(i).getX())) {
                c = !c;
            }
        }
        return c;
    }

    public String toString() {
        return "Corner 1: " + corner1.toString() + ", Corner 2: " + corner2.toString();
    }
}
