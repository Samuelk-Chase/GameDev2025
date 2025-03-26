package components;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RuleComponent implements Component {
    public enum Type { SUBJECT, OPERATOR, PROPERTY }
    public Map<String, Set<String>> activeRules = new HashMap<>();
    public String word;
    public Type type;

    public RuleComponent(String word, Type type) {
        this.word = word;
        this.type = type;
    }
}