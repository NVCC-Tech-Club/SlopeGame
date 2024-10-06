package com.slope.game;

public class Game extends Core {
    private static final int MAX_PlATFORM_CAPACITY = 10;

    // TODO: Also add multiple Fixed Sized Queues here for platforms and red block pillars. (Feeshy Task Only)
    // NOTE: I will have to create my own Fixed Size Queue class that uses LWJGL's direct memory components to
    // allocate and deallocate my capacity and map memory to it like I was in C.

    // TODO: Probably have an ArrayList for the red obstacles in every platform. (Feeshy Task Only)

    public Game() {
        super();
    }

    @Override
    public void init() {
        // TODO: Add stuff above our pre-init to it can get loaded to the renderer. (Feeshy Task Only)

        super.init();
    }
}
