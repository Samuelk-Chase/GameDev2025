package screens;

import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

public abstract class Button {
    private final Rectangle rectangle;
    private final Graphics2D graphics;
    protected static final float textHeightPercentage = 0.8f;
    protected static final Font font = new Font("resources/fonts/Roboto-Bold.ttf", 64, true);
    protected static final Color backgroundColor = new Color(0.2f, 0.3f, 0.4f);
    protected static final Color highlightColor = new Color(0.3f, 0.4f, 0.5f);

    protected String text;
    private final float textX;
    private final float textY;
    private final float textHeight;

    public interface ButtonCreator {
        Button create(float x, float y, float width, float height, String text, Graphics2D graphics);
    }

    public Button(float x, float y, float width, float height, String text, Graphics2D graphics) {
        rectangle = new Rectangle(x, y, width, height);
        textHeight = height * textHeightPercentage;
        textY = y + (height - textHeight) / 2f;
        float textWidth = font.measureTextWidth(text, textHeight);
        textX = x + (width - textWidth) / 2f;
        this.text = text;
        this.graphics = graphics;
    }

    public abstract void click(double elapsedTime);

    public void render(Color buttonColor, Color textColor) {
        graphics.draw(rectangle, buttonColor);
        graphics.drawTextByHeight(font, text, textX, textY, textHeight, textColor);
    }
}