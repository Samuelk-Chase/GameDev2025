package systems;

import components.RuleComponent;
import entities.EntityManager;
import components.NameComponent;

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

        for (int id : youEntities) {
            String name = entityManager.getEntityName(id);
            Set<String> props = ruleSystem.activeRules.getOrDefault(name, new HashSet<>());
            if (props.contains("Win")) {
                return 1; // Win condition: a "You" entity has "Win"
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
}