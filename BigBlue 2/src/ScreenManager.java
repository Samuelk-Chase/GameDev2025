import edu.usu.graphics.Graphics2D;
import screens.*;
import serializer.ControlConfiguration;
import serializer.Serializer;

import static org.lwjgl.glfw.GLFW.*;

public class ScreenManager {
    private double lastTime;
    private final Graphics2D graphics;
    private Screen currentScreen;
    private Serializer serializer;

    public ScreenManager(Graphics2D graphics) {
        serializer = new Serializer();
        this.graphics = graphics;
    }

    public void initialize() {
        createScreens();
    }

    public void createScreens() {
        MenuScreen mainMenu = new MenuScreen(graphics);
        mainMenu.forceAction(GLFW_KEY_ESCAPE, (_) -> glfwSetWindowShouldClose(graphics.getWindow(), true));
        currentScreen = mainMenu;
        MenuScreen pauseMenu = new MenuScreen(graphics);
        MenuScreen levelSelectionScreen = new MenuScreen(graphics);
        MenuScreen controlsScreen = new MenuScreen(graphics);
        ControlConfiguration controlConfiguration = new ControlConfiguration();
        serializer.loadControls(controlConfiguration);
        GameplayScreen gameplayScreen = new GameplayScreen(graphics, controlConfiguration);
        MenuScreen creditsScreen = new MenuScreen(graphics);
        mainMenu.addButtons(0.25f, new MenuScreen.ButtonBundle[] {
          new MenuScreen.ButtonBundle("Play", MenuButton.makeCreator((_) -> {
                  levelSelectionScreen.setBackScreen(mainMenu);
                  mainMenu.setNextScreen(levelSelectionScreen);
              })
          ),
          new MenuScreen.ButtonBundle("Controls", MenuButton.makeCreator((_) -> {
                  controlsScreen.setBackScreen(mainMenu);
                  mainMenu.setNextScreen(controlsScreen);
              })
          ),
          new MenuScreen.ButtonBundle("Credits", MenuButton.makeCreator((_) -> {
                  creditsScreen.setBackScreen(mainMenu);
                  mainMenu.setNextScreen(creditsScreen);
              })
          ),
          new MenuScreen.ButtonBundle("Exit", MenuButton.makeCreator((_) -> glfwSetWindowShouldClose(graphics.getWindow(), true))
          )
        });

        MenuScreen.ButtonBundle[] levelButtons = new MenuScreen.ButtonBundle[gameplayScreen.getNumLevels() + 1];
        for (int i = 0; i < levelButtons.length - 1; i++) {
            int finalI = i;
            levelButtons[i] = new MenuScreen.ButtonBundle("Level " + (i + 1), MenuButton.makeCreator((_) -> {
                gameplayScreen.setLevel(finalI);
                levelSelectionScreen.setNextScreen(gameplayScreen);
            }));
        }
        levelButtons[levelButtons.length - 1] = new MenuScreen.ButtonBundle("Back", MenuButton.makeCreator((_) -> levelSelectionScreen.setNextScreen(levelSelectionScreen.getBackScreen())));
        levelSelectionScreen.addButtons(0.175f, levelButtons);
        pauseMenu.addButtons(0.25f, new MenuScreen.ButtonBundle[] {
                new MenuScreen.ButtonBundle("Main Menu", MenuButton.makeCreator((_) -> pauseMenu.setNextScreen(mainMenu))),
                new MenuScreen.ButtonBundle("Resume", MenuButton.makeCreator((_) -> pauseMenu.setNextScreen(gameplayScreen))),
                new MenuScreen.ButtonBundle("Controls", MenuButton.makeCreator((_) -> {
                    controlsScreen.setBackScreen(pauseMenu);
                    pauseMenu.setNextScreen(controlsScreen);
                })),
        });

        controlsScreen.addButtons(0.125f, new MenuScreen.ButtonBundle[]{
                new MenuScreen.ButtonBundle("Up", ControlButton.makeCreator(ControlConfiguration.Action.UP, controlConfiguration, controlsScreen.getPauseInput())),
                new MenuScreen.ButtonBundle("Down", ControlButton.makeCreator(ControlConfiguration.Action.DOWN, controlConfiguration, controlsScreen.getPauseInput())),
                new MenuScreen.ButtonBundle("Left", ControlButton.makeCreator(ControlConfiguration.Action.LEFT, controlConfiguration, controlsScreen.getPauseInput())),
                new MenuScreen.ButtonBundle("Right", ControlButton.makeCreator(ControlConfiguration.Action.RIGHT, controlConfiguration, controlsScreen.getPauseInput())),
                new MenuScreen.ButtonBundle("Undo", ControlButton.makeCreator(ControlConfiguration.Action.UNDO, controlConfiguration, controlsScreen.getPauseInput())),
                new MenuScreen.ButtonBundle("Restart", ControlButton.makeCreator(ControlConfiguration.Action.RESTART, controlConfiguration, controlsScreen.getPauseInput())),
                new MenuScreen.ButtonBundle("Back", MenuButton.makeCreator((_) -> {serializer.saveControls(controlConfiguration); controlsScreen.setNextScreen(controlsScreen.getBackScreen());}))
        });
        gameplayScreen.forceAction(GLFW_KEY_ESCAPE, (_) -> {serializer.saveControls(controlConfiguration); controlsScreen.setNextScreen(controlsScreen.getBackScreen());});

        // **Step 3: Add "Back" button to credits screen**
        creditsScreen.addButtons(0.25f, new MenuScreen.ButtonBundle[]{
                new MenuScreen.ButtonBundle("Back", MenuButton.makeCreator((_) -> creditsScreen.setNextScreen(creditsScreen.getBackScreen())))
        });
        pauseMenu.forceAction(GLFW_KEY_ESCAPE, (_) -> pauseMenu.setNextScreen(gameplayScreen));
        gameplayScreen.forceAction(GLFW_KEY_ESCAPE, (_) -> gameplayScreen.setNextScreen(pauseMenu));
    }

    public void run() {
        lastTime = glfwGetTime();
        final double targetFPS = 60.0;
        final double secondsPerFrame = 1.0 / targetFPS;
        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - lastTime;
            if (elapsedTime >= secondsPerFrame) {
                lastTime = currentTime;
                processInput();
                update(elapsedTime);
                render();
            }
        }
        System.out.println("System exiting...");
        graphics.close();
    }

    public void shutdown() {
        serializer.shutdown();
    }

    private void processInput() {
        glfwPollEvents();
        currentScreen.processInput();
    }

    private void update(Double elapsedTime) {
        currentScreen.update(elapsedTime);
        if (currentScreen.changeScreen()) {
            currentScreen = currentScreen.getNextScreen();
            currentScreen.setLoadValues();
        }
    }

    private void render() {
        graphics.begin();
        currentScreen.render();
        graphics.end();
    }
}
