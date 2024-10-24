package ronik.ffacore;

public class FourCoordsBox {
    Coords one;
    Coords two;
    Coords three;
    Coords four;

    public FourCoordsBox(Coords one, Coords two, Coords three, Coords four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public Coords getOne() { return one; }
    public Coords getTwo() { return two; }
    public Coords getThree() { return three; }
    public Coords getFour() { return four; }

    public void setOne(Coords one) { this.one = one; }
    public void setTwo(Coords two) { this.two = two; }
    public void setThree(Coords three) { this.three = three; }
    public void setFour(Coords four) { this.four = four; }

    public boolean contains(Coords coords) {
        return coords.getX() >= one.getX() && coords.getX() <= three.getX() &&
               coords.getZ() >= one.getZ() && coords.getZ() <= three.getZ();
    }
}
