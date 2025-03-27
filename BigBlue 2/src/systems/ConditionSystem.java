package systems;

import components.RuleComponent;
import entities.EntityManager;
import components.NameComponent;
import components.PositionComponent; // Adjust import based on your actual component package and name

import java.util.*;

public class ConditionSystem {
    private final EntityManager entityManager;
    private final RuleSystem ruleSystem;

    public ConditionSystem(EntityManager entityManager, RuleSystem ruleSystem) {
        this.entityManager = entityManager;
        this.ruleSystem = ruleSystem;
    }

    /**
     * Checks win and lose conditions.
     * Returns 1 for win, -1 for lose, 0 for continue.
     */
    public int checkConditions() {
        Set<Integer> youEntities = getYouEntities();
        if (youEntities.isEmpty()) {
            return -1; // Lose condition: no "You" entities remain
        }

        Set<Integer> winEntities = getWinEntities();
        for (int youId : youEntities) {
            PositionComponent youPos = entityManager.getComponent(youId, PositionComponent.class);
            for (int winId : winEntities) {
                PositionComponent winPos = entityManager.getComponent(winId, PositionComponent.class);
                if (youPos != null && winPos != null && youPos.x == winPos.x && youPos.y == winPos.y) {
                    return 1; // Win condition: a "You" entity is at the same position as a "Win" entity
                }
            }
        }
        return 0; // Game continues
    }

    /**
     * Retrieves all entities with the "You" property.
     */
    public Set<Integer> getYouEntities() {
        Set<Integer> youEntities = new HashSet<>();
        for (String name : ruleSystem.activeRules.keySet()) {
            if (ruleSystem.activeRules.get(name).contains("You")) {
                for (int id : entityManager.getAllEntityIds()) {
                    NameComponent nameComp = entityManager.getComponent(id, NameComponent.class);
                    RuleComponent ruleComp = entityManager.getComponent(id, RuleComponent.class);
                    if (nameComp != null && nameComp.name.equals(name) && ruleComp == null) {
                        youEntities.add(id);
                    }
                }
            }
        }
        return youEntities;
    }

    /**
     * Retrieves all entities with the "Win" property.
     */
    public Set<Integer> getWinEntities() {
        Set<Integer> winEntities = new HashSet<>();
        for (String name : ruleSystem.activeRules.keySet()) {
            if (ruleSystem.activeRules.get(name).contains("Win")) {
                for (int id : entityManager.getAllEntityIds()) {
                    NameComponent nameComp = entityManager.getComponent(id, NameComponent.class);
                    RuleComponent ruleComp = entityManager.getComponent(id, RuleComponent.class);
                    if (nameComp != null && nameComp.name.equals(name) && ruleComp == null) {
                        winEntities.add(id);
                    }
                }
            }
        }
        return winEntities;
    }
}