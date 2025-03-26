package entities;

public class EntityBlueprint {
    public String spritePath;
    public boolean isText; // For rule layer text elements
    public String ruleProperty; // Optional, for rule logic

    public EntityBlueprint(String spritePath, boolean isText, String ruleProperty) {
        this.spritePath = spritePath;
        this.isText = isText;
        this.ruleProperty = ruleProperty;
    }
}
