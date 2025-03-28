package screens;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class MenuScreen extends Screen {
    private int buttonIndex = 0;
    private final ArrayList<Button> buttons = new ArrayList<>();
    private static final Color NORMAL_BORDER_COLOR = Color.BLACK;
    private static final Color SELECTED_BORDER_COLOR = Color.WHITE;
    private static final Color NORMAL_TEXT_COLOR = Color.WHITE;
    private static final Color SELECTED_TEXT_COLOR = Color.YELLOW;
    private Texture backgroundTexture;
    private float textureWidth;
    private float textureHeight;

    public record ButtonBundle(String text, Button.ButtonCreator creator) {
    }

    public MenuScreen(Graphics2D graphics) {
        super(graphics);
        backgroundTexture = new Texture("resources/images/image.png");
        textureWidth = backgroundTexture.getWidth();
        textureHeight = backgroundTexture.getHeight();
        forceAction(GLFW_KEY_UP, (_) -> buttonIndex = (--buttonIndex) >= 0 ? buttonIndex : buttons.size() - 1);
        forceAction(GLFW_KEY_DOWN, (_) -> buttonIndex = (++buttonIndex) % buttons.size());
        forceAction(GLFW_KEY_ENTER, (double elapsedTime) -> buttons.get(buttonIndex).click(elapsedTime));
    }

    public void addButtons(float buttonHeight, ButtonBundle[] buttonInfo) {
        float maxWidth = 0f;
        float textHeight = buttonHeight * Button.textHeightPercentage;
        for (ButtonBundle info : buttonInfo) {
            float textWidth = Button.font.measureTextWidth(info.text, textHeight);
            maxWidth = Math.max(maxWidth, textWidth);
        }
        float buttonWidth = maxWidth + 2 * (buttonHeight - textHeight);
        float buttonMargin = 0.1f * buttonHeight;
        float buttonTop = top + (height - (buttonHeight + buttonMargin) * buttonInfo.length + buttonMargin) / 2f;
        float buttonLeft = left + (width - buttonWidth) / 2f;
        for (ButtonBundle info : buttonInfo) {
            Button button = info.creator.create(buttonLeft, buttonTop, buttonWidth, buttonHeight, info.text, graphics);
            buttons.add(button);
            buttonTop += buttonHeight + buttonMargin;
        }
    }

    @Override
    public void setLoadValues() {
        buttonIndex = 0;
    }

    @Override
    public void processInput() {
    }

    @Override
    public void screenUpdate(double elapsedTime) {
    }

    @Override
    public void render() {
        graphics.draw(screenRectangle, Color.BLACK);

        if (backgroundTexture != null) {
            float textureAspect = textureWidth / textureHeight;
            float screenAspect = width / height;
            float scale;
            if (textureAspect > screenAspect) {
                scale = height / textureHeight;
            } else {
                scale = width / textureWidth;
            }
            float backgroundScaleFactor = 1.f;
            scale *= backgroundScaleFactor;
            float scaledWidth = textureWidth * scale;
            float scaledHeight = textureHeight * scale;
            float destX = left + (width - scaledWidth) / 2;
            float destY = top + (height - scaledHeight) / 2;
            Rectangle destRect = new Rectangle(destX, destY, scaledWidth, scaledHeight);
            graphics.draw(backgroundTexture, destRect, Color.WHITE);
        }
        for (int i = 0; i < buttons.size(); i++) {
            if (i == buttonIndex) {
                buttons.get(i).render(SELECTED_BORDER_COLOR, Button.highlightColor, SELECTED_TEXT_COLOR);
            } else {
                buttons.get(i).render(NORMAL_BORDER_COLOR, Button.backgroundColor, NORMAL_TEXT_COLOR);
            }
        }
    }
}