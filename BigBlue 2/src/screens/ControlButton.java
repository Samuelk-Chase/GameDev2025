package screens;

import edu.usu.graphics.Graphics2D;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class ControlButton extends Button{

    private Integer currentKey;
    private final KeyboardHandler controlKeyboard;
    private final long window;
    private final String action;
    private Boolean screenPauseInput;

    public ControlButton(float x, float y, float width, float height, String action, Graphics2D graphics, Integer key, KeyboardHandler controlKeyboard, Boolean screenPauseInput) {
        super(x, y, width, height, "", graphics);
        this.action = action;
        this.screenPauseInput = screenPauseInput;
        this.window = graphics.getWindow();
        this.controlKeyboard = controlKeyboard;
        setText(key);
    }

    private void setText(Integer newKey) {
        currentKey = newKey;
        text = action + ": " + currentKey;
        glfwSetKeyCallback(window, null);
    }

    public static ButtonCreator makeCreator(Integer key, KeyboardHandler controlKeyboard, Boolean screenPauseInput) {
        return (float x, float y, float width, float height, String text, Graphics2D graphics) -> new ControlButton(x, y, width, height, text, graphics, key, controlKeyboard, screenPauseInput);
    }

    public void click(double elapsedTime) {
        glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
            if (action == GLFW_PRESS) {
                if (key != GLFW_KEY_ESCAPE &&  key != GLFW_KEY_ENTER) {
                    if (controlKeyboard.changeKey(currentKey, key)) {
                        System.out.println("Changed");
                        setText(key);
                    }
                screenPauseInput = false;
                }
            }
        });
    }
}
