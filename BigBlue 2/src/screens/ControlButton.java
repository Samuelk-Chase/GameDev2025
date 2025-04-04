package screens;

import edu.usu.graphics.Graphics2D;
import serializer.ControlConfiguration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class ControlButton extends Button{

    private Integer currentKey;
    private final ControlConfiguration controlConfiguration;
    private final long window;
    private final String action;
    private Boolean screenPauseInput;

    public ControlButton(float x, float y, float width, float height, String action, Graphics2D graphics, Integer key, ControlConfiguration controlConfiguration, Boolean screenPauseInput) {
        super(x, y, width, height, "", graphics);
        this.action = action;
        this.screenPauseInput = screenPauseInput;
        this.window = graphics.getWindow();
        this.controlConfiguration = controlConfiguration;
        setKey(key);
    }

    private void setKey(Integer newKey) {
        currentKey = newKey;
        text = action + ": " + glfwGetKeyName(currentKey, 0);
        glfwSetKeyCallback(window, null);
    }

    public static ButtonCreator makeCreator(ControlConfiguration.Action action, ControlConfiguration controlConfiguration, Boolean screenPauseInput) {
        Integer key = controlConfiguration.getKey(action);
        return (float x, float y, float width, float height, String text, Graphics2D graphics) -> new ControlButton(x, y, width, height, text, graphics, key, controlConfiguration, screenPauseInput);
    }

    public void click(double elapsedTime) {
        glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
            if (action == GLFW_PRESS) {
                if (key != GLFW_KEY_ESCAPE && key != GLFW_KEY_ENTER) {
                    if (controlConfiguration.changeKey(currentKey, key) || key == currentKey) {
                        setKey(key);
                    }
                screenPauseInput = false;
                }
            }
        });
    }
}
