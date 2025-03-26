package systems;

import components.PositionComponent;
import entities.EntityManager;

import java.util.*;

public class MovementSystem {
    private final EntityManager entityManager;

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean tryMove(int startX, int startY, int dx, int dy, Set<String> pushableNames) {
        return tryPush(startX + dx, startY + dy, dx, dy, pushableNames, 0);
    }

    private boolean tryPush(int x, int y, int dx, int dy, Set<String> pushableNames, int depth) {
        if (depth > 5) return false; // Limit chain length to prevent infinite recursion

        int nextX = x + dx;
        int nextY = y + dy;

        int blockerId = getEntityAt(x, y);
        if (blockerId == -1) return true; // empty space — okay to move

        String name = entityManager.getEntityName(blockerId);
        if (!pushableNames.contains(name)) return false;

        if (!tryPush(nextX, nextY, dx, dy, pushableNames, depth + 1)) return false;

        // Move the current pushable
        PositionComponent pos = entityManager.getComponent(blockerId, PositionComponent.class);
        if (pos != null) {
            pos.x += dx;
            pos.y += dy;
        }
        return true;
    }

    private int getEntityAt(int x, int y) {
        for (int id : entityManager.getAllEntityIds()) {
            PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);
            if (pos != null && pos.x == x && pos.y == y) {
                return id;
            }
        }
        return -1;
    }
}