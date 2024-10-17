package com.slope.game;

import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.slope.game.objs.SphereObject;

public class Game extends Core {
    private static final int MAX_PlATFORM_CAPACITY = 10;

    // Sensitivity and Mouse States
    private float sensitivity = 0.015f;
    private boolean mouseActive = false;
    private boolean initialMouseCentering = true;

    // Player properties
    private SphereObject sphere;
    private ShaderManager shaderManager;
    private FrameBuffer frameBuffer; // Declare FrameBuffer

    // Constructor
    public Game() {
        super();
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
                (float) (Math.cos(camMatrices.verticalAngle) * Math.cos(camMatrices.horizontalAngle))
            );
        }
        window.setMousePosition(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
    }

    @Override
    public void init() {
        super.init();

        // Initialize ShaderManager and FrameBuffer
        shaderManager = new ShaderManager();
        frameBuffer = new FrameBuffer(); // Initialize the FrameBuffer
        frameBuffer.init(800, 600); // Set the size for FrameBuffer, adjust as necessary

        // Now pass the FrameBuffer to SphereObject
        sphere = new SphereObject(this.camMatrices, shaderManager, frameBuffer);
    }

    public void checkMouseActive() {
        Window window = Engine.getMain().getPrimaryWindow();

        // Hold SPACEBAR to move camera direction
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

    @Override
    public void update() {
        camMatrices.update(0.05f, 160.0f);

        checkMouseActive();
        processInput();
    }
}
