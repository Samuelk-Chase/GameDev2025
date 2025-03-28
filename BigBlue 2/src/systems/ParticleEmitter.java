package systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleEmitter {
    private List<Particle> particles = new ArrayList<>();
    private float particleSizeNDC;

    public ParticleEmitter(float particleSizeNDC) {
        this.particleSizeNDC = particleSizeNDC;
    }

    public void emit(float x, float y, int count, Color color, float speed, float lifetime, float ax, float ay) {
        for (int i = 0; i < count; i++) {
            float angle = (float) (Math.random() * 2 * Math.PI);
            float vx = (float) Math.cos(angle) * speed;
            float vy = (float) Math.sin(angle) * speed;
            particles.add(new Particle(x, y, vx, vy, ax, ay, color, lifetime));
        }
    }

    public void addParticle(float x, float y, float vx, float vy, float ax, float ay, Color color, float lifetime) {
        particles.add(new Particle(x, y, vx, vy, ax, ay, color, lifetime));
    }

    public void update(double deltaTime) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(deltaTime);
            if (!p.isAlive()) {
                it.remove();
            }
        }
    }

    public void render(Graphics2D graphics) {
        for (Particle p : particles) {
            float alpha = p.lifetime / p.maxLifetime;
            Color renderColor = new Color(p.color.r, p.color.g, p.color.b, alpha);
            float halfSize = particleSizeNDC / 2;
            Rectangle rect = new Rectangle(
                    p.x - halfSize,
                    p.y - halfSize,
                    particleSizeNDC,
                    particleSizeNDC
            );
            graphics.draw(rect, renderColor);
        }
    }

    public boolean isActive() {
        return !particles.isEmpty();
    }
}