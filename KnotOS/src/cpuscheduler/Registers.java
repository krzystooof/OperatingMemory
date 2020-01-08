package cpuscheduler;
/**
 * Class Registers represents all 4 registers ax, bx, cx, dx
 */
public class Registers{
    public int ax, bx, cx, dx;

    public Registers() {
        this.ax = 0;
        this.bx = 0;
        this.cx = 0;
        this.dx = 0;
    }

    @Override
    public String toString() {
        return "Registers{" +
                "ax=" + ax +
                ", bx=" + bx +
                ", cx=" + cx +
                ", dx=" + dx +
                '}';
    }
}