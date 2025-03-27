package screens;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardHandler {

    public interface KeyAction {
        public void run(double elapsedTime);
    }

    private final HashMap<Integer, KeyAction> keyActions = new HashMap<>();
    private final HashMap<Integer, Boolean> newlyPressed = new HashMap<>();
    private final long window;

    public KeyboardHandler(Long window) {
        this.window = window;
    }

    public void addAction(Integer key, KeyAction action) throws Exception{
        if (!keyActions.containsKey(key)) {
            setAction(key, action);
        } else {
            throw new Exception("Error: Accidental attempt to overwrite key " + key);
        }
    }

    public void setAction(Integer key, KeyAction action) {
        keyActions.put(key, action);
        newlyPressed.put(key, true);
    }

    public boolean removeKey(Integer key) {
        if (keyActions.containsKey(key)) {
            keyActions.remove(key);
            newlyPressed.remove(key);
            return true;
        }
        return false;
    }

    public void copyPressed(KeyboardHandler other) {
        for (Integer key : other.newlyPressed.keySet()) {
            if (newlyPressed.containsKey(key)) {
                newlyPressed.put(key, other.newlyPressed.get(key));
            }
        }
    }

    public boolean changeKey(Integer oldKey, Integer newKey) {
        if (keyActions.containsKey(oldKey)) {
            if (!keyActions.containsKey(newKey)) {
                keyActions.put(newKey, keyActions.get(oldKey));
                newlyPressed.put(newKey, false);
                keyActions.remove(oldKey);
                newlyPressed.remove(oldKey);
                return true;
            }
            return false;
        }
        return false;
    }

    public void update(double elapsedTime) {
        for (Integer key : keyActions.keySet()) {
            if (glfwGetKey(window, key) == GLFW_PRESS) {
                if (newlyPressed.get(key)) {
                    keyActions.get(key).run(elapsedTime);
                    newlyPressed.put(key, false);
                }
            } else {
                newlyPressed.put(key, true);
            }
        }
    }
}
