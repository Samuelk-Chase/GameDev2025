package systems;

import components.RuleComponent;
import entities.EntityManager;
import components.NameComponent;
import components.PositionComponent;

import java.util.*;

public class ConditionSystem {
    private final EntityManager entityManager;
    private final RuleSystem ruleSystem;
    private static final Set<String> DESTRUCTIBLE_NAMES = new HashSet<>(Arrays.asList(
            "Wall", "Rock", "Flag", "BigBlue", "Floor", "Grass", "Water", "Lava"));

    public ConditionSystem(EntityManager entityManager, RuleSystem ruleSystem) {
        this.entityManager = entityManager;
        this.ruleSystem = ruleSystem;
    }

    public int checkConditions() {
        Set<Integer> youEntities = getYouEntities();
        if (youEntities.isEmpty()) {
            return -1;
        }
        for (int youId : youEntities) {
            if (entityManager.getComponent(youId, RuleComponent.class) != null) {
                continue;
            }
            String youName = entityManager.getEntityName(youId);
            if (!DESTRUCTIBLE_NAMES.contains(youName)) {
                continue;
            }
            PositionComponent youPos = entityManager.getComponent(youId, PositionComponent.class);
            List<Integer> entitiesAtPos = getEntitiesAt(youPos.x, youPos.y);
            for (int id : entitiesAtPos) {
                if (id == youId) continue;
                String name = entityManager.getEntityName(id);
                Set<String> props = RuleSystem.activeRules.getOrDefault(name, new HashSet<>());
                if (props.contains("Kill")) {
                    entityManager.destroyEntity(youId);
                    break;
                }
            }
        }
        youEntities = getYouEntities();
        if (youEntities.isEmpty()) {
            return -1;
        }

        Set<Integer> winEntities = getWinEntities();
        for (int youId : youEntities) {
            PositionComponent youPos = entityManager.getComponent(youId, PositionComponent.class);
            for (int winId : winEntities) {
                PositionComponent winPos = entityManager.getComponent(winId, PositionComponent.class);
                if (youPos != null && winPos != null && youPos.x == winPos.x && youPos.y == winPos.y) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public Set<Integer> getYouEntities() {
        Set<Integer> youEntities = new HashSet<>();
        for (int id : entityManager.getAllEntityIds()) {
            if (entityManager.getComponent(id, RuleComponent.class) != null) continue;
            String name = entityManager.getEntityName(id);
            Set<String> props = RuleSystem.activeRules.getOrDefault(name, new HashSet<>());
            if (props.contains("You")) {
                youEntities.add(id);
            }
        }
        return youEntities;
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

    public Set<Integer> getWinEntities() {
        Set<Integer> winEntities = new HashSet<>();
        for (int id : entityManager.getAllEntityIds()) {
            String name = entityManager.getEntityName(id);
            Set<String> props = RuleSystem.activeRules.getOrDefault(name, new HashSet<>());
            if (props.contains("Win")) {
                winEntities.add(id);
            }
        }
        return winEntities;
    }
}