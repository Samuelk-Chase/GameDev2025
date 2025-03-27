package systems;

import components.PositionComponent;
import components.RuleComponent;
import entities.EntityManager;

import java.util.*;

public class RuleSystem {
    private final EntityManager entityManager;
    public static Map<String, Set<String>> activeRules = new HashMap<>();
    public Map<String, String> transformations = new HashMap<>();

    public RuleSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update() {
        activeRules.clear();
        transformations.clear(); // Reset transformations each update
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

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                checkAndAddRule(wordGrid, x, y, 1, 0); // horizontal
                checkAndAddRule(wordGrid, x, y, 0, 1); // vertical
            }
        }
        System.out.println("Transformations: " + transformations);
    }

    // New method to resolve transformation chains
    public String getFinalTransformation(String name) {
        Set<String> visited = new HashSet<>();
        String currentName = name;
        while (transformations.containsKey(currentName) && !visited.contains(currentName)) {
            visited.add(currentName);
            currentName = transformations.get(currentName);
        }
        return currentName;
    }

    private Map.Entry<String, RuleComponent.Type> getWordEntry(Map<String, RuleComponent.Type>[][] grid, int x, int y) {
        if (x < 0 || x >= 20 || y < 0 || y >= 20 || grid[y][x] == null || grid[y][x].isEmpty()) {
            return null;
        }
        return grid[y][x].entrySet().iterator().next(); // Assumes one word per position
    }

    private void checkAndAddRule(Map<String, RuleComponent.Type>[][] grid, int x, int y, int dx, int dy) {
        if (x + 2 >= 20 || y + 2 >= 20) return;

        Map.Entry<String, RuleComponent.Type> subjEntry = getWordEntry(grid, x, y);
        Map.Entry<String, RuleComponent.Type> isEntry = getWordEntry(grid, x + dx, y + dy);
        Map.Entry<String, RuleComponent.Type> thirdEntry = getWordEntry(grid, x + 2 * dx, y + 2 * dy);

        if (subjEntry != null && isEntry != null && thirdEntry != null) {
            if (subjEntry.getValue() == RuleComponent.Type.SUBJECT &&
                    "Is".equals(isEntry.getKey()) &&
                    isEntry.getValue() == RuleComponent.Type.OPERATOR) {
                if (thirdEntry.getValue() == RuleComponent.Type.PROPERTY) {
                    activeRules.putIfAbsent(subjEntry.getKey(), new HashSet<>());
                    activeRules.get(subjEntry.getKey()).add(thirdEntry.getKey());
                } else if (thirdEntry.getValue() == RuleComponent.Type.SUBJECT) {
                    transformations.put(subjEntry.getKey(), thirdEntry.getKey());
                }
            }
        }
    }
}