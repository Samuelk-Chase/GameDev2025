package screens;

import components.NameComponent;
import components.PositionComponent;
import components.RuleComponent;
import components.SpriteComponent;
import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;
import entities.EntityBlueprint;
import entities.EntityManager;
import entities.GameState;
import serializer.ControlConfiguration;
import serializer.Serializer;
import systems.Animation;
import systems.ConditionSystem;
import systems.MovementSystem;
import systems.RuleSystem;
import util.ParseLevel;
import systems.ParticleManager;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class GameplayScreen extends Screen {
    private List<ParseLevel.LevelData> levels;
    private ParseLevel.LevelData currentLevel;
    private EntityManager entityManager;
    private Map<Character, EntityBlueprint> charMap;
    private int currentLevelIndex = 0;
    private ControlConfiguration controlConfiguration;
    private final Map<String, Color> textureTints = new HashMap<>();
    private Set<Integer> keysPressedLastFrame = new HashSet<>();
    private final HashMap<ControlConfiguration.Action, KeyboardHandler.KeyAction> controlActions = new HashMap<>();
    private RuleSystem ruleSystem;
    private MovementSystem movementSystem;
    private ConditionSystem conditionSystem;
    private final Stack<GameState> undoStack = new Stack<>();
    private float gridLeft = -0.8f;
    private float gridBottom = -0.8f;
    private float gridWidth = 1.6f;
    private float gridHeight = 1.6f;
    private float tileWidth;
    private float tileHeight;
    private ParticleManager particleManager;
    private Set<Integer> previousYouEntities = new HashSet<>();
    private Set<Integer> previousWinEntities = new HashSet<>();
    private MenuScreen mainMenu; // Reference to the main menu screen
    private SoundManager soundManager;
    private Sound moveSound;
    private Sound winSound;
    private Sound isWinSound;

    public GameplayScreen(Graphics2D graphics, MenuScreen mainMenu, ControlConfiguration controlConfiguration) {
        super(graphics);
        this.mainMenu = mainMenu; // Store the main menu reference
        this.charMap = new HashMap<>();
        this.entityManager = new EntityManager(this);
        this.ruleSystem = new RuleSystem(entityManager);
        this.movementSystem = new MovementSystem(entityManager);
        this.conditionSystem = new ConditionSystem(entityManager, ruleSystem);
        this.controlConfiguration = controlConfiguration;
        controlActions.put(ControlConfiguration.Action.RIGHT, (_) -> handleMovement(1, 0));
        controlActions.put(ControlConfiguration.Action.LEFT, (_) -> handleMovement(-1, 0));
        controlActions.put(ControlConfiguration.Action.UP, (_) -> handleMovement(0, -1));
        controlActions.put(ControlConfiguration.Action.DOWN, (_) -> handleMovement(0, 1));
        controlActions.put(ControlConfiguration.Action.UNDO, (_) -> undo());
        controlActions.put(ControlConfiguration.Action.RESTART, (_) -> resetLevel());
        loadLevels();
        initializeCharMap();
        initializeTextureTints();
        soundManager = new SoundManager();
        moveSound = soundManager.load("move", "resources/audio/move.ogg", false);
        winSound = soundManager.load("win", "resources/audio/win.ogg", false);
        isWinSound = soundManager.load("isWin", "resources/audio/isWin.ogg", false);
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    private void loadLevels() {
        ParseLevel parser = new ParseLevel();
        levels = parser.parseLevels("resources/levels/levels-all.bbiy");
        if (!levels.isEmpty()) {
            setLevel(levels.get(0));
        } else {
            System.err.println("No levels found!");
        }
    }

    private void setLevel(ParseLevel.LevelData level) {
        this.currentLevel = level;
        this.tileWidth = gridWidth / currentLevel.width;
        this.tileHeight = gridHeight / currentLevel.height;
        entityManager.clear();
        undoStack.clear();
        loadLevelEntities();
        ruleSystem.update();
        applyTransformations();
        particleManager = new ParticleManager(gridLeft, gridBottom, tileWidth, tileHeight, 0.02f);
        movementSystem.setParticleManager(particleManager);
    }

    public void setLevel(int levelIndex) {
        currentLevelIndex = levelIndex;
        setLevel(levels.get(levelIndex));
    }

    public int getNumLevels() {
        return levels.size();
    }

    private void loadNextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.size()) {
            setLevel(levels.get(currentLevelIndex));
        } else {
            setNextScreen(mainMenu); // Transition to main menu when no more levels
        }
    }

    private void resetLevel() {
        setLevel(levels.get(currentLevelIndex));
    }

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

    private void createEntityFromChar(char c, int x, int y, boolean isRuleLayer) {
        EntityBlueprint blueprint = charMap.get(c);
        if (blueprint != null) {
            int entityId = entityManager.createEntity();
            entityManager.addComponent(entityId, new PositionComponent(x, y));
            String fullPath = "resources/images/" + blueprint.spritePath;

            try {
                SpriteComponent sprite;
                if (c == 'b' || c == 'B') {
                    // BigBlue: assign high Z-index
                    Texture texture = new Texture(fullPath);
                    sprite = new SpriteComponent(texture, blueprint.spritePath);
                    sprite.setZIndex(10);
                } else {
                    // Other entities: use an animation and default z-index
                    Animation animation = new Animation(fullPath, 3, 200);
                    sprite = new SpriteComponent(animation, blueprint.spritePath);
                    // If it's Floor, set it to the back (-1), otherwise default to 0.
                    if (blueprint.word.equals("Floor")) {
                        sprite.setZIndex(-1);
                    } else {
                        sprite.setZIndex(0);
                    }
                }
                entityManager.addComponent(entityId, sprite);
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

    private void applyTransformations() {
        for (int entityId : entityManager.getAllEntityIds()) {
            if (entityManager.isEntityActive(entityId)) {
                RuleComponent ruleComp = entityManager.getComponent(entityId, RuleComponent.class);
                if (ruleComp == null) {
                    NameComponent nameComp = entityManager.getComponent(entityId, NameComponent.class);
                    if (nameComp != null) {
                        String currentName = nameComp.name;
                        String targetName = ruleSystem.getFinalTransformation(currentName);
                        if (!targetName.equals(currentName)) {
                            transformEntity(entityId, targetName);
                        }
                    }
                }
            }
        }
    }

    private void transformEntity(int entityId, String targetName) {
        NameComponent nameComp = entityManager.getComponent(entityId, NameComponent.class);
        if (nameComp != null) {
            nameComp.name = targetName;
        }

        SpriteComponent spriteComp = entityManager.getComponent(entityId, SpriteComponent.class);
        if (spriteComp != null) {
            String spritePath = targetName.toLowerCase() + ".png";
            String fullPath = "resources/images/" + spritePath;
            try {
                if (targetName.equals("BigBlue")) {
                    Texture texture = new Texture(fullPath);
                    spriteComp.setTexture(texture, spritePath);
                    spriteComp.setZIndex(10);
                } else if (targetName.equals("Floor")) {
                    Animation animation = new Animation(fullPath, 3, 200);
                    spriteComp.setAnimation(animation, spritePath);
                    spriteComp.setZIndex(-1);
                } else {
                    Animation animation = new Animation(fullPath, 3, 200);
                    spriteComp.setAnimation(animation, spritePath);
                    spriteComp.setZIndex(0);
                }
            } catch (Exception e) {
                // Handle exception if needed
            }
        }
    }

    private void initializeTextureTints() {
        textureTints.put("hedge.png", new Color(0.0f, 0.6f, 0.0f));
        textureTints.put("wall.png", new Color(0.4f, 0.3f, 0.2f));
        textureTints.put("rock.png", new Color(0.5f, 0.25f, 0.1f));
        textureTints.put("flag.png", new Color(1.0f, 0.84f, 0.0f));
        textureTints.put("grass.png", new Color(0.0f, 0.8f, 0.0f));
        textureTints.put("water.png", new Color(0.0f, 0.4f, 1.0f));
        textureTints.put("lava.png", new Color(1.0f, 0.5f, 0.0f));
        textureTints.put("word-wall.png", new Color(0.8f, 0.6f, 0.4f));
        textureTints.put("word-rock.png", new Color(0.6f, 0.5f, 0.5f));
        textureTints.put("word-flag.png", new Color(1.0f, 0.9f, 0.4f));
        textureTints.put("word-bigblue.png", new Color(0.4f, 0.6f, 1.0f));
        textureTints.put("word-is.png", new Color(1.0f, 1.0f, 1.0f));
        textureTints.put("word-stop.png", new Color(1.0f, 0.4f, 0.4f));
        textureTints.put("word-push.png", new Color(0.6f, 0.6f, 1.0f));
        textureTints.put("word-lava.png", new Color(1.0f, 0.6f, 0.4f));
        textureTints.put("word-water.png", new Color(0.4f, 0.8f, 1.0f));
        textureTints.put("word-you.png", new Color(0.8f, 0.4f, 1.0f));
        textureTints.put("word-win.png", new Color(1.0f, 1.0f, 0.6f));
        textureTints.put("word-sink.png", new Color(0.5f, 0.5f, 0.7f));
        textureTints.put("word-kill.png", new Color(0.9f, 0.2f, 0.2f));
    }

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

    private void handleMovement(int dx, int dy) {
        Set<Integer> youEntities = conditionSystem.getYouEntities();
        if (youEntities.isEmpty()) return;
        for (int id : youEntities) {
            String name = entityManager.getEntityName(id);
            Set<String> props = ruleSystem.activeRules.getOrDefault(name, new HashSet<>());
            System.out.println("You entity " + name + " has properties: " + props);
        }
        System.out.println("Active rules: " + ruleSystem.activeRules);
        GameState currentState = entityManager.saveState();
        undoStack.push(currentState);

        boolean anyMoved = false;
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
            // Save previous YOU and WIN entities
            previousYouEntities.clear();
            previousYouEntities.addAll(conditionSystem.getYouEntities());
            previousWinEntities.clear();
            previousWinEntities.addAll(conditionSystem.getWinEntities());

            ruleSystem.update();
            applyTransformations();

            // Detect new YOU and WIN entities
            Set<Integer> newYouEntities = conditionSystem.getYouEntities();
            Set<Integer> newWinEntities = conditionSystem.getWinEntities();

            // Find newly added entities
            Set<Integer> addedYouEntities = new HashSet<>(newYouEntities);
            addedYouEntities.removeAll(previousYouEntities);
            Set<Integer> addedWinEntities = new HashSet<>(newWinEntities);
            addedWinEntities.removeAll(previousWinEntities);

            // Trigger sparkle effects for new YOU and WIN tiles
            for (int id : addedYouEntities) {
                PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);
                if (pos != null) {
                    particleManager.createSparkleEffect(pos.x, pos.y);
                }
            }
            for (int id : addedWinEntities) {
                PositionComponent pos = entityManager.getComponent(id, PositionComponent.class);
                if (pos != null) {
                    particleManager.createSparkleEffect(pos.x, pos.y);
                    if (!isWinSound.isPlaying()) {
                        isWinSound.play();
                    }
                }
            }

            int condition = conditionSystem.checkConditions();
            if (condition == 1) {
                System.out.println("Victory!");
                particleManager.createFireworks();
                if (!winSound.isPlaying()) {
                    winSound.play();
                }
                loadNextLevel();
            }
        } else {
            undoStack.pop();
        }
    }

    public void onEntityRemoved(float gridX, float gridY) {
        particleManager.createDestructionEffect(gridX, gridY);
    }

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
            if (!moveSound.isPlaying()) {
                moveSound.play();
            }
            return true;
        }
        return false;
    }

    private Set<String> getNamesWithProperty(String property) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : ruleSystem.activeRules.entrySet()) {
            if (entry.getValue().contains(property)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            GameState previousState = undoStack.pop();
            entityManager.restoreState(previousState);
            ruleSystem.update();
        }
    }

    @Override
    public void setLoadValues() {
    }

    @Override
    public void processInput() {
        Set<Integer> prevFrame = keysPressedLastFrame;
        keysPressedLastFrame = new HashSet<>();
        for (Integer key : controlConfiguration.getKeys()) {
            if (glfwGetKey(graphics.getWindow(), key) == GLFW_PRESS) {
                if (!prevFrame.contains(key)) {
                    controlActions.get(controlConfiguration.getAction(key)).run(0);
                }
                keysPressedLastFrame.add(key);
            }
        }
    }

    @Override
    public void screenUpdate(double elapsedTime) {
        for (int entityId : entityManager.getAllEntityIds()) {
            SpriteComponent sprite = entityManager.getComponent(entityId, SpriteComponent.class);
            if (sprite != null) {
                sprite.update();
            }
        }
        particleManager.update(elapsedTime);
    }

    @Override
    public void render() {
        float gridLeft = -0.8f;
        float gridBottom = -0.8f;
        float gridWidth = 1.6f;
        float gridHeight = 1.6f;
        float tileWidth = gridWidth / currentLevel.width;
        float tileHeight = gridHeight / currentLevel.height;

        List<Integer> entities = new ArrayList<>(entityManager.getAllEntityIds());
        entities.sort((id1, id2) -> {
            SpriteComponent s1 = entityManager.getComponent(id1, SpriteComponent.class);
            SpriteComponent s2 = entityManager.getComponent(id2, SpriteComponent.class);
            int z1 = s1 != null ? s1.getZIndex() : 0;
            int z2 = s2 != null ? s2.getZIndex() : 0;
            return Integer.compare(z1, z2);
        });

        for (int entityId : entities) {
            if (entityManager.isEntityActive(entityId)) {
                PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
                SpriteComponent sprite = entityManager.getComponent(entityId, SpriteComponent.class);
                if (pos != null && sprite != null) {
                    float ndcX = gridLeft + pos.x * tileWidth;
                    float ndcY = gridBottom + pos.y * tileHeight;
                    Rectangle destinationRect = new Rectangle(ndcX, ndcY, tileWidth, tileHeight);
                    Color tint = textureTints.getOrDefault(sprite.getTexturePath(), Color.WHITE);
                    Texture texture = sprite.getTexture();
                    if (texture != null) {
                        graphics.draw(texture, destinationRect, tint);
                    }
                }
            }
        }
        particleManager.render(graphics);
    }

    public void dispose() {
        soundManager.cleanup();
    }
}