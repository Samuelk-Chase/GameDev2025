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

    /**
     * Attempts to move from start position in the given direction.
     * Returns true if the move is possible (no blockers and pushing succeeds).
     */
    public boolean tryMove(int startX, int startY, int dx, int dy, Set<String> pushableNames, Integer bigBlueEntityId) {
        int targetX = startX + dx;
        int targetY = startY + dy;

        List<Integer> entitiesAtTarget = getEntitiesAt(targetX, targetY);

        // Check for blocking entities (Stop and not Pushable, or Hedge)
        for (int id : entitiesAtTarget) {
            String name = entityManager.getEntityName(id);
            if (name.equals("Hedge")) {
                return false;
            }
            if (entityManager.getComponent(id, RuleComponent.class) == null) {
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop") && !pushableNames.contains(name)) {
                    return false;
                }
            }
        }

        // Try to push all pushable entities
        for (int id : entitiesAtTarget) {
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, bigBlueEntityId, 0)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Attempts to push an entity in the given direction.
     * Returns true if the push succeeds.
     */
    private boolean tryPush(int entityId, int dx, int dy, Set<String> pushableNames, Integer bigBlueEntityId, int depth) {
        if (depth > 5) return false; // Prevent infinite recursion

        PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
        if (pos == null) return false;

        int nextX = pos.x + dx;
        int nextY = pos.y + dy;
        System.out.println("Moved entity " + entityId + " to (" + nextX + ", " + nextY + ")");
        List<Integer> entitiesAtNext = getEntitiesAt(nextX, nextY);

        // Check for blockers
        for (int id : entitiesAtNext) {
            if (id == entityId) continue;
            String name = entityManager.getEntityName(id);
            if (name.equals("Hedge")) {
                return false;
            }
            if (entityManager.getComponent(id, RuleComponent.class) == null) {
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop") && !pushableNames.contains(name)) {
                    return false;
                }
            }
        }

        // Try to push all pushable entities at the next position
        for (int id : entitiesAtNext) {
            if (id == entityId) continue;
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, bigBlueEntityId, depth + 1)) {
                    return false;
                }
            }
        }

        // Move the entity
        pos.x = nextX;
        pos.y = nextY;

        // Check and apply "Sink" condition
        checkAndApplySink(nextX, nextY);

        return true;
    }

    /**
     * Checks if the position has multiple entities and if any have "Sink".
     * If so, destroys all entities at that position.
     */
    public void checkAndApplySink(int x, int y) {
        List<Integer> entitiesAtPos = getEntitiesAt(x, y); // Get all entities at this position
        if (entitiesAtPos.size() >= 2) { // Check if multiple entities overlap
            boolean hasSink = false;
            for (int id : entitiesAtPos) {
                String name = entityManager.getEntityName(id);
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Sink")) {
                    hasSink = true;
                    break;
                }
            }
            if (hasSink) {
                System.out.println("Sink at (" + x + ", " + y + "): Destroying " + entitiesAtPos);
                for (int id : entitiesAtPos) {
                    entityManager.destroyEntity(id); // Destroy each entity
                }
            }
        }
    }

    /**
     * Determines if an entity is pushable.
     */
    private boolean isPushable(int id, Set<String> pushableNames) {
        if (entityManager.getComponent(id, RuleComponent.class) != null) {
            return true; // Word entities are always pushable
        }
        String name = entityManager.getEntityName(id);
        return pushableNames.contains(name); // Gameplay entities with "Push"
    }

    /**
     * Gets all entities at a specific position.
     */
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
}