package screens;

import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public abstract class Screen {

    protected final KeyboardHandler keyboardHandler;
    protected final Graphics2D graphics;
    protected Screen nextScreen = this;
    protected Screen backScreen = this;
    protected static float left;
    protected static float width;
    protected static float height;
    protected static float top;
    public static Rectangle screenRectangle;
    protected Boolean pauseInput = false;

    public Screen(Graphics2D graphics) {
        this.keyboardHandler = new KeyboardHandler(graphics.getWindow());
        forceAction(GLFW_KEY_ESCAPE, (_) -> this.nextScreen = this.backScreen);
        this.graphics = graphics;
    }

    public static void setDimensions(int pixelWidth, int pixelHeight) {
        float totalWidth = 2f;
        float totalLeft = -1f;
        float totalHeight = totalWidth / pixelWidth * pixelHeight;
        float totalTop = totalHeight / -2f;
        width = Math.min(totalWidth, totalHeight);
        left = totalLeft + (totalWidth - width) / 2f;
        height = width;
        top = totalTop + (totalHeight - height) / 2f;
        screenRectangle = new Rectangle(left, top, width, height);
    }

    public void addAction(Integer key, KeyboardHandler.KeyAction action) throws Exception {
        keyboardHandler.addAction(key, action);
    }

    public void forceAction(Integer key, KeyboardHandler.KeyAction action) {
        this.keyboardHandler.setAction(key, action);
    }

    public Boolean getPauseInput() {
        return pauseInput;
    }

    public void setNextScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;
        nextScreen.setBackScreen(this);
    }


    public Screen getNextScreen() {
        Screen nextScreen = this.nextScreen;
        this.nextScreen = this;
        return nextScreen;
    }

    public Screen getBackScreen() {
        return backScreen;
    }

    public void setBackScreen(Screen backScreen) {
        this.backScreen = backScreen;
    }

    public abstract void setLoadValues();

    public boolean changeScreen() {
        if (nextScreen != this) {
            nextScreen.keyboardHandler.copyPressed(keyboardHandler);
            return true;
        }
        return false;
    }

    public abstract void processInput();

    public void update(double elapsedTime) {
        if (!pauseInput) keyboardHandler.update(elapsedTime);
        screenUpdate(elapsedTime);
    }

    public abstract void screenUpdate(double elapsedTime);

    public abstract void render();
}
