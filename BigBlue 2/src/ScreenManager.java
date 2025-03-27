import edu.usu.graphics.Graphics2D;
import screens.Button;
import screens.GameplayScreen;
import screens.MenuScreen;
import screens.Screen;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;

public class ScreenManager {
    private double lastTime;
    private final Graphics2D graphics;
    private Screen currentScreen;
    private GameplayScreen gameScreen;

    public ScreenManager(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
//        Load Levels
//        Create Screens
        createScreens();
//        Setup Screen actions and connections
    }

    public void createScreens() {
        MenuScreen mainMenu = new MenuScreen(graphics);
        mainMenu.forceAction(GLFW_KEY_ESCAPE, (_) -> glfwSetWindowShouldClose(graphics.getWindow(), true));
        currentScreen = mainMenu;
        MenuScreen pauseMenu = new MenuScreen(graphics);
        MenuScreen levelSelectionScreen = new MenuScreen(graphics);
        MenuScreen controlsScreen = new MenuScreen(graphics);
        GameplayScreen gameplayScreen = new GameplayScreen(graphics);
        mainMenu.addButtons(0.25f, new MenuScreen.ButtonBundle[] {
            new MenuScreen.ButtonBundle("Play", (_) -> mainMenu.setNextScreen(levelSelectionScreen)),
            new MenuScreen.ButtonBundle("Controls", (_) -> mainMenu.setNextScreen(controlsScreen)),
            new MenuScreen.ButtonBundle("Exit", (_) -> glfwSetWindowShouldClose(graphics.getWindow(), true))
        });
        MenuScreen.ButtonBundle[] levelButtons = new MenuScreen.ButtonBundle[gameplayScreen.getNumLevels() + 1];
        for (int i = 0; i < levelButtons.length - 1; i++) {
            int finalI = i;
            levelButtons[i] = new MenuScreen.ButtonBundle("Level " + (i + 1), (_) -> {gameplayScreen.setLevel(finalI); levelSelectionScreen.setNextScreen(gameplayScreen);});
        }
        levelButtons[levelButtons.length - 1] = new MenuScreen.ButtonBundle("Back", (_) -> levelSelectionScreen.setNextScreen(levelSelectionScreen.getBackScreen()));
        levelSelectionScreen.addButtons(0.2f, levelButtons);
        pauseMenu.addButtons(0.25f, new MenuScreen.ButtonBundle[] {
            new MenuScreen.ButtonBundle("Main Menu", (_) -> pauseMenu.setNextScreen(mainMenu)),
            new MenuScreen.ButtonBundle("Resume", (_) -> pauseMenu.setNextScreen(gameplayScreen)),
            new MenuScreen.ButtonBundle("Controls", (_) -> pauseMenu.setNextScreen(controlsScreen)),
        });
        controlsScreen.addButtons(0.25f, new MenuScreen.ButtonBundle[]{
                new MenuScreen.ButtonBundle("Back", (_) -> controlsScreen.setNextScreen(controlsScreen.getBackScreen()))
        });
        gameplayScreen.forceAction(GLFW_KEY_ESCAPE, (_) -> gameplayScreen.setNextScreen(pauseMenu));
        this.gameScreen = gameplayScreen;
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
