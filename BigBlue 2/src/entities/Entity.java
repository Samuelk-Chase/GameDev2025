package entities;

public class Entity {
    private final int id;
    private static int idCounter = 0;

    public Entity() {
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

}