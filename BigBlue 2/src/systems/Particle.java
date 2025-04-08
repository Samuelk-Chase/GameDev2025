package systems;

import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;

public class Particle {
    public float x, y;
    public float vx, vy;
    public float ax, ay;
    public Color color;
    public float lifetime;
    public float maxLifetime;
    public Texture texture;
    public Float size;

    public Particle(float x, float y, float vx, float vy, float ax, float ay, Color color, float lifetime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
        this.color = color;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
    }

    public Particle(float x, float y, float vx, float vy, float ax, float ay, Color color, float lifetime, Texture texture) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
        this.color = color;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.texture = texture;
    }

    public Particle(float x, float y, float vx, float vy, float ax, float ay, Color color, float lifetime, Texture texture, float size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
        this.color = color;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.texture = texture;
        this.size = size;
    }

    public void update(double deltaTime) {
        vx += ax * (float) deltaTime;
        vy += ay * (float) deltaTime;
        x += vx * (float) deltaTime;
        y += vy * (float) deltaTime;
        lifetime -= (float) deltaTime;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
}