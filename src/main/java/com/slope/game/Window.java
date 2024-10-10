package com.slope.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.system.MemoryUtil;

class Window {
    private int width;
    private int height;
    private int[] framebufferWidth;
    private int[] framebufferHeight;
    private String title;
    private boolean vsync;
    private float aspectRatio;

    private long window;

    public Window(int width, int height, String title) {

        // Make sure window manager is initialized before creating the window.
        if(!WindowManager.isInitialized()) {
            throw new IllegalStateException("WindowManager must be initialized before creating a window.");
        }

        this.width = width;
        this.height = height;
        this.title = title;
        this.vsync = true;

        this.framebufferWidth = new int[1];
        this.framebufferHeight = new int[1];

        if(this.width <= 0 || this.height <= 0) {
            this.width = 800;
            this.height = 800;
        }

        this.aspectRatio = (float)width / (float)height;
    }

    public void create() {
        // Create the actual window component.
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

        // Signal when window was resized.

        GLFWFramebufferSizeCallbackI bufferSizeCall = (window, width, height) -> {
            this.framebufferWidth[0] = width;
            this.framebufferHeight[0] = height;
            this.aspectRatio = (float)width / (float)height;
        };

        GLFWWindowSizeCallbackI windowSizeCall = (window, width, height) -> {
            this.width = width;
            this.height = height;
        };

        GLFW.glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
        GLFW.glfwSetFramebufferSizeCallback(window, bufferSizeCall);
        GLFW.glfwSetWindowSizeCallback(window, windowSizeCall);
        bufferSizeCall.invoke(window, framebufferWidth[0], framebufferHeight[0]);

        // Since we aren't planning on starting the program with a fullscreened window, let's just center it.
        // Our video mode will be our primary monitor.
        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (videoMode.width() - width) >> 1, (videoMode.height() - height) >> 1);

        // Set our current context to be the window.
        GLFW.glfwMakeContextCurrent(window);

        if(vsync) { // If vsync, add vsync.
            GLFW.glfwSwapInterval(1);
        }

        // Display our window.
        GLFW.glfwShowWindow(window);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void update() {
        GLFW.glfwSwapBuffers(window);
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(window);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFramebufferWidth() {
        return framebufferWidth[0];
    }

    public int getFramebufferHeight() {
        return framebufferHeight[0];
    }

    public String getTitle() {
        return title;
    }

    public boolean isVsync() {
        return vsync;
    }

    public long getWindow() {
        return window;
    }

    public float getAspectRatio() { return aspectRatio; }
}