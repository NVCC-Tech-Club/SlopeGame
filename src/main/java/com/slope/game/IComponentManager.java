package com.slope.game;

public interface IComponentManager extends IComponent {
    // Add component to manager with graphics handler.
    IComponent addComponent(IComponent component, Class<IGraphics> handler);

    // Update when window was resized (DPI are accounted for width and height)
    void onWindowResize(int width, int height);
}