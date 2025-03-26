package entities;
import components.Component;
import java.util.*;

public class EntityManager {
    private int nextEntityId = 0;
    private final Map<Integer, List<Component>> entityComponents = new HashMap<>();
    private Map<Integer, Map<Class<?>, Component>> componentMap = new HashMap<>();

    public int createEntity() {
        int id = nextEntityId++;
        entityComponents.put(id, new ArrayList<>());
        return id;
    }

    public void destroyEntity(int entityId) {
        entityComponents.remove(entityId);
    }
    public Set<Integer> getAllEntityIds() {
        return componentMap.keySet();
    }

    public void addComponent(int entityId, Component component) {
        List<Component> components = entityComponents.get(entityId);
        if (components != null) {
            components.add(component);
            // Update componentMap for efficient retrieval
            Map<Class<?>, Component> entityComps = componentMap.computeIfAbsent(entityId, k -> new HashMap<>());
            entityComps.put(component.getClass(), component);
        }
    }


    public <T extends Component> T getComponent(int entityId, Class<T> componentClass) {
        Map<Class<?>, Component> components = componentMap.get(entityId);
        if (components == null) return null;
        Component component = components.get(componentClass);
        if (component == null) return null;
        return componentClass.cast(component);
    }

    public List<Component> getAllComponents(int entityId) {
        return entityComponents.getOrDefault(entityId, Collections.emptyList());
    }

    public List<Integer> getEntitiesWith(Class<?>... componentTypes) {
        List<Integer> matchingEntities = new ArrayList<>();
        for (Map.Entry<Integer, List<Component>> entry : entityComponents.entrySet()) {
            boolean matches = true;
            for (Class<?> type : componentTypes) {
                boolean found = false;
                for (Component c : entry.getValue()) {
                    if (type.isInstance(c)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    matches = false;
                    break;
                }
            }
        }
        return matchingEntities;
    }
    public void clear() {
        entityComponents.clear();
        componentMap.clear();
    }
}