package com.slope.game;

import org.lwjgl.opengl.GL21;

public class Core implements IComponentManager {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final CameraMatrices camMatrices;
    // TODO: Add ComputeShaderManager here.

    public Core() {
        camMatrices = new CameraMatrices();
        loader = new ObjectLoader();
        renderer = new RenderManager(camMatrices);
    }

    @Override
    public void init() {
        createGreenTowers();

        camMatrices.init();
        renderer.init();
        loader.unbind();
    }

    @Override
    public IComponent addComponent(IComponent component, Class<IGraphics> hander) {
        return null; // TODO: Make this work, meaning implement.
    }

    @Override
    public void render() {
        final int width = Engine.getMain().getPrimaryWindow().getFramebufferWidth();
        final int height = Engine.getMain().getPrimaryWindow().getFramebufferHeight();

        GL21.glViewport(0, 0, width, height);
        renderer.renderInstances(loader);
    }

    @Override
    public void update() {
        camMatrices.update(0.05f, 160.0f);
    }

    @Override
    public void destroy() {
        renderer.destroy();
        loader.destroy();
    }

    // These are background objects that can't be interacted with the game in any way.
    // That's why they are in the Core Class rather than the Game Class.
    private void createGreenTowers() {
        loader.loadTexture("textures/Object.png");
        loader.loadVertexObject(Shape.CUBE);
    }
}
