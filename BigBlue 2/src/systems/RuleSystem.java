package systems;

import components.PositionComponent;
import components.RuleComponent;
import entities.EntityManager;

import java.util.*;

public class RuleSystem {
    private final EntityManager entityManager;

    public Map<String, Set<String>> activeRules = new HashMap<>(); // e.g., "Wall" -> ["Stop"]

    public RuleSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update() {
        activeRules.clear();
        Map<String, RuleComponent.Type> wordGrid[][] = new HashMap[20][20];

        for (int id : entityManager.getAllEntityIds()) {
            RuleComponent rule = entityManager.getComponent(id, RuleComponent.class);
            PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);

            if (rule != null && pos != null) {
                if (wordGrid[pos.y][pos.x] == null) {
                    wordGrid[pos.y][pos.x] = new HashMap<>();
                }
                wordGrid[pos.y][pos.x].put(rule.word, rule.type);
            }
        }

        // Check horizontally and vertically for patterns
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                checkAndAddRule(wordGrid, x, y, 1, 0); // horizontal
                checkAndAddRule(wordGrid, x, y, 0, 1); // vertical
            }
        }
    }

    private void checkAndAddRule(Map<String, RuleComponent.Type>[][] grid, int x, int y, int dx, int dy) {
        if (x + 2 >= 20 || y + 2 >= 20) return;

        String subj = getWord(grid, x, y, RuleComponent.Type.SUBJECT);
        String is = getWord(grid, x + dx, y + dy, RuleComponent.Type.OPERATOR);
        String prop = getWord(grid, x + 2 * dx, y + 2 * dy, RuleComponent.Type.PROPERTY);

        if (subj != null && "Is".equals(is) && prop != null) {
            activeRules.putIfAbsent(subj, new HashSet<>());
            activeRules.get(subj).add(prop);
        }
    }

    private String getWord(Map<String, RuleComponent.Type>[][] grid, int x, int y, RuleComponent.Type expectedType) {
        if (grid[y][x] == null) return null;
        for (Map.Entry<String, RuleComponent.Type> entry : grid[y][x].entrySet()) {
            if (entry.getValue() == expectedType) return entry.getKey();
        }
        return null;
    }
}