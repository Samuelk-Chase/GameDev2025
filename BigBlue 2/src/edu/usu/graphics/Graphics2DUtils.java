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
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL32.*;

public class Graphics2DUtils {

    public static class BuffersColor {
        public BuffersColor(float[] positions, float[] colors, int[] indices) {
            this.positions = positions;
            this.colors = colors;
            this.indices = indices;
        }

        public float[] positions;
        public float[] colors;
        public int[] indices;
    }

    public static class BuffersTexture {
        public BuffersTexture(float[] positions, float[] coords, int[] indices) {
            this.positions = positions;
            this.coords = coords;
            this.indices = indices;
        }

        public float[] positions;
        public float[] coords;
        public int[] indices;
    }

    public static BuffersColor prepareLinesColorBuffers(ArrayList<Tuple3<Vector3f, Vector3f, Color>> lines) {
        float[] positions = new float[lines.size() * 6];
        float[] colors = new float[lines.size() * 6];
        int[] indices = new int[lines.size() * 2];

        int rIndex = 0;
        int iIndex = 0;
        for (var l : lines) {
            positions[rIndex * 3 + 0] = l.item1().x;
            positions[rIndex * 3 + 1] = l.item1().y;
            positions[rIndex * 3 + 2] = l.item1().z;

            positions[rIndex * 3 + 3] = l.item2().x;
            positions[rIndex * 3 + 4] = l.item2().y;
            positions[rIndex * 3 + 5] = l.item2().z;

            colors[rIndex * 3 + 0] = l.item3().r;
            colors[rIndex * 3 + 1] = l.item3().g;
            colors[rIndex * 3 + 2] = l.item3().b;

            colors[rIndex * 3 + 3] = l.item3().r;
            colors[rIndex * 3 + 4] = l.item3().g;
            colors[rIndex * 3 + 5] = l.item3().b;

            indices[iIndex + 0] = rIndex + 0;
            indices[iIndex + 1] = rIndex + 1;

            rIndex += 2;
            iIndex += 2;
        }

        return new BuffersColor(positions, colors, indices);
    }

    public static BuffersColor prepareTrisSolidColorBuffers(ArrayList<Tuple3<Triangle, Color, Matrix4f>> triangles) {
        float[] positions = new float[triangles.size() * 9];
        float[] colors = new float[triangles.size() * 9];
        int[] indices = new int[triangles.size() * 3];

        int rIndex = 0;
        int iIndex = 0;
        for (var t : triangles) {
            positions[rIndex * 3 + 0] = t.item1().pt1.x;
            positions[rIndex * 3 + 1] = t.item1().pt1.y;
            positions[rIndex * 3 + 2] = t.item1().pt1.z;

            positions[rIndex * 3 + 3] = t.item1().pt2.x;
            positions[rIndex * 3 + 4] = t.item1().pt2.y;
            positions[rIndex * 3 + 5] = t.item1().pt2.z;

            positions[rIndex * 3 + 6] = t.item1().pt3.x;
            positions[rIndex * 3 + 7] = t.item1().pt3.y;
            positions[rIndex * 3 + 8] = t.item1().pt3.z;

            colors[rIndex * 3 + 0] = t.item2().r;
            colors[rIndex * 3 + 1] = t.item2().g;
            colors[rIndex * 3 + 2] = t.item2().b;

            colors[rIndex * 3 + 3] = t.item2().r;
            colors[rIndex * 3 + 4] = t.item2().g;
            colors[rIndex * 3 + 5] = t.item2().b;

            colors[rIndex * 3 + 6] = t.item2().r;
            colors[rIndex * 3 + 7] = t.item2().g;
            colors[rIndex * 3 + 8] = t.item2().b;

            indices[iIndex + 0] = rIndex + 0;
            indices[iIndex + 1] = rIndex + 1;
            indices[iIndex + 2] = rIndex + 2;

            rIndex += 3;
            iIndex += 3;
        }

        return new BuffersColor(positions, colors, indices);
    }

