package components;

public class RuleComponent implements Component {
    public enum Type { SUBJECT, OPERATOR, PROPERTY }

    public String word;
    public Type type;

    public RuleComponent(String word, Type type) {
        this.word = word;
        this.type = type;
    }
}