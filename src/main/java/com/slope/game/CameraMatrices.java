package com.slope.game;

import org.joml.*;
import org.joml.Math;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;


public class CameraMatrices {
    private static final float COS_X = Math.cos((float)Math.PI);
    private static final float SIN_X = Math.sin((float)Math.PI);
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
    private float horizontalAngle;
    private Vector3f lookAt;

    private float sensitivity = 0.015f;
    private boolean mouseActive = false;
    private boolean initialMouseCentering = true;

    public CameraMatrices() {
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.rotationMatrix = new Matrix3f();
        this.position = new Vector3f(0.0f, 15.0f, 90.0f);
        this.nearPlane = 0.0f;
        this.farPlane = 0.0f;



        this.lookAt = new Vector3f(0.0f, 0.0f, -1.0f);

        this.horizontalAngle = (float) Math.atan2(lookAt.x, lookAt.z);
        this.verticalAngle = (float) Math.asin(lookAt.y);

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

    // @param zFar (datatype: float) -> The far clipping plane of the camera.
    // @param zNear (datatype: float) -> The near clipping plane of the camera.
    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    private void processInput() {
        Window window = Engine.getMain().getPrimaryWindow();

        Vector3f forward = new Vector3f(lookAt).normalize();
        Vector3f right = new Vector3f(forward).cross(LOOK_UP).normalize();
        Vector3f up = new Vector3f(right).cross(forward).normalize();

        // Move forward (W)
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            position.add(forward.mul(0.3f));
        }
        // Move backward (S)
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            position.sub(forward.mul(0.3f));
        }
        // Move left (A)
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            position.sub(right.mul(0.3f));
        }
        // Move right (D)
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            position.add(right.mul(0.3f));
        }
        // Move up (E)
        if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
            position.add(up.mul(0.3f));
        }
        // Move down (Q)
        if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
            position.sub(up.mul(0.3f));
        }
    }

    private void handleMouseInput() {
        Window window = Engine.getMain().getPrimaryWindow();

        if (mouseActive) {
            float[] mousePos = window.getMousePosition();
            float deltaX = mousePos[0] - (window.getWidth() / 2.0f);
            float deltaY = mousePos[1] - (window.getHeight() / 2.0f);

            horizontalAngle -= deltaX * sensitivity;
            verticalAngle -= deltaY * sensitivity;

            verticalAngle = Math.clamp(verticalAngle, Math.toRadians(-89.0f), Math.toRadians(89.0f));

            lookAt.set(
                    (float) (Math.cos(verticalAngle) * Math.sin(horizontalAngle)),
                    (float) Math.sin(verticalAngle),
                    (float) (Math.cos(verticalAngle) * Math.cos(horizontalAngle)));
        }
        window.setMousePosition(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
    }

    public void update(float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(FOV, Engine.getMain().getPrimaryWindow().getAspectRatio(), zNear, zFar, false);
        Window window = Engine.getMain().getPrimaryWindow();


        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            if (!mouseActive) {
                mouseActive = true;
                initialMouseCentering = true;
                GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
            }
        } else {
            mouseActive = false;
            GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }

        if (mouseActive && initialMouseCentering) {
            window.setMousePosition(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
            initialMouseCentering = false;
        }

        if (mouseActive) {
            handleMouseInput();
        }
        processInput();

        updateViewMat();
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
