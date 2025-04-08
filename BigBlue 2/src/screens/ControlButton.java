package screens;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import serializer.ControlConfiguration;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class ControlButton extends Button{

    private Integer currentKey;
    private final ControlConfiguration controlConfiguration;
    private final long window;
    private final String action;
    private final HashMap<Integer, String> keyNames = new HashMap<>();
    private Boolean screenPauseInput;
    private Boolean selected = false;
    private int colorIndex = 0;
    private int frameCount = 20;
    private Color[] selectedColors = new Color[]{new Color(168/255f, 166/255f, 50/255f), new Color(200/255f, 200/255f, 100/255f)};

    public ControlButton(float x, float y, float width, float height, String action, Graphics2D graphics, Integer key, ControlConfiguration controlConfiguration, Boolean screenPauseInput) {
        super(x, y, width, height, action + ": X", graphics);
        this.action = action;
        this.screenPauseInput = screenPauseInput;
        this.window = graphics.getWindow();
        this.controlConfiguration = controlConfiguration;
        keyNames.put(GLFW_KEY_UP, "^");
        keyNames.put(GLFW_KEY_DOWN, "v");
        keyNames.put(GLFW_KEY_LEFT, "<");
        keyNames.put(GLFW_KEY_RIGHT, ">");
        setKey(key);
    }

    private void setKey(Integer newKey) {
        currentKey = newKey;
        selected = false;
        String keyName = glfwGetKeyName(currentKey, 0);
        text = action + ": " + (keyName != null ? keyName : keyNames.get(currentKey));
        glfwSetKeyCallback(window, null);
    }

    public static ButtonCreator makeCreator(ControlConfiguration.Action action, ControlConfiguration controlConfiguration, Boolean screenPauseInput) {
        Integer key = controlConfiguration.getKey(action);
        return (float x, float y, float width, float height, String text, Graphics2D graphics) -> new ControlButton(x, y, width, height, text, graphics, key, controlConfiguration, screenPauseInput);
    }

    public void click(double elapsedTime) {
        selected = true;
        screenPauseInput = true;
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

    @Override
    public void render(Color textColor) {
        if (selected) {
            colorIndex++;
            colorIndex %= selectedColors.length * frameCount;
            super.render(selectedColors[colorIndex / frameCount]);
        } else {
            super.render(textColor);
        }
    }
}
