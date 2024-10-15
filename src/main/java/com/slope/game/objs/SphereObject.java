package com.slope.game.objs;

import com.slope.game.CameraMatrices;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public class SphereObject extends Object {
    private CameraMatrices camMatrices;

    private static int SIZE =
            Float.BYTES * 16 + // The size of our projection matrix.
            Float.BYTES * 16; // The size of our model matrix.


    protected SphereObject(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
    }

    public void write(ByteBuffer buffer) {

    }
}
