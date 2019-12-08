public class Registers{
    private int ax, bc, cx, dx;

    public Registers() {
        this.ax = 0;
        this.bc = 0;
        this.cx = 0;
        this.dx = 0;
    }

    public int getAx() {
        return ax;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public int getBc() {
        return bc;
    }

    public void setBc(int bc) {
        this.bc = bc;
    }

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
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