package com.slope.game;

import com.slope.game.utils.Model;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public enum Shape {
    // Using vertices for drawing shapes!

    CUBE(8,

         // Model
        new Model(

            // Vertices
            new float[] {
             -0.5f, 0.5f, 0.5f,
             -0.5f, -0.5f, 0.5f,
             0.5f, -0.5f, 0.5f,
             0.5f, 0.5f, 0.5f,
             -0.5f, 0.5f, -0.5f,
             0.5f, 0.5f, -0.5f,
             -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             -0.5f, 0.5f, -0.5f,
             0.5f, 0.5f, -0.5f,
             -0.5f, 0.5f, 0.5f,
             0.5f, 0.5f, 0.5f,
             0.5f, 0.5f, 0.5f,
             0.5f, -0.5f, 0.5f,
             -0.5f, 0.5f, 0.5f,
             -0.5f, -0.5f, 0.5f,
             -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             -0.5f, -0.5f, 0.5f,
             0.5f, -0.5f, 0.5f,
            },

            // Indices
            new int[] {
            0, 1, 3, 3, 1, 2,
            8, 10, 11, 9, 8, 11,
            12, 13, 7, 5, 12, 7,
            14, 15, 6, 4, 14, 6,
            16, 18, 19, 17, 16, 19,
            4, 6, 7, 5, 4, 7,
            },

            // Texture Coords
            new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 2.0f,
                1.0f, 2.0f,
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                2.0f, 0.0f,
                1.0f, 1.0f,
                2.0f, 1.0f,
            }
        )
    );


    // Don't focus on this, not important for anyone other than "Feeshy" to use.
    private final int vertexCount;
    private final Model model;

    Shape(int vertexCount, Model model) {
        this.vertexCount = vertexCount;
        this.model = model;
    }

    public FloatBuffer storeVerticesInBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(model.getVertices().length);
        buffer.put(model.getVertices()).flip();
        return buffer;
    }

    public FloatBuffer storeTexCoordsInBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(model.getTexCoord().length);
        buffer.put(model.getTexCoord()).flip();
        return buffer;
    }

    public IntBuffer storeIndicesInBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(model.getIndices().length);
        buffer.put(model.getIndices()).flip();
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexAmount() {
        return model.getVertices().length;
    }

    public int getIndexCount() { return model.getIndices().length; }
}