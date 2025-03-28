package systems;

import edu.usu.graphics.Color;

public class Particle {
    public float x, y;
    public float vx, vy;
    public float ax, ay;
    public Color color;
    public float lifetime;
    public float maxLifetime;

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