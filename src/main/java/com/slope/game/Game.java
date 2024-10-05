package com.slope.game;

import org.lwjgl.opengl.GL21;

public class Game implements IComponent {

    private final RenderManager renderer;
    private final ObjectLoader loader;

    public Game() {
        loader = new ObjectLoader();
        renderer = new RenderManager();
    }

    @Override
    public void init() {
        loader.loadVertexObject(Shape.RAMP);
        renderer.init();
        loader.unbind();
    }

    @Override
    public void render() {
        final int width = Engine.getMain().getPrimaryWindow().getWidth();
        final int height = Engine.getMain().getPrimaryWindow().getHeight();

        GL21.glViewport(0, 0, width, height);
        renderer.renderInstances(loader);
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {
        renderer.destroy();
        loader.destroy();
    }
}
