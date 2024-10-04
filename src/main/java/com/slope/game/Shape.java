package com.slope.game;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public enum Shape {
    // Using vertices for drawing shapes!

    RAMP( 3, new float[] {
        0.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f
    });


    // Don't focus on this.
    private final int vertexCount;
    private final float[] vertices;

    Shape(int vertexCount, float[] vertices) {
        this.vertexCount = vertexCount;
        this.vertices = vertices;
    }

    public FloatBuffer storeDataInBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        return buffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexAmount() {
        return vertices.length;
    }
}