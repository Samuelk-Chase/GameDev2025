package serializer;

import java.util.HashMap;
import java.util.Set;

public class ControlConfiguration {
    public enum Action {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UNDO,
        RESTART
    }

    protected HashMap<Integer, Action> actionMap;

    public ControlConfiguration() {
        actionMap = new HashMap<>();
    }

    public Set<Integer> getKeys() {
        return actionMap.keySet();
    }

    public void setKey(Integer key, Action action) {
        actionMap.put(key, action);
    }

    public Integer getKey(Action action) {
        for (Integer key : actionMap.keySet()) {
            if (action == actionMap.get(key)) {
                return key;
            }
        }
        return null;
    }

    public Action getAction(Integer key) {
        return actionMap.get(key);
    }

    public void copyControlConfiguration(ControlConfiguration controlConfiguration) {
        this.actionMap.clear();
        for (Integer key : controlConfiguration.actionMap.keySet()) {
            this.actionMap.put(key, controlConfiguration.actionMap.get(key));
        }
    }

    public boolean changeKey(Integer oldKey, Integer newKey) {
        if (actionMap.containsKey(oldKey) && !actionMap.containsKey(newKey)) {
            actionMap.put(newKey, actionMap.get(oldKey));
            actionMap.remove(oldKey);
            return true;
        }
        return false;
    }
}
