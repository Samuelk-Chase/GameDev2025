package entities;

import components.Component;
import components.NameComponent;
import components.PositionComponent;
import screens.GameplayScreen;
import java.util.*;

public class EntityManager {
    private int nextEntityId = 0;
    private final Map<Integer, List<Component>> entityComponents = new HashMap<>();
    private final Map<Integer, Map<Class<?>, Component>> componentMap = new HashMap<>();
    private final Set<Integer> allEntityIds = new HashSet<>();
    private final GameplayScreen gameplayScreen;

    public EntityManager(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public int createEntity() {
        int id = nextEntityId++;
        entityComponents.put(id, new ArrayList<>());
        componentMap.put(id, new HashMap<>());
        allEntityIds.add(id);
        return id;
    }

    public void destroyEntity(int entityId) {
        PositionComponent pos = getComponent(entityId, PositionComponent.class);
        if (pos != null) {
            gameplayScreen.onEntityRemoved(pos.x, pos.y);
        }
        componentMap.remove(entityId);
        entityComponents.remove(entityId);
        allEntityIds.remove(entityId);
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

    public void restoreState(GameState state) {
        this.entityComponents.clear();
        this.entityComponents.putAll(state.getEntityComponents());
        this.allEntityIds.clear();
        this.allEntityIds.addAll(state.getAllEntityIds());
        this.componentMap.clear();
        for (Map.Entry<Integer, List<Component>> entry : entityComponents.entrySet()) {
            int entityId = entry.getKey();
            Map<Class<?>, Component> compMap = new HashMap<>();
            for (Component comp : entry.getValue()) {
                compMap.put(comp.getClass(), comp);
            }
            componentMap.put(entityId, compMap);
        }
    }

    public Set<Integer> getAllEntityIds() {
        return new HashSet<>(allEntityIds);
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

    public GameState saveState() {
        return new GameState(entityComponents, allEntityIds);
    }
}