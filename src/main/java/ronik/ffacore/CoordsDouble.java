package ronik.ffacore;

public class CoordsDouble {
    private double x;
    private double y;
    private double z;

    public CoordsDouble(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CoordsDouble(Coords coords) {
         this.x = coords.getX();
         this.y = coords.getY();
         this.z = coords.getZ();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    public boolean equals(CoordsDouble coords) {
        return coords.getX() == x && coords.getY() == y && coords.getZ() == z;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
