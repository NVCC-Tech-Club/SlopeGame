package com.slope.game;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

// This class is responsible for the main loop of the game, physics, rendering, etc.
// All the game logic will be handled here.
class Engine {
    private static Engine main = null; // The main game engine component.
    private static boolean initialized = false; // Whether or not the application has already been initialized.

    private static final long NANOSECOND = 1000000000L;
    public static final int FRAMERATE = 120;
    private static final double FRAME_AREA = 1.0 / FRAMERATE;

    private Window primaryWindow;
    private IComponentManager game;
    private GLFWErrorCallback errorCallback; // Capture any errors that may arise.
    private boolean isRunning; // Whether or not the application is still running.
    private long frames;

    private Engine() {
        this.errorCallback = null;
        this.primaryWindow = null;
        this.isRunning = false;
        this.frames = 0;

        game = Main.getGame();
    }

    public static void init() throws Exception {
        if(main.initialized) { // Check if it's already initialized.
            throw new IllegalStateException("Engine has already been initialized!");
        }

        main = new Engine();
        initialized = true;
        
    }
    
    

    public void start() {
        if(!main.initialized) { // Check if it's been initialized before continuing.
            throw new IllegalStateException("Engine has not been initialized already!");
        }

        if(isRunning) { // Check if it's already running.
            throw new IllegalStateException("Engine has already been initialized!");
        }

        // Print out our current LWJGL version.
        System.out.println("Current LWJGL Version: " + Version.getVersion());

        // Create the "capturer"
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        
        // Initialize window manager and create our window!
        WindowManager.init();
        primaryWindow = WindowManager.createMainWindow(0, 0, "Slope Game");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL.createCapabilities();

        game.init();
        
    }

    // Overall loop.
    public void run() throws Exception {
        isRunning = true;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double accumulatedTime = 0;

        while(isRunning) {
            boolean render = false;
            long start = System.nanoTime();
            long passedTime = start - lastTime;
            lastTime = start;

            accumulatedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            while(accumulatedTime > FRAME_AREA) {
                render = true;
                accumulatedTime -= FRAME_AREA;

                if(primaryWindow.shouldClose()) {
                    stop();
                }

                update();

                if(frameCounter >= NANOSECOND) {
                    frameCounter = 0;
                }
            }

            if(render) {
                render();
            }

            GLFW.glfwPollEvents();
        }
    }

    public void stop() {
        if(!isRunning) {
            return;
        }

        isRunning = false;
    }

    public static Engine getMain() {
        return main;
    }

    // This method is going to be used for rendering stuff.
    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        game.render();
        primaryWindow.update();
    }

    // This method is going to be used for calculating Physics.
    private void update() {
        game.update();
    }

    public Window getPrimaryWindow() {
        return primaryWindow;
    }

    public void destroy() {
        game.destroy();
        primaryWindow.destroy();
        WindowManager.terminate();
        errorCallback.free();
        main = null;
    }
}
