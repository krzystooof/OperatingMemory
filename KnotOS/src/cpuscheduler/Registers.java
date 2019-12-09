package cpuscheduler;

public class Registers{
    public int ax, bc, cx, dx;

    public Registers() {
        this.ax = 0;
        this.bc = 0;
        this.cx = 0;
        this.dx = 0;
    }

    @Override
    public String toString() {
        return "Registers{" +
                "ax=" + ax +
                ", bc=" + bc +
                ", cx=" + cx +
                ", dx=" + dx +
                '}';
    }
}