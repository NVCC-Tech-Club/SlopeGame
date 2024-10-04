package com.slope.game;

import org.lwjgl.opengl.GL21;
import org.lwjgl.glfw.GLFW;

// This class is responsible for the main loop of the game, physics, rendering, etc.
// All the game logic will be handled here.
class Engine {
    public static void begin() {
        GL21.glClear(GL21.GL_COLOR_BUFFER_BIT | GL21.GL_DEPTH_BUFFER_BIT);
    }

    public static void end() {
        GLFW.glfwPollEvents();
    }
}
