package com.slope.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;

class WindowManager {
    private static boolean initialized = false;
    private static boolean createdMainWindow = false;

    public static void init() {

        // Initializes GLFW, if returned false, then something went wrong.
        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, 0); // The window will not be visible until we set it to be.
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1); // The window will be resizable.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4); // The major version of OpenGL.
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1); // The minor version of OpenGL.
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); // The profile of OpenGL.
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, 1); // The forward compatibility of OpenGL.
        GLFW.glfwWindowHint(GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER, 1);

        // Finished initializing
        initialized = true;
    }

    public static Window createMainWindow(int width, int height, String title) {
        if(createdMainWindow) {
            throw new IllegalStateException("Cannot create two or more main windows!");
        }

        createdMainWindow = true;
        Window window = new Window(width, height, title);
        window.create();

        if(window.getWindow() == 0) {
            throw new IllegalStateException("Window was not created.");
        }

        GLFW.glfwMakeContextCurrent(window.getWindow());

        // Create our OpenGL capabilities, meaning our function pointers to our current context.
        GL.createCapabilities();

        // Have our window screen be black.
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // We need this since our sphere might just be raymarched
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        return window;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void terminate() {
        // Terminates GLFW.
        GLFW.glfwTerminate();
    }
}