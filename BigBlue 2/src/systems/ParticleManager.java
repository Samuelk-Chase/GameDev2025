package systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleManager {
    private List<ParticleEmitter> emitters = new ArrayList<>();
    private float gridLeft, gridBottom;
    private float tileWidth, tileHeight;
    private float particleSizeNDC;

    public ParticleManager(float gridLeft, float gridBottom, float tileWidth, float tileHeight, float particleSizeNDC) {
        this.gridLeft = gridLeft;
        this.gridBottom = gridBottom;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.particleSizeNDC = particleSizeNDC;
    }

    /** Creates a particle effect for object destruction, originating from the tile's center. */
    public void createDestructionEffect(float gridX, float gridY) {
        float x = gridLeft + (gridX + 0.5f) * tileWidth;
        float y = gridBottom + (gridY + 0.5f) * tileHeight;
        Texture skull = new Texture("resources/images/skull.png");
        ParticleEmitter emitter = new ParticleEmitter(particleSizeNDC);
        Random random = new Random();
        int particleCount = (int) random.nextGaussian(4, 1);
        for (int i = 0; i < particleCount; i++) {
            float velocity = (float) random.nextGaussian(0.02, 0.01);
            float size = (float) random.nextGaussian(tileWidth / 3f, tileWidth / 15f);
            if (size < 0) {
                size = -size + 0.01f;
            }
            double angle = random.nextDouble(0,2 * Math.PI);
            float vx = (float) (Math.cos(angle) * velocity);
            float vy = (float) (Math.sin(angle) * velocity);
            float lifeTime = (float) random.nextGaussian(1.75, 0.25);
            Color color = new Color(random.nextFloat(0.5f, 0.9f), random.nextFloat(0f, 0.2f), random.nextFloat(0f, 0.2f));
            emitter.addParticle(x, y, vx, vy, 0, 0, color, lifeTime, skull, size);
        }
        emitters.add(emitter);
    }

    public void clear() {
        for (ParticleEmitter emitter : emitters) {
            emitter.clear();
        }
        emitters.clear();
    }

    /** Creates a fireworks display across the screen. */
    public void createFireworks(float screenWidth, float screenHeight) {
        int numFireworks = 20;
        Random random = new Random();
        Texture star = new Texture("resources/images/star.png");
        ParticleEmitter emitter = new ParticleEmitter(particleSizeNDC);
        for (int i = 0; i < numFireworks; i++) {
            int particleCount = (int) random.nextGaussian(150, 50);
            float centerX = (float) random.nextGaussian(0, screenWidth / 4f);
            float centerY = (float) random.nextGaussian(0, screenHeight / 4f);
            float averageVelocity = (float) random.nextGaussian(0.2, 0.1);
            float averageSize = (float) random.nextGaussian(0.02, 0.01);
            if (averageSize < 0) {
                averageSize = -averageSize + 0.01f;
            }
            Color color = new Color(random.nextFloat(0.5f, 0.9f), random.nextFloat(0.5f, 0.9f), random.nextFloat(0.5f, 0.9f));
            for (int j = 0; j < particleCount; j++) {
                double angle = random.nextDouble(0,2 * Math.PI);
                float velocity = (float) random.nextGaussian(averageVelocity, 0.05);
                float size = (float) random.nextGaussian(averageSize, averageSize/4);
                float acceleration = (float) random.nextGaussian(0.075, 0.1);
                float velX = (float) (Math.cos(angle) * velocity);
                float velY = (float) (Math.sin(angle) * velocity);
                float accX = (float) (Math.cos(angle) * acceleration);
                float accY = (float) (Math.sin(angle) * acceleration);
                float lifeTime = (float) random.nextGaussian(1.75, 0.25);
                emitter.addParticle(centerX, centerY, velX, velY, accX, accY, color, lifeTime, star, size);
            }
        }
        emitters.add(emitter);
    }

    public void createSparkleEffect(float gridX, float gridY) {
        float left = gridLeft + gridX * tileWidth;
        float bottom = gridBottom + gridY * tileHeight;
        float right = left + tileWidth;
        float top = bottom + tileHeight;

        System.out.println("What");
        Texture star = new Texture("resources/images/star.png");

        float offset = -0.005f;

        float expandedLeft = left - offset;
        float expandedBottom = bottom - offset;
        float expandedRight = right + offset;
        float expandedTop = top + offset;

        ParticleEmitter emitter = new ParticleEmitter(particleSizeNDC);
        int count = 50;
        Color color = Color.YELLOW;
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            float lifetime = (float) random.nextGaussian(0.75f, 0.1f);
            float speed = (float) random.nextGaussian(0.01f, 0.01f);
            int edge = (int) (Math.random() * 4);
            float x, y;
            double angle = random.nextGaussian(Math.PI / 2, Math.PI / 16f);
            switch (edge) {
                case 0:
                    x = expandedLeft + (float) Math.random() * (expandedRight - expandedLeft);
                    y = expandedBottom;
                    angle += Math.PI;
                    break;
                case 1:
                    x = expandedLeft + (float) Math.random() * (expandedRight - expandedLeft);
                    y = expandedTop;
                    break;
                case 2:
                    x = expandedLeft;
                    y = expandedBottom + (float) Math.random() * (expandedTop - expandedBottom);
                    angle += Math.PI / 2;
                    break;
                default:
                    x = expandedRight;
                    y = expandedBottom + (float) Math.random() * (expandedTop - expandedBottom);
                    angle -= Math.PI / 2;
                    break;
            }
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);
            emitter.addParticle(x, y, vx, vy, 0, 0, color, lifetime, star);
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