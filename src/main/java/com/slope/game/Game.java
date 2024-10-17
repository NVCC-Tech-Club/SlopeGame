package com.slope.game;

import com.slope.game.objs.SphereObject;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.joml.Math;

public class Game extends Core {
    private static final int MAX_PlATFORM_CAPACITY = 10;

    // TODO: Also add multiple Fixed Sized Queues here for platforms and red block pillars. (Feeshy Task Only)
    // NOTE: I will have to create my own Fixed Size Queue class that uses LWJGL's direct memory components to
    // allocate and deallocate my capacity and map memory to it like I was in C.
    // This means I need an object pool to store all platform variations.

    // TODO: Probably have an ArrayList for the red obstacles in every platform. (Feeshy Task Only)
    protected float sensitivity = 0.015f;
    protected boolean mouseActive = false;
    protected boolean initialMouseCentering = true;

    // Sphere Object
    private final SphereObject sphere;

    public Game() {
        super();

        sphere = new SphereObject(camMatrices);
    }
    
    public void move(float dx, float dy, float dz) {
        camMatrices.position.add(dx, dy, dz);
    }

    private void processInput() {
        Window window = Engine.getMain().getPrimaryWindow();

        Vector3f forward = new Vector3f(camMatrices.lookAt).normalize();
        Vector3f right = new Vector3f(forward).cross(camMatrices.LOOK_UP).normalize();
        Vector3f up = new Vector3f(right).cross(forward).normalize();

        // Move forward (W)
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            camMatrices.position.add(forward.mul(0.3f));
        }
        // Move backward (S)
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            camMatrices.position.sub(forward.mul(0.3f));
        }
        // Move left (A)
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            camMatrices.position.sub(right.mul(0.3f));
        }
        // Move right (D)
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            camMatrices.position.add(right.mul(0.3f));
        }
        // Move up (E)
        if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
            camMatrices.position.add(up.mul(0.3f));
        }
        // Move down (Q)
        if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
            camMatrices.position.sub(up.mul(0.3f));
        }
    }

    private void handleMouseInput() {
        Window window = Engine.getMain().getPrimaryWindow();

        if (mouseActive) {
            float[] mousePos = window.getMousePosition();
            float deltaX = mousePos[0] - (window.getWidth() / 2.0f);
            float deltaY = mousePos[1] - (window.getHeight() / 2.0f);

            camMatrices.horizontalAngle -= deltaX * sensitivity;
            camMatrices.verticalAngle -= deltaY * sensitivity;

            camMatrices.verticalAngle = Math.clamp(camMatrices.verticalAngle, Math.toRadians(-89.0f), Math.toRadians(89.0f));

            camMatrices.lookAt.set(
                    (float) (Math.cos(camMatrices.verticalAngle) * Math.sin(camMatrices.horizontalAngle)),
                    (float) Math.sin(camMatrices.verticalAngle),
                    (float) (Math.cos(camMatrices.verticalAngle) * Math.cos(camMatrices.horizontalAngle)));
        }
        window.setMousePosition(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
    }

    @Override
    public void init() {
        // TODO: Add stuff above our pre-init to it can get loaded to the renderer. (Feeshy Task Only)

        super.init();
    }

    public void checkMouseActive(){
        Window window = Engine.getMain().getPrimaryWindow();

        //HOLD SPACEBAR TO MOVE CAMERA DIRECTION (might change later cuz idk a better way to do it atm).
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
    }

    public void graphicsPass() {
        super.graphicsPass();

        renderer.renderScreen(1, null, this.loader);
        //renderer.renderInstances(loader);
    }

    @Override
    public void update() {
        camMatrices.updateViewMat();

        checkMouseActive();
        processInput();
    }
}