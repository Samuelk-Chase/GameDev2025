package serializer;

import com.google.gson.Gson;
import screens.KeyboardHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Serializer implements Runnable {

    private enum Activity {
        Nothing,
        Load,
        Save
    }

    protected KeyboardHandler keyboardHandler;

    private boolean done = false;
    private final Lock lockSignal = new ReentrantLock();
    private final Condition doSomething = lockSignal.newCondition();

    private final String fileName = "keyboard.json";

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

    public void saveControls(KeyboardHandler keyboardHandler) {
        lockSignal.lock();
        doThis = Activity.Save;
        this.keyboardHandler = keyboardHandler;
        doSomething.signal();
        lockSignal.unlock();
    }

    public void loadControls(KeyboardHandler keyboardHandler) {
        lockSignal.lock();
        doThis = Activity.Load;
        this.keyboardHandler = keyboardHandler;
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
            gson.toJson(this.keyboardHandler, writer);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private synchronized void loadSomething() {
        try (FileReader reader = new FileReader(fileName)) {
            KeyboardHandler loadedKeyboardHandler = (new Gson()).fromJson(reader, KeyboardHandler.class);
            this.keyboardHandler.copyKeyboardHandler(loadedKeyboardHandler);
        } catch(FileNotFoundException e) {
            try (FileWriter writer = new FileWriter(fileName)) {
                Gson gson = new Gson();
                gson.toJson(this.keyboardHandler, writer);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
