package entities;

import components.Component;
import java.util.*;

public class GameState {
    private final Map<Integer, List<Component>> entityComponents;
    private final Set<Integer> allEntityIds;

    public GameState(Map<Integer, List<Component>> entityComponents, Set<Integer> allEntityIds) {
        this.entityComponents = deepClone(entityComponents);
        this.allEntityIds = new HashSet<>(allEntityIds);
    }

    private Map<Integer, List<Component>> deepClone(Map<Integer, List<Component>> original) {
        Map<Integer, List<Component>> clone = new HashMap<>();
        for (Map.Entry<Integer, List<Component>> entry : original.entrySet()) {
            List<Component> componentList = new ArrayList<>();
            for (Component comp : entry.getValue()) {
                componentList.add(comp.clone());
            }
            clone.put(entry.getKey(), componentList);
        }
        return clone;
    }

    Map<Integer, List<Component>> getEntityComponents() {
        return entityComponents;
    }

    Set<Integer> getAllEntityIds() {
        return allEntityIds;
    }
}