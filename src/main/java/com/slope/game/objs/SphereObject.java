package com.slope.game.objs;

import com.slope.game.CameraMatrices;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public class SphereObject extends Object {
    private CameraMatrices camMatrices;
    private Matrix4f modelMatrix;

    protected SphereObject(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
    }

    public void write(ByteBuffer buffer) {

    }
}
