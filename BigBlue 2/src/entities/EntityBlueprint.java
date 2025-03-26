package entities;

public class EntityBlueprint {
    public final String spritePath;
    public final boolean isText;
    public final String word; // <-- 🔥 make sure this exists

    public EntityBlueprint(String spritePath, boolean isText, String word) {
        this.spritePath = spritePath;
        this.isText = isText;
        this.word = word;
    }
}