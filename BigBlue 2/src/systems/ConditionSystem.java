package systems;

import components.PositionComponent;
import entities.EntityManager;
import systems.RuleSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConditionSystem {
    private final EntityManager entityManager;
    private final RuleSystem ruleSystem;
    private final Integer bigBlueEntityId;

    public ConditionSystem(EntityManager entityManager, RuleSystem ruleSystem, Integer bigBlueEntityId) {
        this.entityManager = entityManager;
        this.ruleSystem = ruleSystem;
        this.bigBlueEntityId = bigBlueEntityId;
    }

    /**
     * Checks if victory or death conditions are met.
     * Returns:
     * - 1 if victory condition is met
     * - -1 if death condition is met
     * - 0 otherwise
     */
    public int checkConditions() {
        if (bigBlueEntityId == null) return 0;

        PositionComponent bigBluePos = entityManager.getComponent(bigBlueEntityId, PositionComponent.class);
        if (bigBluePos == null) return 0;

        int x = bigBluePos.x;
        int y = bigBluePos.y;

        List<Integer> entitiesAtPos = getEntitiesAt(x, y);

        for (int id : entitiesAtPos) {
            if (id == bigBlueEntityId) continue; // Skip Big Blue itself

            String name = entityManager.getEntityName(id);
            Set<String> props = ruleSystem.activeRules.get(name);

            if (props != null) {
                if (props.contains("Win")) {
                    return 1; // Victory
                }
                if (props.contains("Kill")) {
                    return -1; // Death
                }
            }
        }

        return 0; // No condition met
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
}