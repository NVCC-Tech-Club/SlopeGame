package com.slope.game;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public enum Shape {
    // Using vertices for drawing shapes!

    CUBE(8,

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
    );


    // Don't focus on this, not important for anyone other than "Feeshy" to use.

    private final int vertexCount;
    private final float[] vertices;
    private final int[] indices;
    private final float[] texCoords;

    Shape(int vertexCount, float[] vertices, int[] indices, float[] texCoords) {
        this.vertexCount = vertexCount;
        this.vertices = vertices;
        this.indices = indices;
        this.texCoords = texCoords;
    }

    public FloatBuffer storeVerticesInBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        return buffer;
    }

    public FloatBuffer storeTexCoordsInBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(texCoords.length);
        buffer.put(texCoords).flip();
        return buffer;
    }

    public IntBuffer storeIndicesInBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices).flip();
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexAmount() {
        return vertices.length;
    }
    
    public int getTexCoordAmount(){
        return texCoords.length / 2;
    }

    public float[] getTexCoords(){
        return texCoords;
    }

    public int getIndexCount() { return indices.length; }
}