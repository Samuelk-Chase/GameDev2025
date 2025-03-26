package components;

import edu.usu.graphics.Texture;
import systems.Animation;

public class SpriteComponent implements Component {
    private Texture texture;
    private Animation animation;
    private String texturePath;

    public SpriteComponent(Texture texture, String path) {
        this.texture = texture;
        this.texturePath = path;
    }

    public SpriteComponent(Animation animation, String path) {
        this.animation = animation;
        this.texturePath = path;
    }

    public Texture getTexture() {
        return (animation != null) ? animation.getCurrentTexture() : texture;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void update() {
        if (animation != null) {
            animation.update();
        }
    }
}