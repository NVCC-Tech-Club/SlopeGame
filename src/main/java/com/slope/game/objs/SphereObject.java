package com.slope.game.objs;

import com.slope.game.CameraMatrices;
import com.slope.game.Engine;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

public class SphereObject extends Object {
    public static final int SIZE =
            Float.BYTES * 16 +
            Float.BYTES * 16 +
            Float.BYTES * 2 +
            Float.BYTES * 16 +
            Float.BYTES * 3;

    private final CameraMatrices camMatrices;
    private final Vector2f resolution;

    public SphereObject(CameraMatrices camMatrices) {
        this.resolution = new Vector2f(0, 0);
        this.camMatrices = camMatrices;
    }

    public void init() {
        updateResolution();
    }

    public void write(ByteBuffer buffer) {
        camMatrices.getProjectionMatrix().get(0, buffer);
        getModelMatrix().get(Float.BYTES * 16, buffer);
        resolution.get(Float.BYTES * 32, buffer);
        camMatrices.getViewMatrix().get(Float.BYTES * 34, buffer);
        camMatrices.getPosition().get(Float.BYTES * 50, buffer);
    }

    public void updateResolution() {
        final int width = Engine.getMain().getPrimaryWindow().getFramebufferWidth();
        final int height = Engine.getMain().getPrimaryWindow().getFramebufferHeight();

        resolution.x = width;
        resolution.y = height;
    }
}