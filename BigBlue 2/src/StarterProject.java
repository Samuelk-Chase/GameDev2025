import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

public class StarterProject {
    public static void main(String[] args) {
        try (Graphics2D graphics = new Graphics2D(1024, 820, "Big Blue - Level Test")) {
            graphics.initialize(Color.BLACK);

            Game game = new Game(graphics);
            game.initialize();
            game.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}