package components;

public class PositionComponent extends Component {
    public int x;
    public int y;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public PositionComponent clone() {
        return new PositionComponent(x, y);
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
