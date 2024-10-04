package com.slope.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.system.MemoryUtil;

class Window {
    private int width;
    private int height;
    private String title;
    private boolean vsync;

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

        if(this.width <= 0 || this.height <= 0) {
            this.width = 1280;
            this.height = 720;
        }
    }

    public void create() {
        // Create the actual window component.
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);

        // Signal when window was resized.
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
        });

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

    public String getTitle() {
        return title;
    }

    public boolean isVsync() {
        return vsync;
    }

    public long getWindow() {
        return window;
    }
}
