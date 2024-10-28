package com.slope.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class Sphere {
    public static final int SIZE =
            Float.BYTES * 3 +
            Float.BYTES * 3;

    private final Vector3f rotation;
    private final Vector3f position;

    public Sphere() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
    }

    public void write(ByteBuffer buffer) {
        position.get(0, buffer);
        rotation.get(Float.BYTES * 3, buffer);
    }
}