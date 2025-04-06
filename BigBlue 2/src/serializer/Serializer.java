package serializer;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.lwjgl.glfw.GLFW.*;

public class Serializer implements Runnable {

    private enum Activity {
        Nothing,
        Load,
        Save
    }

    protected ControlConfiguration controlConfiguration;

    private boolean done = false;
    private final Lock lockSignal = new ReentrantLock();
    private final Condition doSomething = lockSignal.newCondition();

    private final String fileName = "controls.json";

    private Activity doThis = Activity.Nothing;

    private final Thread thread;

    public Serializer() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while(!done) {
                lockSignal.lock();
                doSomething.await();
                lockSignal.unlock();

                switch (doThis) {
                    case Activity.Nothing -> {}
                    case Activity.Save -> saveSomething();
                    case Activity.Load -> loadSomething();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void saveControls(ControlConfiguration controlConfiguration) {
        lockSignal.lock();
        doThis = Activity.Save;
        this.controlConfiguration = controlConfiguration;
        doSomething.signal();
        lockSignal.unlock();
    }

    public void loadControls(ControlConfiguration controlConfiguration) {
        lockSignal.lock();
        doThis = Activity.Load;
        this.controlConfiguration = controlConfiguration;
        doSomething.signal();
        lockSignal.unlock();
    }

    public void shutdown() {
        try {
            lockSignal.lock();
            doThis = Activity.Nothing;
            done = true;
            doSomething.signal();
            lockSignal.unlock();
            thread.join();
        } catch (Exception ex) {
            System.out.printf("Failure to gracefully shutdown thread: %s\n", ex.getMessage());
        }
    }

    private synchronized void saveSomething() {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new Gson();
            gson.toJson(this.controlConfiguration, writer);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private synchronized void loadSomething() {
        try (FileReader reader = new FileReader(fileName)) {
            ControlConfiguration loadedControlConfiguration = (new Gson()).fromJson(reader, ControlConfiguration.class);
            this.controlConfiguration.copyControlConfiguration(loadedControlConfiguration);
        } catch(FileNotFoundException e) {
            try (FileWriter writer = new FileWriter(fileName)) {
                this.controlConfiguration.setKey(GLFW_KEY_W, ControlConfiguration.Action.UP);
                this.controlConfiguration.setKey(GLFW_KEY_S, ControlConfiguration.Action.DOWN);
                this.controlConfiguration.setKey(GLFW_KEY_A, ControlConfiguration.Action.LEFT);
                this.controlConfiguration.setKey(GLFW_KEY_D, ControlConfiguration.Action.RIGHT);
                this.controlConfiguration.setKey(GLFW_KEY_Z, ControlConfiguration.Action.UNDO);
                this.controlConfiguration.setKey(GLFW_KEY_R, ControlConfiguration.Action.RESTART);
                Gson gson = new Gson();
                gson.toJson(this.controlConfiguration, writer);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
