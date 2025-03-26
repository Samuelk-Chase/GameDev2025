package components;

import java.util.HashSet;
import java.util.Set;

public class RuleComponent implements Component {
    private Set<String> properties = new HashSet<>();

    public void addProperty(String prop) { properties.add(prop); }
    public void removeProperty(String prop) { properties.remove(prop); }
    public boolean hasProperty(String prop) { return properties.contains(prop); }
}