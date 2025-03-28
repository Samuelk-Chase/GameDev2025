package screens;

import edu.usu.graphics.Graphics2D;

public class MenuButton extends Button {

    private final KeyboardHandler.KeyAction action;

    public MenuButton(float x, float y, float width, float height, String text, Graphics2D graphics, KeyboardHandler.KeyAction action) {
        super(x, y, width, height, text, graphics);
        this.action = action;
    }

    public static ButtonCreator makeCreator(KeyboardHandler.KeyAction action) {
        return (float x, float y, float width, float height, String text, Graphics2D graphics) -> new MenuButton(x, y, width, height, text, graphics, action);
    }

    public void click(double elapsedTime) {
        action.run(elapsedTime);
    }
}