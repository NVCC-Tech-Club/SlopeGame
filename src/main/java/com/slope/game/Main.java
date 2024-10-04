package com.slope.game;

import org.lwjgl.Version;

class Main {
    public static void main(String[] args) {
        System.out.println("Current LWJGL Version: " + Version.getVersion());

        WindowManager.init();
        Window window = WindowManager.createMainWindow(1280, 720, "Slope Game");

        while(!window.shouldClose()) {
            Engine.begin();
            window.update();
            Engine.end();
        }

        window.destroy();
        WindowManager.terminate();
    }
}
