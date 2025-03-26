package components;

public class NameComponent extends Component {
    public final String name;

    public NameComponent(String name) {
        this.name = name;
    }
    @Override
    public NameComponent clone() {
        return new NameComponent(name);
    }
}