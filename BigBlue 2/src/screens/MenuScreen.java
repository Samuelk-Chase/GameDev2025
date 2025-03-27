package screens;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class MenuScreen extends Screen {
    private int buttonIndex = 0;
    private final ArrayList<Button> buttons = new ArrayList<>();

    public record ButtonBundle(String text, KeyboardHandler.KeyAction action) {

    }

    public MenuScreen(Graphics2D graphics) {
        super(graphics);
        forceAction(GLFW_KEY_UP, (_) -> {buttonIndex = (--buttonIndex) >= 0 ? buttonIndex : buttons.size() - 1;});
        forceAction(GLFW_KEY_DOWN, (_) -> {buttonIndex = (++buttonIndex) % buttons.size();});
        forceAction(GLFW_KEY_ENTER, (double elapsedTime) -> {buttons.get(buttonIndex).click(elapsedTime);});
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
            Button button = new Button(buttonLeft, buttonTop, buttonWidth, buttonHeight, info.text, info.action, graphics);
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
        graphics.draw(screenRectangle, Color.PURPLE);
        for (int i = 0; i < buttonIndex; i++) {
            buttons.get(i).render(Button.backgroundColor);
        }
        buttons.get(buttonIndex).render(Button.highlightColor);
        for (int i = buttonIndex + 1; i < buttons.size(); i++) {
            buttons.get(i).render(Button.backgroundColor);
        }
    }
}
