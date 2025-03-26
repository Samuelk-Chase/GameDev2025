package systems;

import edu.usu.graphics.Texture;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Animation implements Cloneable {
    private Texture[] frames;
    private int currentFrame = 0;
    private long frameDelay;
    private long lastFrameTime;
    private File tempDir;

    public Animation(String imagePath, int totalFrames, long frameDelay) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(imagePath));
            int frameWidth = spriteSheet.getWidth() / totalFrames;
            int frameHeight = spriteSheet.getHeight();

            tempDir = File.createTempFile("animation_frames_", "");
            tempDir.delete();
            tempDir.mkdir();

            frames = new Texture[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                BufferedImage frameImage = spriteSheet.getSubimage(
                        i * frameWidth, 0, frameWidth, frameHeight);
                File frameFile = new File(tempDir, "frame_" + i + ".png");
                ImageIO.write(frameImage, "png", frameFile);
                frames[i] = new Texture(frameFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.frameDelay = frameDelay;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDelay) {
            currentFrame = (currentFrame + 1) % frames.length;
            lastFrameTime = currentTime;
        }
    }

    public Texture getCurrentTexture() {
        return frames[currentFrame];
    }

    @Override
    public Animation clone() {
        try {
            Animation clone = (Animation) super.clone();
            // Shallow copy is fine for frames and tempDir since Texture is immutable/shared
            // Primitive fields (currentFrame, frameDelay, lastFrameTime) are copied by Object.clone()
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning not supported", e);
        }
    }
}