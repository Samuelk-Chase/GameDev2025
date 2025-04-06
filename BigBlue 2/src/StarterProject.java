import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;

public class StarterProject {
    public static void main(String[] args) {
        int width = 912;
        int height = 513;
        screens.Screen.setDimensions(width, height);
        try (Graphics2D graphics = new Graphics2D(width, height, "Big Blue - Level Test")) {
            graphics.initialize(Color.BLACK);
            ScreenManager game = new ScreenManager(graphics);
            glfwSetErrorCallback((_, _) -> {game.shutdown();});
            game.initialize();
            game.run();
            game.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}