package systems;

import components.PositionComponent;
import components.RuleComponent;
import entities.EntityManager;
import java.util.*;
import systems.ParticleManager;

public class MovementSystem {
    private final EntityManager entityManager;
    private ParticleManager particleManager;
    private static final Set<String> DESTRUCTIBLE_NAMES = new HashSet<>(Arrays.asList(
            "Wall", "Rock", "Flag", "BigBlue", "Floor", "Grass", "Water", "Lava"
    ));

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setParticleManager(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }

    public boolean tryMove(int startX, int startY, int dx, int dy, Set<String> pushableNames, Set<String> youNames) {
        int targetX = startX + dx;
        int targetY = startY + dy;

        List<Integer> entitiesAtTarget = getEntitiesAt(targetX, targetY);
        for (int id : entitiesAtTarget) {
            String name = entityManager.getEntityName(id);
            if (name.equals("Hedge")) {
                return false;
            }
            RuleComponent ruleComp = entityManager.getComponent(id, RuleComponent.class);
            if (ruleComp == null) {
                Set<String> props = RuleSystem.activeRules.get(name);
                if ((props != null && props.contains("Stop") && !pushableNames.contains(name))) {
                    return false;
                } else if (youNames.contains(name)) {
                    PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);
                    if (!tryMove(pos.x, pos.y, dx, dy, pushableNames, youNames)) {
                        return false;
                    }
                }
            }
        }
        for (int id : entitiesAtTarget) {
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, youNames)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean tryPush(int entityId, int dx, int dy, Set<String> pushableNames, Set<String> youNames) {
//        if (depth > 5) return false;

        PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
        if (pos == null) return false;

        int nextX = pos.x + dx;
        int nextY = pos.y + dy;
        List<Integer> entitiesAtNext = getEntitiesAt(nextX, nextY);

        for (int id : entitiesAtNext) {
//            if (id == entityId) continue;
            String name = entityManager.getEntityName(id);
            if (name.equals("Hedge")) {
                return false;
            }
            RuleComponent ruleComp = entityManager.getComponent(id, RuleComponent.class);
            if (ruleComp == null) {
                Set<String> props = RuleSystem.activeRules.get(name);
                if (props != null && props.contains("Stop") && !pushableNames.contains(name)) {
                    return false;
                } else if (youNames.contains(name)) {
                    PositionComponent youPos = entityManager.getComponent(id, PositionComponent.class);
                    if (!tryMove(youPos.x, youPos.y, dx, dy, pushableNames, youNames)) {
                        return false;
                    }
                }
            }
        }

        for (int id : entitiesAtNext) {
//            if (id == entityId) continue;
            if (isPushable(id, pushableNames)) {
                if (!tryPush(id, dx, dy, pushableNames, youNames)) {
                    return false;
                }
            }
        }
        pos.x = nextX;
        pos.y = nextY;
        if (entityManager.getComponent(entityId, RuleComponent.class) == null) {
            checkAndApplySink(nextX, nextY);
        }
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
        if (entityManager.getComponent(entityId, RuleComponent.class) != null) {
            return true;
        }
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
                if (entityManager.getComponent(id, RuleComponent.class) == null &&
                        DESTRUCTIBLE_NAMES.contains(entityManager.getEntityName(id))) {
                    PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);
                    if (pos != null && particleManager != null) {
                        particleManager.createSparkleEffect(pos.x, pos.y);
                    }
                    entityManager.destroyEntity(id);
                }
            }
        }
    }
}