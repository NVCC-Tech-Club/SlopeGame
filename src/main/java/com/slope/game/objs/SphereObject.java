package com.slope.game.objs;

import com.slope.game.CameraMatrices;

import java.nio.ByteBuffer;

public class SphereObject extends Object {
    public static final int SIZE =
            Float.BYTES * 16 +
            Float.BYTES * 16;

    private final CameraMatrices camMatrices;

    public SphereObject(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
    }

    public void write(ByteBuffer buffer) {
        //camMatrices.getProjectionMatrix().get(0, buffer);
        //getModelMatrix().get(Float.BYTES * 16, buffer);
    }
}