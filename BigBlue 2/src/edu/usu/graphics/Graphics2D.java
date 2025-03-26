/*
Copyright (c) 2024 James Dean Mathias

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package edu.usu.graphics;

import edu.usu.utils.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Graphics2D  implements AutoCloseable {

    private final int width;
    private final int height;
    private final String title;
    private long window;

    private final ArrayList<Tuple3<Rectangle, Color, Matrix4f>> rectanglesSolidColor = new ArrayList<>();
    // TODO: Organize texture rectangles by texture to improve rendering efficiency
    private final ArrayList<Tuple5<Texture, Rectangle, Rectangle, Matrix4f, Vector3f>> rectanglesTexture = new ArrayList<>();
    private final ArrayList<Tuple8<Texture, Rectangle, Vector2f, Vector2f, Vector2f, Vector2f, Matrix4f, Vector3f>> textGlyphs = new ArrayList<>();
    private final ArrayList<Tuple3<Triangle, Color, Matrix4f>> trianglesSolidColor = new ArrayList<>();
    private final ArrayList<Tuple3<Vector3f, Vector3f, Color>> lines = new ArrayList<>();

    private Matrix4f mProjection;
    private Matrix4f mModelIdentity;
    private ShaderProgram shaderSolidColor;
    private ShaderProgram shaderTexture;
    private ShaderProgram shaderFont;

    public Graphics2D(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void initialize(Color clearColor) {
        this.window = prepareWindow(width, height, title);

        // Need this setting to be false, otherwise the way I have implemented the input handling
        // gets messed up.
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_FALSE);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        var capabilities = GL.getCapabilities().OpenGL32;

        // Set the clear color
        glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        // Enable support for blending so that alpha is handled in textures correctly
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_LINE_SMOOTH);

        // Prepare depth testing
        glClearDepth(1.0);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_DEPTH_TEST);

        this.mProjection = new Matrix4f();
        float aspectRatio = (float) width / height;
        this.mProjection.setOrtho2D(-1, 1, 1 / aspectRatio, -1 / aspectRatio);

        this.mModelIdentity = new Matrix4f();
        this.mModelIdentity.identity();

        shaderSolidColor = createShader("resources/shaders/solid-color.vert", "resources/shaders/solid-color.frag");
        shaderTexture = createShader("resources/shaders/texture.vert", "resources/shaders/texture.frag");
        shaderFont = createShader("resources/shaders/font.vert", "resources/shaders/font.frag");

        // OSX needs this to be done before doing anything else in order for the
        // font rendering to work correctly.
        System.setProperty("java.awt.headless", "true");
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public long getWindow() {
        return this.window;
    }

    public void begin() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height);
    }

    public void end() {
        // Draw the lines
        Graphics2DUtils.BuffersColor buffersLinesColor = Graphics2DUtils.prepareLinesColorBuffers(lines);
        Graphics2DUtils.renderLines(mProjection, shaderSolidColor, buffersLinesColor, lines);

        // Draw the solid colored triangles
        Graphics2DUtils.BuffersColor buffersTrisSolidColor = Graphics2DUtils.prepareTrisSolidColorBuffers(trianglesSolidColor);
        Graphics2DUtils.renderTriangles(mProjection, shaderSolidColor, buffersTrisSolidColor, trianglesSolidColor);

        // Draw the solid colored rectangles
        Graphics2DUtils.BuffersColor buffersRectsSolidColor = Graphics2DUtils.prepareRectsSolidColorBuffers(rectanglesSolidColor);
        Graphics2DUtils.renderRectangles(mProjection, shaderSolidColor, buffersRectsSolidColor, rectanglesSolidColor);

        // Draw the textured rectangles
        Graphics2DUtils.BuffersTexture buffersTexture = Graphics2DUtils.prepareRectsTextureBuffers(rectanglesTexture);
        Graphics2DUtils.renderRectangles(mProjection, shaderTexture, buffersTexture, rectanglesTexture);

        // Draw the font rectangles
        Graphics2DUtils.BuffersTexture buffersTextGlyphs = Graphics2DUtils.prepareTextGlyphBuffers(textGlyphs);
        Graphics2DUtils.renderTextGlyphRectangles(mProjection, shaderFont, buffersTextGlyphs, textGlyphs);

        lines.clear();
        trianglesSolidColor.clear();
        rectanglesSolidColor.clear();
        rectanglesTexture.clear();
        textGlyphs.clear();

        glfwSwapBuffers(window);
    }

    public void close() {
        shaderSolidColor.cleanup();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void draw(Vector3f pt1, Vector3f pt2, Color color) {
        lines.add(new Tuple3<>(pt1, pt2, color));
    }

    public void draw(Rectangle destination, Color color) {
        rectanglesSolidColor.add(new Tuple3<>(destination, color, mModelIdentity));
    }

    public void draw(Triangle triangle, Color color) {
        trianglesSolidColor.add(new Tuple3<>(triangle, color, mModelIdentity));
    }

    public void draw(Triangle triangle, float rotation, Vector2f center, Color color) {
        Matrix4f mRotation = new Matrix4f();
        mRotation.translate(center.x, center.y, 0);
        mRotation.rotateZ(rotation);
        mRotation.translate(-center.x, -center.y, 0);
        trianglesSolidColor.add(new Tuple3<>(triangle, color, mRotation));
    }

    public void draw(Rectangle destination, float rotation, Vector2f center, Color color) {
        Matrix4f mRotation = new Matrix4f();
        mRotation.translate(center.x, center.y, 0);
        mRotation.rotateZ(rotation);
        mRotation.translate(-center.x, -center.y, 0);
        rectanglesSolidColor.add(new Tuple3<>(destination, color, mRotation));
    }

    public void draw(Texture texture, Rectangle destination, Color color) {
        rectanglesTexture.add(new Tuple5<>(texture, destination, null, mModelIdentity, new Vector3f(color.r, color.g, color.b)));
    }

    public void draw(Texture texture, Rectangle destination, float rotation, Vector2f center, Color color) {
        Matrix4f mRotation = new Matrix4f();
        mRotation.translate(center.x, center.y, 0);
        mRotation.rotateZ(rotation);
        mRotation.translate(-center.x, -center.y, 0);
        rectanglesTexture.add(new Tuple5<>(texture, destination, null, mRotation, new Vector3f(color.r, color.g, color.b)));
    }

    public void draw(Texture texture, Rectangle destination, Rectangle subImage, float rotation, Vector2f center, Color color) {
        Matrix4f mRotation = new Matrix4f();
        mRotation.translate(center.x, center.y, 0);
        mRotation.rotateZ(rotation);
        mRotation.translate(-center.x, -center.y, 0);
        rectanglesTexture.add(new Tuple5<>(texture, destination, subImage, mRotation, new Vector3f(color.r, color.g, color.b)));
    }

    public void drawTextByWidth(Font font, String text, float left, float top, float width, float z, Color color) {
        var tuples = font.drawText(text, left, top, width, z);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByWidth(Font font, String text, float left, float top, float width, Color color) {
        var tuples = font.drawText(text, left, top,  width, 0.0f);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByWidth(Font font, String text, float left, float top, float width, float z, float rotation, Vector2f center, Color color) {
        var tuples = font.drawText(text, left, top, width, z, rotation, center);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByWidth(Font font, String text, float left, float top, float width, float rotation, Vector2f center, Color color) {
        var tuples = font.drawText(text, left, top, width, 0.0f, rotation, center);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByHeight(Font font, String text, float left, float top, float height, float z, Color color) {
        float width = font.measureTextWidth(text, height);
        var tuples = font.drawText(text, left, top, width, z);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByHeight(Font font, String text, float left, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        var tuples = font.drawText(text, left, top, width, 0.0f);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByHeight(Font font, String text, float left, float top, float height, float z, float rotation, Vector2f center, Color color) {
        float width = font.measureTextWidth(text, height);
        var tuples = font.drawText(text, left, top, width, z, rotation, center);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    public void drawTextByHeight(Font font, String text, float left, float top, float height, float rotation, Vector2f center, Color color) {
        float width = font.measureTextWidth(text, height);
        var tuples = font.drawText(text, left, top, width, 0.0f, rotation, center);
        for (var tuple : tuples) {
            textGlyphs.add(new Tuple8<>(
                    tuple.item1(),
                    tuple.item2(),
                    tuple.item3(),
                    tuple.item4(),
                    tuple.item5(),
                    tuple.item6(),
                    tuple.item7(), // Rotation
                    new Vector3f(color.r, color.g, color.b)));
        }
    }

    private static long prepareWindow(int width, int height, String title) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will not stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        // Create the window
        long window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        centerWindow(window, width, height);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        return window;
    }

    private static void centerWindow(long window, int width, int height) {
        // Get the resolution of the primary monitor
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode != null) {
            // Center the window
            glfwSetWindowPos(
                    window,
                    (videoMode.width() - width) / 2,
                    (videoMode.height() - height) / 2
            );
        }
    }

    private ShaderProgram createShader(String vertexShaderPath, String fragmentShaderPath) {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();

        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(vertexShaderPath, GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(fragmentShaderPath, GL_FRAGMENT_SHADER));

        return new ShaderProgram(shaderModuleDataList);
    }

}
