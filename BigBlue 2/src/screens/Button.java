package screens;

import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

public class Button {
    private final KeyboardHandler.KeyAction action;
    private final Rectangle borderRectangle;
    private final Rectangle rectangle;
    private final Graphics2D graphics;
    private static final float borderPercentage = 0.05f;
    protected static final float textHeightPercentage = 0.75f;
    protected static final Font font = new Font("resources/fonts/Roboto-Bold.ttf", 64, true);
    protected static final Color backgroundColor = new Color(0.25f, 0.25f, 0.25f);
    protected static final Color highlightColor = new Color(0.35f, 0.35f, 0.35f);
    private final String text;
    private final float textX;
    private final float textY;
    private final float textHeight;


    public Button(float x, float y, float width, float height, String text, KeyboardHandler.KeyAction action, Graphics2D graphics) {
        borderRectangle = new Rectangle(x, y, width, height);
        float borderWidth = height * borderPercentage;
        rectangle = new Rectangle(x + borderWidth, y + borderWidth, width - 2 * borderWidth, height - 2 * borderWidth);
        textHeight = height * textHeightPercentage;
        textY = y + (height - textHeight) / 2f;
        float textWidth = font.measureTextWidth(text, textHeight);
        textX = x + (width - textWidth) / 2f;
        this.text = text;
        this.action = action;
        this.graphics = graphics;
    }

    public void click(double elapsedTime) {
        action.run(elapsedTime);
    }

    public void render(Color buttonColor) {
        graphics.draw(borderRectangle, Color.BLACK);
        graphics.draw(rectangle, buttonColor);
        graphics.drawTextByHeight(font, text, textX, textY, textHeight, Color.GREEN);
    }
}
