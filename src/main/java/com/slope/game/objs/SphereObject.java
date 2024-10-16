package com.slope.game.objs;

import com.slope.game.CameraMatrices;
import com.slope.game.RenderManager;
import com.slope.game.SizedShaderBlock;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

public class SphereObject extends Object {
    private final CameraMatrices camMatrices;

    public static final int SIZE =
            Float.BYTES * 16 + // The size of our projection matrix.
            Float.BYTES * 16; // The size of our model matrix.


    public SphereObject(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
    }

    public void write(ByteBuffer buffer) {
        this.camMatrices.getProjectionMatrix().get(0, buffer);
        this.getModelMatrix().get(Float.BYTES * 16, buffer);
    }
}
