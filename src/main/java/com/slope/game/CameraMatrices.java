package com.slope.game;

import org.joml.*;
import org.joml.Math;

import java.nio.ByteBuffer;


public class CameraMatrices {
    public static final float Z_NEAR = 0.05f;
    public static final float Z_FAR = 100.0f;

    private static final float COS_X = Math.cos(0);
    private static final float SIN_X = Math.sin(0);
    private static final float FOV = Math.toRadians(45);

    public static final Vector3f LOOK_UP = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final int SIZE =
            Float.BYTES * 16 + // The size of our projection matrix.
            Float.BYTES * 16 + // The size of our view matrix.
            Float.BYTES * 2; // The size of our near and far plane.

    // Our data sent to the GPU.
    protected final Matrix4f projectionMatrix;
    protected final Matrix4f viewMatrix;
    protected final Vector3f position;
    protected final Vector3f center;
    protected float nearPlane;
    protected float farPlane;

    // Our data that stays.
    protected float verticalAngle;
    protected float horizontalAngle;
    protected Vector3f lookAt;

    

    public CameraMatrices() {
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.position = new Vector3f(0.0f, 0.0f, -3.0f);
        this.nearPlane = 0.0f;
        this.farPlane = 0.0f;

        this.lookAt = new Vector3f(0.0f, 0.0f, 0.0f);

        this.horizontalAngle = Math.atan2(lookAt.x, lookAt.z);
        this.verticalAngle = Math.asin(lookAt.y);

        this.center = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    public void init() {
        updateRotationMat();
        updateViewMat();
    }

    public void write(ByteBuffer buffer) {
        this.projectionMatrix.get(0, buffer);
        this.viewMatrix.get(Float.BYTES * 16, buffer);
        buffer.putFloat(Float.BYTES * 32, this.nearPlane);
        buffer.putFloat(Float.BYTES * 33, this.farPlane);
    }

    // @param zFar (datatype: float) -> The far clipping plane of the camera.
    // @param zNear (datatype: float) -> The near clipping plane of the camera.
    
    public void update(float zNear, float zFar) {
        this.nearPlane = zNear;
        this.farPlane = zFar;

        projectionMatrix.identity();
        projectionMatrix.perspective(FOV, Engine.getMain().getPrimaryWindow().getAspectRatio(), zNear, zFar, false);
    }

    public void updateViewMat() {
        center.zero();
        center.set(position).add(lookAt);
        position.add(lookAt, center);
        viewMatrix.identity();
        viewMatrix.lookAt(position, center, LOOK_UP);
    }

    public void updateRotationMat() {
        float cos_y = Math.cos(verticalAngle);
        float sin_y = Math.sin(verticalAngle);

        lookAt.set(SIN_X * cos_y, sin_y, cos_y * COS_X);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() { return viewMatrix; }

    public Vector3f getPosition() { return position; }
}