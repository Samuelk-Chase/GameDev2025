package entities;

import components.Component;
import components.NameComponent;

import java.util.*;

public class EntityManager {
    private int nextEntityId = 0;
    private final Map<Integer, List<Component>> entityComponents = new HashMap<>();
    private final Map<Integer, Map<Class<?>, Component>> componentMap = new HashMap<>();
    private final Set<Integer> allEntityIds = new HashSet<>();

    public int createEntity() {
        int id = nextEntityId++;
        entityComponents.put(id, new ArrayList<>());
        componentMap.put(id, new HashMap<>());
        allEntityIds.add(id); // Track the new entity
        return id;
    }

    public void destroyEntity(int entityId) {
        System.out.println("Destroying entity: " + entityId); // Debug log
        componentMap.remove(entityId); // Remove all components for this entity
        entityComponents.remove(entityId); // Remove from entityComponents
        allEntityIds.remove(entityId); // Remove from active entities
    }

    public void addComponent(int entityId, Component component) {
        List<Component> components = entityComponents.get(entityId);
        if (components != null) {
            components.add(component);
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

    public Set<Integer> getAllEntityIds() {
        return new HashSet<>(allEntityIds); // Return a copy to prevent external modification
    }

    public boolean isEntityActive(int entityId) {
        return allEntityIds.contains(entityId);
    }

    public String getEntityName(int entityId) {
        NameComponent nameComp = getComponent(entityId, NameComponent.class);
        return nameComp != null ? nameComp.name : null;
    }

    public void clear() {
        entityComponents.clear();
        componentMap.clear();
        allEntityIds.clear();
    }
}