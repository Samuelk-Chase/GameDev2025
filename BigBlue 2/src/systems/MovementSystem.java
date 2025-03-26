package systems;

import components.PositionComponent;
import components.RuleComponent;
import entities.EntityManager;

import java.util.*;

public class MovementSystem {
    private final EntityManager entityManager;

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean tryMove(int startX, int startY, int dx, int dy, Set<String> pushableNames) {
        int targetX = startX + dx;
        int targetY = startY + dy;

        List<Integer> entitiesAtTarget = getEntitiesAt(targetX, targetY);

        // Check for blocking entities
        for (int id : entitiesAtTarget) {
            String name = entityManager.getEntityName(id);
            if (name.equals("Hedge")) {
                return false;
            }
            RuleComponent ruleComp = entityManager.getComponent(id, RuleComponent.class);
            if (ruleComp == null) { // Only check "Stop" for non-text entities
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop") && !pushableNames.contains(name)) {
                    return false;
                }
            }
        }

        // Try to push all pushable entities (text entities are always pushable)
        for (int id : entitiesAtTarget) {
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, 0)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean tryPush(int entityId, int dx, int dy, Set<String> pushableNames, int depth) {
        if (depth > 5) return false;

        PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
        if (pos == null) return false;

        int nextX = pos.x + dx;
        int nextY = pos.y + dy;
        List<Integer> entitiesAtNext = getEntitiesAt(nextX, nextY);

        // Check for blockers
        for (int id : entitiesAtNext) {
            if (id == entityId) continue;
            String name = entityManager.getEntityName(id);
            if (name.equals("Hedge")) {
                return false;
            }
            RuleComponent ruleComp = entityManager.getComponent(id, RuleComponent.class);
            if (ruleComp == null) { // Only check "Stop" for non-text entities
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop") && !pushableNames.contains(name)) {
                    return false;
                }
            }
        }

        // Push other pushable entities
        for (int id : entitiesAtNext) {
            if (id == entityId) continue;
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, depth + 1)) {
                    return false;
                }
            }
        }

        // Move the entity
        pos.x = nextX;
        pos.y = nextY;

        // Check for sink condition
        checkAndApplySink(nextX, nextY);

        return true;
    }

    private List<Integer> getEntitiesAt(int x, int y) {
        List<Integer> entities = new ArrayList<>();
        for (int id : entityManager.getAllEntityIds()) {
            PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);
            if (pos != null && pos.x == x && pos.y == y) {
                entities.add(id);
            }
        }
        return entities;
    }

    private boolean isPushable(int entityId, Set<String> pushableNames) {
        // Text entities (with RuleComponent) are always pushable
        if (entityManager.getComponent(entityId, RuleComponent.class) != null) {
            return true;
        }
        // Non-text entities depend on the "Push" property
        String name = entityManager.getEntityName(entityId);
        return pushableNames.contains(name);
    }

    public void checkAndApplySink(int x, int y) {
        List<Integer> entities = getEntitiesAt(x, y);
        boolean hasSink = false;
        for (int id : entities) {
            String name = entityManager.getEntityName(id);
            Set<String> props = RuleSystem.activeRules.getOrDefault(name, new HashSet<>());
            if (props.contains("Sink")) {
                hasSink = true;
                break;
            }
        }
        if (hasSink) {
            for (int id : new ArrayList<>(entities)) {
                entityManager.destroyEntity(id);
            }
        }
    }
}