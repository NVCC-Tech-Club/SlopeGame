package com.slope.game;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public enum Shape {
    // Using vertices for drawing shapes!

    RAMP( 3,

        // Vertices
        new float[] {
            -0.333f, -0.333f, 0.0f,  // V1
            0.666f, -0.333f, 0.0f,  // V2
            -0.333f,  0.666f, 0.0f   // V3
        },

        // Indices
        new int[] {
            1, 2, 3
        }
    ),

    CUBE(3,

         // Vertices
         new float[] {
            -0.5f, -0.5f, -0.5f, // V0
            0.5f, -0.5f, -0.5f, // V1
            0.5f,  0.5f, -0.5f, // V2
            -0.5f,  0.5f, -0.5f, // V3
            -0.5f, -0.5f,  0.5f, // V4
            0.5f, -0.5f,  0.5f, // V5
            0.5f,  0.5f,  0.5f, // V6
            -0.5f,  0.5f,  0.5f  // V7
        },

        // Indices
        new int[] {
            0, 1, 3, 3, 1, 2,
            1, 5, 2, 2, 5, 6,
            5, 4, 6, 6, 4, 7,
            4, 0, 7, 7, 0, 3,
            3, 2, 7, 7, 2, 6,
            4, 5, 0, 0, 5, 1
        }
    );


    // Don't focus on this, not important for anyone other than "Feeshy" to use.

    private final int vertexCount;
    private final float[] vertices;
    private final int[] indices;

    Shape(int vertexCount, float[] vertices, int[] indices) {
        this.vertexCount = vertexCount;
        this.vertices = vertices;
        this.indices = indices;
    }

    public FloatBuffer storeVerticesInBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
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
}