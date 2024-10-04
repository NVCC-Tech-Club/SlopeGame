package com.slope.game;

public interface IComponent {
    // Initialize our component.
    void init();

    // Render our component.
    void render();

    // Update our component.
    void update();

    // Destroy our component.
    void destroy();
}
