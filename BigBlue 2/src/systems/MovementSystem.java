package systems;
import entities.EntityManager;
import components.PositionComponent;

public class MovementSystem {
    private EntityManager entityManager;

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void move(int entityId, int dx, int dy) {
        PositionComponent pos = entityManager.getComponent(entityId, PositionComponent.class);
        if (pos != null) {
            pos.x += dx;
            pos.y += dy;
        }
    }
}