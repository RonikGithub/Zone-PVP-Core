package ronik.ffacore;

public class HitBox {
    private int x;
    private int y;
    private int width;
    private int height;

    public HitBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width  = width;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public boolean contains(Coords coords) {
        return coords.getX() >= x && coords.getX() <= x + width &&
               coords.getZ() >= y && coords.getZ() <= y + height;
    }
}