    public static BuffersColor prepareRectsSolidColorBuffers(ArrayList<Tuple3<Rectangle, Color, Matrix4f>> rectangles) {
        float[] positions = new float[rectangles.size() * 12];
        float[] colors = new float[rectangles.size() * 12];
        int[] indices = new int[rectangles.size() * 6];

        int rIndex = 0;
        int iIndex = 0;
        for (var r : rectangles) {
            positions[rIndex * 3 + 0] = r.item1().left;
            positions[rIndex * 3 + 1] = r.item1().top;
            positions[rIndex * 3 + 2] = r.item1().z;

            positions[rIndex * 3 + 3] = r.item1().left + r.item1().width;
            positions[rIndex * 3 + 4] = r.item1().top;
            positions[rIndex * 3 + 5] = r.item1().z;

            positions[rIndex * 3 + 6] = r.item1().left + r.item1().width;
            positions[rIndex * 3 + 7] = r.item1().top + r.item1().height;
            positions[rIndex * 3 + 8] = r.item1().z;

            positions[rIndex * 3 + 9] = r.item1().left;
            positions[rIndex * 3 + 10] = r.item1().top + r.item1().height;
            positions[rIndex * 3 + 11] = r.item1().z;

            colors[rIndex * 3 + 0] = r.item2().r;
            colors[rIndex * 3 + 1] = r.item2().g;
            colors[rIndex * 3 + 2] = r.item2().b;

            colors[rIndex * 3 + 3] = r.item2().r;
            colors[rIndex * 3 + 4] = r.item2().g;
            colors[rIndex * 3 + 5] = r.item2().b;

            colors[rIndex * 3 + 6] = r.item2().r;
            colors[rIndex * 3 + 7] = r.item2().g;
            colors[rIndex * 3 + 8] = r.item2().b;

            colors[rIndex * 3 + 9] = r.item2().r;
            colors[rIndex * 3 + 10] = r.item2().g;
            colors[rIndex * 3 + 11] = r.item2().b;

            indices[iIndex + 0] = rIndex + 0;
            indices[iIndex + 1] = rIndex + 1;
            indices[iIndex + 2] = rIndex + 2;

            indices[iIndex + 3] = rIndex + 0;
            indices[iIndex + 4] = rIndex + 2;
            indices[iIndex + 5] = rIndex + 3;

            rIndex += 4;
            iIndex += 6;
        }

        return new BuffersColor(positions, colors, indices);
    }

    public static BuffersTexture prepareRectsTextureBuffers(ArrayList<Tuple5<Texture, Rectangle, Rectangle, Matrix4f, Vector3f>> rectangles) {
        float[] positions = new float[rectangles.size() * 12];
        float[] coords = new float[rectangles.size() * 8];
        int[] indices = new int[rectangles.size() * 6];

        int rIndex = 0;
        int iIndex = 0;
        for (var r : rectangles) {
            positions[rIndex * 3 + 0] = r.item2().left;
            positions[rIndex * 3 + 1] = r.item2().top;
            positions[rIndex * 3 + 2] = r.item2().z;

            positions[rIndex * 3 + 3] = r.item2().left + r.item2().width;
            positions[rIndex * 3 + 4] = r.item2().top;
            positions[rIndex * 3 + 5] = r.item2().z;

            positions[rIndex * 3 + 6] = r.item2().left + r.item2().width;
            positions[rIndex * 3 + 7] = r.item2().top + r.item2().height;
            positions[rIndex * 3 + 8] = r.item2().z;

            positions[rIndex * 3 + 9] = r.item2().left;
            positions[rIndex * 3 + 10] = r.item2().top + r.item2().height;
            positions[rIndex * 3 + 11] = r.item2().z;

            // r.item3 is the sub image to render, in pixels coord.
            // we have to convert them to texture coords for correct rendering.

            if (r.item3() != null) {
                coords[rIndex * 2 + 0] = r.item3().left / r.item1().getWidth();
                coords[rIndex * 2 + 1] = r.item3().top / r.item1().getHeight();

                coords[rIndex * 2 + 2] = (r.item3().left + r.item3().width) / r.item1().getWidth();
                coords[rIndex * 2 + 3] = r.item3().top / r.item1().getHeight();

                coords[rIndex * 2 + 4] = (r.item3().left + r.item3().width) / r.item1().getWidth();
                coords[rIndex * 2 + 5] = (r.item3().top + r.item3().height) / r.item1().getHeight();

                coords[rIndex * 2 + 6] = r.item3().left / r.item1().getWidth();
                coords[rIndex * 2 + 7] = (r.item3().top + r.item3().height) / r.item1().getHeight();
            } else {
                coords[rIndex * 2 + 0] = 0.0f;
                coords[rIndex * 2 + 1] = 0.0f;

                coords[rIndex * 2 + 2] = 1.0f;
                coords[rIndex * 2 + 3] = 0.0f;

                coords[rIndex * 2 + 4] = 1.0f;
                coords[rIndex * 2 + 5] = 1.0f;

                coords[rIndex * 2 + 6] = 0.0f;
                coords[rIndex * 2 + 7] = 1.0f;
            }

            indices[iIndex + 0] = rIndex + 0;
            indices[iIndex + 1] = rIndex + 1;
            indices[iIndex + 2] = rIndex + 2;

            indices[iIndex + 3] = rIndex + 0;
            indices[iIndex + 4] = rIndex + 2;
            indices[iIndex + 5] = rIndex + 3;

            rIndex += 4;
            iIndex += 6;
        }

        return new BuffersTexture(positions, coords, indices);
    }

