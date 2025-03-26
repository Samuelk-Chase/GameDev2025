import components.NameComponent;
import components.PositionComponent;
import components.RuleComponent;
import components.SpriteComponent;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;
import entities.EntityBlueprint;
import entities.EntityManager;
import entities.GameState;
import systems.Animation;
import systems.ConditionSystem;
import systems.MovementSystem;
import systems.RuleSystem;
import util.ParseLevel;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private List<ParseLevel.LevelData> levels;
    private ParseLevel.LevelData currentLevel;
    private EntityManager entityManager;
    private Graphics2D graphics;
    private Map<Character, EntityBlueprint> charMap;
    private int currentLevelIndex = 0;
    private boolean running = true;
    private long lastTime;
    private final Map<String, Color> textureTints = new HashMap<>();
    private final Set<Integer> keysPressedLastFrame = new HashSet<>();
    private RuleSystem ruleSystem;
    private MovementSystem movementSystem;
    private ConditionSystem conditionSystem;
    private final Stack<GameState> undoStack = new Stack<>();

    // Constructor
    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.entityManager = new EntityManager();
        this.charMap = new HashMap<>();
        this.ruleSystem = new RuleSystem(entityManager);
        this.movementSystem = new MovementSystem(entityManager);
        this.conditionSystem = new ConditionSystem(entityManager, ruleSystem);
        initializeCharMap();
        initializeTextureTints();
    }

    // Initialize the game
    public void initialize() {
        loadLevels();
    }

    // Load all levels from file
    private void loadLevels() {
        ParseLevel parser = new ParseLevel();
        levels = parser.parseLevels("resources/levels/levels-all.bbiy");
        if (!levels.isEmpty()) {
            setLevel(levels.get(0));
        } else {
            System.err.println("No levels found!");
        }
    }

    // Set the current level and load its entities
    public void setLevel(ParseLevel.LevelData level) {
        this.currentLevel = level;
        entityManager.clear();
        loadLevelEntities();
        ruleSystem.update();
    }

    // Load the next level
    private void loadNextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.size()) {
            setLevel(levels.get(currentLevelIndex));
        } else {
            System.out.println("Game Completed!");
            running = false;
        }
    }

    // Reset the current level
    private void resetLevel() {
        setLevel(levels.get(currentLevelIndex));
    }

    // Load entities from the level data
    private void loadLevelEntities() {
        for (int y = 0; y < currentLevel.height; y++) {
            for (int x = 0; x < currentLevel.width; x++) {
                char gameplayChar = currentLevel.gameplayLayer[y][x];
                if (gameplayChar != ' ') {
                    createEntityFromChar(gameplayChar, x, y, false);
                }
                char ruleChar = currentLevel.ruleLayer[y][x];
                if (ruleChar != ' ') {
                    createEntityFromChar(ruleChar, x, y, true);
                }
            }
        }
    }

    // Create an entity based on a character from the level map
    private void createEntityFromChar(char c, int x, int y, boolean isRuleLayer) {
        EntityBlueprint blueprint = charMap.get(c);
        if (blueprint != null) {
            int entityId = entityManager.createEntity();
            entityManager.addComponent(entityId, new PositionComponent(x, y));
            String fullPath = "resources/images/" + blueprint.spritePath;

            // Sprite loading logic remains the same
            try {
                if (c == 'b' || c == 'B') {
                    Texture texture = new Texture(fullPath);
                    SpriteComponent sprite = new SpriteComponent(texture, blueprint.spritePath);
                    entityManager.addComponent(entityId, sprite);
                } else {
                    Animation animation = new Animation(fullPath, 3, 200);
                    entityManager.addComponent(entityId, new SpriteComponent(animation, blueprint.spritePath));
                }
            } catch (Exception e) {
                System.err.println("Failed to load texture: " + blueprint.spritePath);
            }

            if (blueprint.word != null) {
                entityManager.addComponent(entityId, new NameComponent(blueprint.word));
            }

            if (blueprint.isText) {
                RuleComponent.Type type;
                switch (blueprint.word) {
                    case "Is": type = RuleComponent.Type.OPERATOR; break;
                    case "Push":
                    case "Stop":
                    case "Win":
                    case "You":
                    case "Sink":
                    case "Kill":
                        type = RuleComponent.Type.PROPERTY; break;
                    default:
                        type = RuleComponent.Type.SUBJECT; break;
                }
                entityManager.addComponent(entityId, new RuleComponent(blueprint.word, type));
                ruleSystem.activeRules.computeIfAbsent(blueprint.word, k -> new HashSet<>()).add("Push");
            }
        }
    }

    // Initialize texture tints for rendering
    private void initializeTextureTints() {
        textureTints.put("hedge.png", new Color(0.0f, 0.6f, 0.0f));
        textureTints.put("wall.png", new Color(0.4f, 0.3f, 0.2f));
        textureTints.put("rock.png", new Color(0.5f, 0.25f, 0.1f));
        textureTints.put("flag.png", new Color(1.0f, 0.84f, 0.0f));
        textureTints.put("grass.png", new Color(0.0f, 0.8f, 0.0f));
        textureTints.put("water.png", new Color(0.0f, 0.4f, 1.0f));
        textureTints.put("lava.png", new Color(1.0f, 0.5f, 0.0f));
    }

    // Initialize the character-to-entity mapping
    private void initializeCharMap() {
        charMap.put('h', new EntityBlueprint("hedge.png", false, "Hedge"));
        charMap.put('w', new EntityBlueprint("wall.png", false, "Wall"));
        charMap.put('r', new EntityBlueprint("rock.png", false, "Rock"));
        charMap.put('b', new EntityBlueprint("bigblue.png", false, "BigBlue"));
        charMap.put('f', new EntityBlueprint("flag.png", false, "Flag"));
        charMap.put('l', new EntityBlueprint("floor.png", false, "Floor"));
        charMap.put('g', new EntityBlueprint("grass.png", false, "Grass"));
        charMap.put('a', new EntityBlueprint("water.png", false, "Water"));
        charMap.put('v', new EntityBlueprint("lava.png", false, "Lava"));

        charMap.put('W', new EntityBlueprint("word-wall.png", true, "Wall"));
        charMap.put('R', new EntityBlueprint("word-rock.png", true, "Rock"));
        charMap.put('F', new EntityBlueprint("word-flag.png", true, "Flag"));
        charMap.put('B', new EntityBlueprint("word-bigblue.png", true, "BigBlue"));
        charMap.put('I', new EntityBlueprint("word-is.png", true, "Is"));
        charMap.put('S', new EntityBlueprint("word-stop.png", true, "Stop"));
        charMap.put('P', new EntityBlueprint("word-push.png", true, "Push"));
        charMap.put('V', new EntityBlueprint("word-lava.png", true, "Lava"));
        charMap.put('A', new EntityBlueprint("word-water.png", true, "Water"));
        charMap.put('Y', new EntityBlueprint("word-you.png", true, "You"));
        charMap.put('X', new EntityBlueprint("word-win.png", true, "Win"));
        charMap.put('N', new EntityBlueprint("word-sink.png", true, "Sink"));
        charMap.put('K', new EntityBlueprint("word-kill.png", true, "Kill"));
    }

    // Handle movement for all "you" entities
    private void handleMovement(int dx, int dy) {
        Set<Integer> youEntities = conditionSystem.getYouEntities();
        if (youEntities.isEmpty()) return;
        for (int id : youEntities) {
            String name = entityManager.getEntityName(id);
            Set<String> props = ruleSystem.activeRules.getOrDefault(name, new HashSet<>());
            System.out.println("You entity " + name + " has properties: " + props);
        }
        System.out.println("Active rules: " + ruleSystem.activeRules);
        // Save the current state for undo
        GameState currentState = entityManager.saveState();
        undoStack.push(currentState);

        boolean anyMoved = false;
        // Sort entities for consistent processing order
        List<Integer> youEntitiesList = new ArrayList<>(youEntities);
        Collections.sort(youEntitiesList);

        for (int id : youEntitiesList) {
            if (entityManager.isEntityActive(id)) {
                if (tryMoveEntity(id, dx, dy)) {
                    anyMoved = true;
                }
            }
        }

        if (anyMoved) {
            ruleSystem.update(); // Update rules after movement
            int condition = conditionSystem.checkConditions();
            if (condition == 1) {
                System.out.println("Victory!");
                loadNextLevel();
            } else if (condition == -1) {
                System.out.println("Death!");
                resetLevel();
            }
        } else {
            undoStack.pop(); // No movement occurred, discard state
        }
    }

    // Attempt to move a single entity
    private boolean tryMoveEntity(int entityId, int dx, int dy) {
        PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
        if (pos == null) return false;

        int targetX = pos.x + dx;
        int targetY = pos.y + dy;

        Set<String> pushableNames = getNamesWithProperty("Push");

        if (movementSystem.tryMove(pos.x, pos.y, dx, dy, pushableNames)) {
            pos.x = targetX;
            pos.y = targetY;
            movementSystem.checkAndApplySink(targetX, targetY);
            return true;
        }
        return false;
    }

    // Get names of entities with a specific property
    private Set<String> getNamesWithProperty(String property) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : ruleSystem.activeRules.entrySet()) {
            if (entry.getValue().contains(property)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    // Main game loop
    public void run() {
        final double targetFPS = 60.0;
        final double timePerFrame = 1.0 / targetFPS;
        lastTime = System.nanoTime();

        while (!graphics.shouldClose() && running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;

            if (deltaTime >= timePerFrame) {
                processInput(deltaTime);
                update(deltaTime);
                draw();
                lastTime = now;
            }
        }
        shutdown();
    }

    // Undo the last move
    private void undo() {
        if (!undoStack.isEmpty()) {
            GameState previousState = undoStack.pop();
            entityManager.restoreState(previousState);
            ruleSystem.update();
        }
    }

    // Process user input
    protected void processInput(double dt) {
        glfwPollEvents();

        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            running = false;
        }

        handleKeyOnce(GLFW_KEY_RIGHT, () -> handleMovement(1, 0));
        handleKeyOnce(GLFW_KEY_LEFT, () -> handleMovement(-1, 0));
        handleKeyOnce(GLFW_KEY_UP, () -> handleMovement(0, -1));
        handleKeyOnce(GLFW_KEY_DOWN, () -> handleMovement(0, 1));
        handleKeyOnce(GLFW_KEY_Z, this::undo);
        handleKeyOnce(GLFW_KEY_R, this::resetLevel);

        keysPressedLastFrame.clear();
        int[] keys = {GLFW_KEY_RIGHT, GLFW_KEY_LEFT, GLFW_KEY_UP, GLFW_KEY_DOWN, GLFW_KEY_Z, GLFW_KEY_R};
        for (int key : keys) {
            if (glfwGetKey(graphics.getWindow(), key) == GLFW_PRESS) {
                keysPressedLastFrame.add(key);
            }
        }
    }

    // Handle key presses with single-action triggering
    private void handleKeyOnce(int key, Runnable action) {
        boolean isDownNow = glfwGetKey(graphics.getWindow(), key) == GLFW_PRESS;
        boolean wasDownLastFrame = keysPressedLastFrame.contains(key);
        if (isDownNow && !wasDownLastFrame) {
            System.out.println("Key pressed: " + key); // Debug line
            action.run();
        }
        keysPressedLastFrame.add(key);
    }

    // Update game state
    protected void update(double dt) {
        for (int entityId : entityManager.getAllEntityIds()) {
            SpriteComponent sprite = entityManager.getComponent(entityId, SpriteComponent.class);
            if (sprite != null) {
                sprite.update();
            }
        }
    }

    // Render the game
    protected void draw() {
        graphics.begin();
        if (currentLevel == null) {
            graphics.end();
            return;
        }
        float gridLeft = -0.8f;
        float gridBottom = -0.8f;
        float gridWidth = 1.6f;
        float gridHeight = 1.6f;
        float tileWidth = gridWidth / currentLevel.width;
        float tileHeight = gridHeight / currentLevel.height;

        for (int entityId : entityManager.getAllEntityIds()) {
            if (entityManager.isEntityActive(entityId)) {
                PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
                SpriteComponent sprite = entityManager.getComponent(entityId, SpriteComponent.class);
                if (pos != null && sprite != null) {
                    float ndcX = gridLeft + pos.x * tileWidth;
                    float ndcY = gridBottom + pos.y * tileHeight;
                    Rectangle destinationRect = new Rectangle(ndcX, ndcY, tileWidth, tileHeight);
                    Color tint = textureTints.getOrDefault(sprite.getTexturePath(), Color.WHITE);
                    Texture texture = sprite.getTexture();
                    if (texture == null) {
                        System.err.println("Entity " + entityId + " has null texture. TexturePath: " + sprite.getTexturePath());
                    } else {
                        graphics.draw(texture, destinationRect, tint);
                    }
                }
            }
        }
        graphics.end();
    }

    // Shutdown the game
    public void shutdown() {
        System.out.println("System exiting...");
        graphics.close();
    }
}