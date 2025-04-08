package screens;

import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

public abstract class Button {
    private final Graphics2D graphics;
    protected static final float textHeightPercentage = 0.8f;
    protected static final Font font = new Font("resources/fonts/Roboto-Bold.ttf", 64, true);

    protected String text;
    private final float textX;
    private final float textY;
    private final float textHeight;

    public interface ButtonCreator {
        Button create(float x, float y, float width, float height, String text, Graphics2D graphics);
    }

    public Button(float x, float y, float width, float height, String text, Graphics2D graphics) {
        textHeight = height * textHeightPercentage;
        textY = y + (height - textHeight) / 2f;
        float textWidth = font.measureTextWidth(text, textHeight);
        textX = x + (width - textWidth) / 2f;
        this.text = text;
        this.graphics = graphics;
    }

    public abstract void click(double elapsedTime);

    public void render(Color textColor) {
        graphics.drawTextByHeight(font, text, textX, textY, textHeight, textColor);
    }
}