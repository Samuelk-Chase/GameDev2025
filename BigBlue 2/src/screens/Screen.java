package screens;

import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public abstract class Screen {

    protected final KeyboardHandler keyboardHandler;
    protected final Graphics2D graphics;
    protected Screen nextScreen = this;
    protected Screen backScreen = this;
    protected final float left = -1f;
    protected final float width = 2f;
    protected final float height =  820f/1024f * width;
    protected final float top = 0 - height/2f;
    protected final Rectangle screenRectangle = new Rectangle(left, top, width, height);
    protected Boolean pauseInput = false;

    public Screen(Graphics2D graphics) {
        this.keyboardHandler = new KeyboardHandler(graphics.getWindow());
        forceAction(GLFW_KEY_ESCAPE, (_) -> this.nextScreen = this.backScreen);
        this.graphics = graphics;
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