    public static BuffersTexture prepareTextGlyphBuffers(ArrayList<Tuple8<Texture, Rectangle, Vector2f, Vector2f, Vector2f, Vector2f, Matrix4f, Vector3f>> rectangles) {
        float[] positions = new float[rectangles.size() * 12];
        float[] coords = new float[rectangles.size() * 8];
        int[] indices = new int[rectangles.size() * 6];

        int rIndex = 0;
        int iIndex = 0;
        for (var r : rectangles) {
            positions[rIndex * 3 + 0] = r.item2().left;
            positions[rIndex * 3 + 1] = r.item2().top;
            positions[rIndex * 3 + 2] = r.item2().z;

            positions[rIndex * 3 + 3] = r.item2().left + r.item2().width;
            positions[rIndex * 3 + 4] = r.item2().top;
            positions[rIndex * 3 + 5] = r.item2().z;

            positions[rIndex * 3 + 6] = r.item2().left + r.item2().width;
            positions[rIndex * 3 + 7] = r.item2().top + r.item2().height;
            positions[rIndex * 3 + 8] = r.item2().z;

            positions[rIndex * 3 + 9] = r.item2().left;
            positions[rIndex * 3 + 10] = r.item2().top + r.item2().height;
            positions[rIndex * 3 + 11] = r.item2().z;

            coords[rIndex * 2 + 0] = r.item3().x;
            coords[rIndex * 2 + 1] = r.item3().y;

            coords[rIndex * 2 + 2] = r.item4().x;
            coords[rIndex * 2 + 3] = r.item4().y;

            coords[rIndex * 2 + 4] = r.item5().x;
            coords[rIndex * 2 + 5] = r.item5().y;

            coords[rIndex * 2 + 6] = r.item6().x;
            coords[rIndex * 2 + 7] = r.item6().y;

            indices[iIndex + 0] = rIndex + 0;
            indices[iIndex + 1] = rIndex + 1;
            indices[iIndex + 2] = rIndex + 2;

            indices[iIndex + 3] = rIndex + 0;
            indices[iIndex + 4] = rIndex + 2;
            indices[iIndex + 5] = rIndex + 3;

            rIndex += 4;
            iIndex += 6;
        }

        return new BuffersTexture(positions, coords, indices);
    }

    public static void renderTriangles(Matrix4f mProjection, ShaderProgram shader, BuffersColor buffers, ArrayList<Tuple3<Triangle, Color, Matrix4f>> triangles) {
        if (triangles.isEmpty()) {
            return;
        }

        List<FloatBuffer> memBuffers = new ArrayList<>();
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboVerts = glGenBuffers();
        FloatBuffer bufferPositions = MemoryUtil.memAllocFloat(buffers.positions.length);
        bufferPositions.put(0, buffers.positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboVerts);
        glBufferData(GL_ARRAY_BUFFER, bufferPositions, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferPositions);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int vboColors = glGenBuffers();
        FloatBuffer bufferColors = MemoryUtil.memAllocFloat(buffers.colors.length);
        bufferColors.put(0, buffers.colors);
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, bufferColors, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferColors);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        int vboIndex = glGenBuffers();
        IntBuffer bufferIndex = MemoryUtil.memAllocInt(buffers.indices.length);
        bufferIndex.put(0, buffers.indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferIndex, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferIndex);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        shader.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Now, step through the list of rectangles and see which ones we can draw together and
            // which ones need to be drawn individually.
            int locationProjection = glGetUniformLocation(shader.getProgramId(), "mProjection");
            glUniformMatrix4fv(locationProjection, false, mProjection.get(stack.mallocFloat(16)));
            int locationModel = glGetUniformLocation(shader.getProgramId(), "mModel");

