package PlanetaryOrbitSimulation;

public class VectorP {

    double x, y;

    public VectorP(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public VectorP add(VectorP v) {
        return new VectorP(x + v.x, y + v.y);
    }

    public VectorP subtract(VectorP v) {
        return new VectorP(x - v.x, y - v.y);
    }

    public VectorP scale(double scalar) {
        return new VectorP(x * scalar, y * scalar);
    }

    public double dot(VectorP v) {
        return x * v.x + y * v.y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public VectorP normalize() {
        double mag = magnitude();
        return new VectorP(x / mag, y / mag);
    }

    public double distance(VectorP v) {
        return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
    }

    public VectorP axis(double a) {
        a = -a;
        return new VectorP(x * Math.cos(a) + y * Math.sin(a), y * Math.cos(a) - x * Math.sin(a));
    }

    public VectorP copy() {
        return new VectorP(this.x, this.y);
    }

}
