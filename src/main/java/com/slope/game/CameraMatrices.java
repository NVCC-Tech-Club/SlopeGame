package com.slope.game;

import org.joml.*;
import org.joml.Math;

import java.nio.ByteBuffer;

public class CameraMatrices {
    private static final float COS_X = Math.cos(0);
    private static final float SIN_X = Math.sin(0);
    private static final float FOV = Math.toRadians(45);
    private static final Vector3f LOOK_UP = new Vector3f(0.0f, 1.0f, 0.0f);

    public static final int SIZE =
            Float.BYTES * 16 + // The size of our projection matrix.
            Float.BYTES * 16 + // The size of our view matrix.
            Float.BYTES * 9 + // The size of our rotation matrix.
            Float.BYTES * 3 + // The size of our camera position.
            Float.BYTES * 2; // The size of our near and far plane.

    // Our data sent to the GPU.
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix3f rotationMatrix;
    private final Vector3f position;
    private final Vector3f center;
    private float nearPlane;
    private float farPlane;

    // Our data that stays.
    private Matrix3f rotation3fX;
    private Matrix3f rotation3fY;
    private float verticalAngle;
    private Vector3f lookAt;

    public CameraMatrices() {
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.rotationMatrix = new Matrix3f();
        this.position = new Vector3f(0.0f, 1.0f, -3.0f);
        this.nearPlane = 0.0f;
        this.farPlane = 0.0f;

        this.verticalAngle = Math.toRadians(-30.0f);
        this.lookAt = new Vector3f(0.0f, 0.0f, 1.0f);
        this.center = new Vector3f(0.0f, 0.0f, 0.0f);
        this.rotation3fX = new Matrix3f();
        this.rotation3fY = new Matrix3f();
    }

    public void init() {
        updateRotationMat();
        updateViewMat();
    }

    public void write(ByteBuffer buffer) {
        this.projectionMatrix.get(0, buffer);
        this.viewMatrix.get(Float.BYTES * 16, buffer);
        this.rotationMatrix.get(Float.BYTES * 32, buffer);
        this.position.get(Float.BYTES * 41, buffer);
        buffer.putFloat(Float.BYTES * 44, this.nearPlane);
        buffer.putFloat(Float.BYTES * 45, this.farPlane);
    }

    // TODO (Gain one point): Have a update method here that calculates our matrix.
    // @param zFar (datatype: float) -> The far clipping plane of the camera.
    // @param zNear (datatype: float) -> The near clipping plane of the camera.
    public void update(float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(FOV, Engine.getMain().getPrimaryWindow().getAspectRatio(), zNear, zFar, false);
    }

    public void updateViewMat() {
        center.zero();
        position.add(lookAt, center);
        viewMatrix.identity();
        viewMatrix.lookAt(position, center, LOOK_UP);
    }

    public void updateRotationMat() {
        float cos_y = Math.cos(verticalAngle);
        float sin_y = Math.sin(verticalAngle);

        rotation3fX.m00(COS_X);
        rotation3fX.m02(SIN_X);
        rotation3fX.m20(-SIN_X);
        rotation3fX.m22(COS_X);

        rotation3fY.m11(cos_y);
        rotation3fY.m12(-sin_y);
        rotation3fY.m21(sin_y);
        rotation3fY.m22(cos_y);

        rotation3fX.mul(rotation3fY, rotationMatrix);
        lookAt.set(SIN_X * cos_y, sin_y, cos_y * COS_X);
    }
}