            glBindVertexArray(vaoId);

            int startIndex = 0;
            Matrix4f mModelGroup = triangles.get(0).item3();
            for (int t = 1; t <= triangles.size(); t++) {
                if (t == triangles.size() || mModelGroup != triangles.get(t).item3()) {
                    memBuffers.add(MemoryUtil.memAllocFloat(16));
                    glUniformMatrix4fv(locationModel, false, mModelGroup.get(memBuffers.get(memBuffers.size() - 1)));
                    // * 3 because three values per index
                    glDrawElements(GL_TRIANGLES, (t - startIndex) * 3, GL_UNSIGNED_INT, (long) startIndex * 3 * Integer.BYTES);

                    startIndex = t;
                    if (t < triangles.size()) {
                        mModelGroup = triangles.get(t).item3();
                    }
                }
            }

            glBindVertexArray(0);
            shader.unbind();

            glDeleteBuffers(vboVerts);
            glDeleteBuffers(vboColors);
            glDeleteBuffers(vboIndex);
            glDeleteVertexArrays(vaoId);
        }

        for (var memory : memBuffers) {
            MemoryUtil.memFree(memory);
        }
    }

    public static void renderLines(Matrix4f mProjection, ShaderProgram shader, BuffersColor buffers, ArrayList<Tuple3<Vector3f, Vector3f, Color>> lines) {
        if (lines.isEmpty()) {
            return;
        }

        List<FloatBuffer> memBuffers = new ArrayList<>();
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboVerts = glGenBuffers();
        FloatBuffer bufferPositions = MemoryUtil.memAllocFloat(buffers.positions.length);
        bufferPositions.put(0, buffers.positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboVerts);
        glBufferData(GL_ARRAY_BUFFER, bufferPositions, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferPositions);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int vboColors = glGenBuffers();
        FloatBuffer bufferColors = MemoryUtil.memAllocFloat(buffers.colors.length);
        bufferColors.put(0, buffers.colors);
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, bufferColors, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferColors);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        int vboIndex = glGenBuffers();
        IntBuffer bufferIndex = MemoryUtil.memAllocInt(buffers.indices.length);
        bufferIndex.put(0, buffers.indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferIndex, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferIndex);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        shader.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Now, step through the list of rectangles and see which ones we can draw together and
            // which ones need to be drawn individually.
            int locationProjection = glGetUniformLocation(shader.getProgramId(), "mProjection");
            glUniformMatrix4fv(locationProjection, false, mProjection.get(stack.mallocFloat(16)));
            int locationModel = glGetUniformLocation(shader.getProgramId(), "mModel");

            glBindVertexArray(vaoId);

            Matrix4f mModelGroup = (new Matrix4f()).identity();
            memBuffers.add(MemoryUtil.memAllocFloat(16));
            glUniformMatrix4fv(locationModel, false, mModelGroup.get(memBuffers.get(memBuffers.size() - 1)));
            // * 2 because three values per index
            glDrawElements(GL_LINES, lines.size() * 2, GL_UNSIGNED_INT, (long) 0);

            glBindVertexArray(0);
            shader.unbind();

            glDeleteBuffers(vboVerts);
            glDeleteBuffers(vboColors);
            glDeleteBuffers(vboIndex);
            glDeleteVertexArrays(vaoId);
        }

        for (var memory : memBuffers) {
            MemoryUtil.memFree(memory);
        }
    }

    public static void renderRectangles(Matrix4f mProjection, ShaderProgram shader, BuffersColor buffers, ArrayList<Tuple3<Rectangle, Color, Matrix4f>> rectangles) {
        if (rectangles.isEmpty()) {
            return;
        }

        List<FloatBuffer> memBuffers = new ArrayList<>();
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboVerts = glGenBuffers();
        FloatBuffer bufferPositions = MemoryUtil.memAllocFloat(buffers.positions.length);
        bufferPositions.put(0, buffers.positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboVerts);
        glBufferData(GL_ARRAY_BUFFER, bufferPositions, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferPositions);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int vboColors = glGenBuffers();
        FloatBuffer bufferColors = MemoryUtil.memAllocFloat(buffers.colors.length);
        bufferColors.put(0, buffers.colors);
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, bufferColors, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferColors);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        int vboIndex = glGenBuffers();
        IntBuffer bufferIndex = MemoryUtil.memAllocInt(buffers.indices.length);
        bufferIndex.put(0, buffers.indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferIndex, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferIndex);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        shader.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Now, step through the list of rectangles and see which ones we can draw together and
            // which ones need to be drawn individually.
            int locationProjection = glGetUniformLocation(shader.getProgramId(), "mProjection");
            glUniformMatrix4fv(locationProjection, false, mProjection.get(stack.mallocFloat(16)));
            int locationModel = glGetUniformLocation(shader.getProgramId(), "mModel");

            glBindVertexArray(vaoId);

            int startIndex = 0;
            Matrix4f mModelGroup = rectangles.get(0).item3();
            for (int r = 1; r <= rectangles.size(); r++) {
                if (r == rectangles.size() || mModelGroup != rectangles.get(r).item3()) {
                    memBuffers.add(MemoryUtil.memAllocFloat(16));
                    glUniformMatrix4fv(locationModel, false, mModelGroup.get(memBuffers.get(memBuffers.size() - 1)));
                    // * 3 because three values per index
                    // * 2 because two triangles per rectangle
                    glDrawElements(GL_TRIANGLES, (r - startIndex) * 3 * 2, GL_UNSIGNED_INT, (long) startIndex * 3 * 2 * Integer.BYTES);

                    startIndex = r;
                    if (r < rectangles.size()) {
                        mModelGroup = rectangles.get(r).item3();
                    }
                }
            }

            glBindVertexArray(0);
            shader.unbind();

            glDeleteBuffers(vboVerts);
            glDeleteBuffers(vboColors);
            glDeleteBuffers(vboIndex);
            glDeleteVertexArrays(vaoId);
        }

        for (var memory : memBuffers) {
            MemoryUtil.memFree(memory);
        }
    }

    public static void renderRectangles(Matrix4f mProjection, ShaderProgram shader, BuffersTexture buffers, ArrayList<Tuple5<Texture, Rectangle, Rectangle, Matrix4f, Vector3f>> rectangles) {
        if (rectangles.isEmpty()) {
            return;
        }

        List<FloatBuffer> memBuffers = new ArrayList<>();

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboVerts = glGenBuffers();
        FloatBuffer bufferPositions = MemoryUtil.memAllocFloat(buffers.positions.length);
        bufferPositions.put(0, buffers.positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboVerts);
        glBufferData(GL_ARRAY_BUFFER, bufferPositions, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferPositions);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int vboCoords = glGenBuffers();
        FloatBuffer bufferTexCoords = MemoryUtil.memAllocFloat(buffers.coords.length);
        bufferTexCoords.put(0, buffers.coords);
        glBindBuffer(GL_ARRAY_BUFFER, vboCoords);
        glBufferData(GL_ARRAY_BUFFER, bufferTexCoords, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferTexCoords);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        int vboIndex = glGenBuffers();
        IntBuffer bufferIndex = MemoryUtil.memAllocInt(buffers.indices.length);
        bufferIndex.put(0, buffers.indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferIndex, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferIndex);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        shader.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Now, step through the list of rectangles and see which ones we can draw together and
            // which ones need to be drawn individually.
            int locationProjection = glGetUniformLocation(shader.getProgramId(), "mProjection");
            glUniformMatrix4fv(locationProjection, false, mProjection.get(stack.mallocFloat(16)));
            int locationModel = glGetUniformLocation(shader.getProgramId(), "mModel");
            int locationColor = glGetUniformLocation(shader.getProgramId(), "color");

            glBindVertexArray(vaoId);

            int startIndex = 0;
            Matrix4f mModelGroup = rectangles.get(0).item4();
            for (int r = 1; r <= rectangles.size(); r++) {
                if (r == rectangles.size() || rectangles.get(r - 1).item1() != rectangles.get(r).item1() || rectangles.get(r - 1).item5() != rectangles.get(r).item5() || mModelGroup != rectangles.get(r).item4()) {

                    memBuffers.add(MemoryUtil.memAllocFloat(16));
                    glUniformMatrix4fv(locationModel, false, mModelGroup.get(memBuffers.get(memBuffers.size() - 1)));
                    memBuffers.add(MemoryUtil.memAllocFloat(3));
                    glUniform3fv(locationColor, rectangles.get(r - 1).item5().get(memBuffers.get(memBuffers.size() - 1)));

                    glActiveTexture(GL_TEXTURE0);
                    rectangles.get(r - 1).item1().bind();

                    // * 3 because three values per index
                    // * 2 because two triangles per rectangle
                    glDrawElements(GL_TRIANGLES, (r - startIndex) * 3 * 2, GL_UNSIGNED_INT, (long) startIndex * 3 * 2 * Integer.BYTES);

                    startIndex = r;
                    if (r < rectangles.size()) {
                        mModelGroup = rectangles.get(r).item4();
                    }
                }
            }

            glBindVertexArray(0);
            shader.unbind();

            glDeleteBuffers(vboVerts);
            glDeleteBuffers(vboCoords);
            glDeleteBuffers(vboIndex);
            glDeleteVertexArrays(vaoId);
        }

        for (var memory : memBuffers) {
            MemoryUtil.memFree(memory);
        }
    }

    public static void renderTextGlyphRectangles(Matrix4f mProjection, ShaderProgram shader, BuffersTexture buffers, ArrayList<Tuple8<Texture, Rectangle, Vector2f, Vector2f, Vector2f, Vector2f, Matrix4f, Vector3f>> rectangles) {
        if (rectangles.isEmpty()) {
            return;
        }

        List<FloatBuffer> memBuffers = new ArrayList<>();

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboVerts = glGenBuffers();
        FloatBuffer bufferPositions = MemoryUtil.memAllocFloat(buffers.positions.length);
        bufferPositions.put(0, buffers.positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboVerts);
        glBufferData(GL_ARRAY_BUFFER, bufferPositions, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferPositions);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int vboCoords = glGenBuffers();
        FloatBuffer bufferTexCoords = MemoryUtil.memAllocFloat(buffers.coords.length);
        bufferTexCoords.put(0, buffers.coords);
        glBindBuffer(GL_ARRAY_BUFFER, vboCoords);
        glBufferData(GL_ARRAY_BUFFER, bufferTexCoords, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferTexCoords);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        int vboIndex = glGenBuffers();
        IntBuffer bufferIndex = MemoryUtil.memAllocInt(buffers.indices.length);
        bufferIndex.put(0, buffers.indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndex);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferIndex, GL_STATIC_DRAW);
        MemoryUtil.memFree(bufferIndex);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        shader.bind();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Now, step through the list of rectangles and see which ones we can draw together and
            // which ones need to be drawn individually.
            int locationProjection = glGetUniformLocation(shader.getProgramId(), "mProjection");
            glUniformMatrix4fv(locationProjection, false, mProjection.get(stack.mallocFloat(16)));
            int locationModel = glGetUniformLocation(shader.getProgramId(), "mModel");
            int locationColor = glGetUniformLocation(shader.getProgramId(), "color");

            glBindVertexArray(vaoId);

            int startIndex = 0;
            Matrix4f mModelGroup = rectangles.get(0).item7();
            for (int r = 1; r <= rectangles.size(); r++) {
                if (r == rectangles.size() || rectangles.get(r - 1).item1() != rectangles.get(r).item1() || rectangles.get(r - 1).item8() != rectangles.get(r).item8() || mModelGroup != rectangles.get(r).item7()) {

                    memBuffers.add(MemoryUtil.memAllocFloat(16));
                    glUniformMatrix4fv(locationModel, false, mModelGroup.get(memBuffers.get(memBuffers.size() - 1)));
                    memBuffers.add(MemoryUtil.memAllocFloat(3));
                    glUniform3fv(locationColor, rectangles.get(r - 1).item8().get(memBuffers.get(memBuffers.size() - 1)));

                    glActiveTexture(GL_TEXTURE0);
                    rectangles.get(r - 1).item1().bind();

                    // * 3 because three values per index
                    // * 2 because two triangles per rectangle
                    glDrawElements(GL_TRIANGLES, (r - startIndex) * 3 * 2, GL_UNSIGNED_INT, (long) startIndex * 3 * 2 * Integer.BYTES);

                    startIndex = r;
                    if (r < rectangles.size()) {
                        mModelGroup = rectangles.get(r).item7();
                    }
                }
            }

            glBindVertexArray(0);
            shader.unbind();

            glDeleteBuffers(vboVerts);
            glDeleteBuffers(vboCoords);
            glDeleteBuffers(vboIndex);
            glDeleteVertexArrays(vaoId);
        }

        for (var memory : memBuffers) {
            MemoryUtil.memFree(memory);
        }
    }
}
