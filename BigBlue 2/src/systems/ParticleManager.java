package systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import systems.ParticleEmitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleManager {
    private List<ParticleEmitter> emitters = new ArrayList<>();
    private float gridLeft, gridBottom;  // Bottom-left corner of the grid in NDC
    private float tileWidth, tileHeight; // Tile dimensions in NDC
    private float particleSizeNDC;       // Particle size in NDC

    public ParticleManager(float gridLeft, float gridBottom, float tileWidth, float tileHeight, float particleSizeNDC) {
        this.gridLeft = gridLeft;
        this.gridBottom = gridBottom;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.particleSizeNDC = particleSizeNDC;
    }

    /** Creates a particle effect for object destruction, originating from the tile's center. */
    public void createDestructionEffect(float gridX, float gridY) {
        // Calculate center of the tile in NDC
        float ndcX = gridLeft + (gridX + 0.5f) * tileWidth;
        float ndcY = gridBottom + (gridY + 0.5f) * tileHeight;
        ParticleEmitter emitter = new ParticleEmitter(particleSizeNDC);
        emitter.emit(
                ndcX, ndcY,         // Position
                90,                 // Number of particles
                Color.BLACK,        // Color
                0.5f,                 // Speed (units per second in NDC)
                0.7f,               // Lifetime in seconds
                0, 0                // No acceleration
        );
        emitters.add(emitter);
    }

    /** Creates a fireworks display across the screen. */
    public void createFireworks() {
        int numFireworks = 5;
        for (int i = 0; i < numFireworks; i++) {
            float x = (float) Math.random() * 1.6f - 0.8f; // Random x between -0.8 and 0.8
            float y = -0.8f + (float) Math.random() * 0.2f; // Near bottom of screen
            ParticleEmitter emitter = new ParticleEmitter(particleSizeNDC);
            emitter.emit(
                    x, y,               // Position
                    50,                 // Number of particles
                    Color.YELLOW,       // Color
                    5,                // Speed
                    2.0f,               // Lifetime
                    0, -50              // Gravity (downward acceleration)
            );
            emitters.add(emitter);
        }
    }

    public void createSparkleEffect(float gridX, float gridY) {
        // Calculate tile boundaries in NDC (Normalized Device Coordinates)
        float left = gridLeft + gridX * tileWidth;
        float bottom = gridBottom + gridY * tileHeight;
        float right = left + tileWidth;
        float top = bottom + tileHeight;

        // Define offset to spawn particles slightly outside the tile
        float offset = 0.05f; // Adjust this value as needed

        // Calculate expanded boundaries for particle spawning
        float expandedLeft = left - offset;
        float expandedBottom = bottom - offset;
        float expandedRight = right + offset;
        float expandedTop = top + offset;


        ParticleEmitter emitter = new ParticleEmitter(particleSizeNDC);
        int count = 50;
        float speed = 0.09f;
        float lifetime = 1.5f;
        Color color = Color.YELLOW;


        for (int i = 0; i < count; i++) {
            int edge = (int) (Math.random() * 4);
            float x = 0, y = 0, vx = 0, vy = 0;

            switch (edge) {
                case 0:
                    x = expandedLeft + (float) Math.random() * (expandedRight - expandedLeft);
                    y = expandedBottom;
                    vx = (float) (Math.random() * 2 - 1) * speed;
                    vy = -(float) Math.random() * speed;
                    break;
                case 1:
                    x = expandedLeft + (float) Math.random() * (expandedRight - expandedLeft);
                    y = expandedTop;
                    vx = (float) (Math.random() * 2 - 1) * speed;
                    vy = (float) Math.random() * speed;
                    break;
                case 2:
                    x = expandedLeft;
                    y = expandedBottom + (float) Math.random() * (expandedTop - expandedBottom);
                    vx = -(float) Math.random() * speed;
                    vy = (float) (Math.random() * 2 - 1) * speed;
                    break;
                case 3:
                    x = expandedRight;
                    y = expandedBottom + (float) Math.random() * (expandedTop - expandedBottom);
                    vx = (float) Math.random() * speed;
                    vy = (float) (Math.random() * 2 - 1) * speed;
                    break;
            }
            emitter.addParticle(x, y, vx, vy, 0, 0, color, lifetime);
        }

        emitters.add(emitter);
    }

    public void update(double deltaTime) {
        Iterator<ParticleEmitter> it = emitters.iterator();
        while (it.hasNext()) {
            ParticleEmitter emitter = it.next();
            emitter.update(deltaTime);
            if (!emitter.isActive()) {
                it.remove();
            }
        }
    }

    public void render(Graphics2D graphics) {
        for (ParticleEmitter emitter : emitters) {
            emitter.render(graphics);
        }
    }
}