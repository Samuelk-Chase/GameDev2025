package components;

import edu.usu.graphics.Texture;
import systems.Animation;

public class SpriteComponent extends Component implements Cloneable {
    private Texture texture;
    private Animation animation;
    private String texturePath;
    private int zIndex;

    public SpriteComponent(Texture texture, String texturePath) {
        this.texture = texture;
        this.texturePath = texturePath;
        this.zIndex = zIndex;
    }
    public int getZIndex() {
        return zIndex;
    }
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public SpriteComponent(Animation animation, String texturePath) {
        this.animation = animation;
        this.texturePath = texturePath;
    }

    public Texture getTexture() {
        if (animation != null) {
            return animation.getCurrentTexture();
        } else {
            return texture;
        }
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void update() {
        if (animation != null) {
            animation.update();
        }
    }
    public void setTexture(Texture texture, String texturePath) {
        this.texture = texture;
        this.animation = null;
        this.texturePath = texturePath;
    }

    public void setAnimation(Animation animation, String texturePath) {
        this.animation = animation;
        this.texture = null;
        this.texturePath = texturePath;
    }

    @Override
    public SpriteComponent clone() {
        SpriteComponent clone;
        if (animation != null) {
            clone = new SpriteComponent(animation.clone(), texturePath);
        } else {
            clone = new SpriteComponent(texture, texturePath);
        }
        return clone;
    }
}