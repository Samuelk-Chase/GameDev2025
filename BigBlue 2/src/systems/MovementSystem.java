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
     * Attempts to move Big Blue from (startX, startY) in direction (dx, dy).
     * Returns true if the move succeeds, false if blocked.
     */
    public boolean tryMove(int startX, int startY, int dx, int dy, Set<String> pushableNames) {
        int targetX = startX + dx;
        int targetY = startY + dy;

        List<Integer> entitiesAtTarget = getEntitiesAt(targetX, targetY);

        // Check for blocking entities
        for (int id : entitiesAtTarget) {
            String name = entityManager.getEntityName(id);
            // Default blockers
            if (name.equals("Hedge") || name.equals("Flag")) {
                return false;
            }
            // Gameplay entities (no RuleComponent) can block if they have "Stop"
            if (entityManager.getComponent(id, RuleComponent.class) == null) {
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop")) {
                    return false;
                }
            }
        }

        // Try to push all pushable entities
        for (int id : entitiesAtTarget) {
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, 0)) {
                    return false; // Cannot push this entity
                }
            }
        }

        // All pushable entities were pushed successfully, so the move is allowed
        return true;
    }

    /**
     * Determines if an entity is pushable.
     * Word entities (with RuleComponent) are always pushable.
     * Gameplay entities are pushable if their name is in pushableNames (i.e., have "Push").
     */
    private boolean isPushable(int id, Set<String> pushableNames) {
        if (entityManager.getComponent(id, RuleComponent.class) != null) {
            return true; // Word entities are always pushable
        }
        String name = entityManager.getEntityName(id);
        return pushableNames.contains(name); // Gameplay entities with "Push"
    }

    /**
     * Attempts to push a specific entity in direction (dx, dy).
     * Handles chains of pushable entities recursively, up to a depth limit.
     */
    private boolean tryPush(int entityId, int dx, int dy, Set<String> pushableNames, int depth) {
        if (depth > 5) return false; // Prevent infinite recursion

        PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
        if (pos == null) return false;

        int nextX = pos.x + dx;
        int nextY = pos.y + dy;

        // Check entities at the next position
        List<Integer> entitiesAtNext = getEntitiesAt(nextX, nextY);
        for (int id : entitiesAtNext) {
            if (id == entityId) continue; // Skip the entity being pushed
            String name = entityManager.getEntityName(id);
            // Blocked by default blockers
            if (name.equals("Hedge") || name.equals("Flag")) {
                return false;
            }
            // Blocked by gameplay entities with "Stop"
            if (entityManager.getComponent(id, RuleComponent.class) == null) {
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop")) {
                    return false;
                }
            }
            // If the entity is pushable, try pushing it first
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, depth + 1)) {
                    return false;
                }
            }
        }

        // All checks passed, move the entity
        pos.x = nextX;
        pos.y = nextY;
        return true;
    }

    /**
     * Returns a list of all entity IDs at position (x, y).
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