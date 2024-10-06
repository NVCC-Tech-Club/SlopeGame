package com.slope.game;

public interface IComponentManager extends IComponent {
    // Add component to manager with graphics handler.
    IComponent addComponent(IComponent component, Class<IGraphics> handler);
}