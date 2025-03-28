package screens;

import edu.usu.graphics.Graphics2D;

import static org.lwjgl.glfw.GLFW.*;

public class ControlButton extends Button{

    private Integer currentKey;
    private final KeyboardHandler controlKeyboard;
    private final long window;
    private final String action;

    public ControlButton(float x, float y, float width, float height, String action, Graphics2D graphics, Integer key, KeyboardHandler controlKeyboard) {
        super(x, y, width, height, "", graphics);
        this.action = action;
        setText(key);
        this.controlKeyboard = controlKeyboard;
        this.window = graphics.getWindow();
    }

    private void setText(Integer newKey) {
        currentKey = newKey;
        text = action + ": " + glfwGetKeyName(currentKey, 0);
    }

    public static ButtonCreator makeCreator(Integer key, KeyboardHandler controlKeyboard) {
        return (float x, float y, float width, float height, String text, Graphics2D graphics) -> new ControlButton(x, y, width, height, text, graphics, key, controlKeyboard);
    }

    public void click(double elapsedTime) {
        glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
            if (action == GLFW_PRESS && key != GLFW_KEY_ESCAPE) {
                if (controlKeyboard.changeKey(currentKey, key)) {
                    setText(key);
                }
            }
        });
    }
}
