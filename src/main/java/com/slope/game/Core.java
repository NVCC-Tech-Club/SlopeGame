package com.slope.game;

import org.lwjgl.opengl.GL21;

public class Core implements IComponentManager {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    // TODO: Add ComputeShaderManager here. (Feeshy Task Onlu)

    // MONDAY TASK: Add Camere Class here. (Remove the comment)

    public Core() {
        loader = new ObjectLoader();
        renderer = new RenderManager();
    }

    @Override
    public void init() {
        createGreenTowers();

        renderer.init();
        loader.unbind();
    }

    @Override
    public IComponent addComponent(IComponent component, Class<IGraphics> hander) {
        return null; // TODO: Make this work, meaning implement. (Feeshy Task Onlu)
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
        //MONDAY TASK: Put the updater function for Camera Class here!. (Remove this comment)
    }

    @Override
    public void destroy() {
        renderer.destroy();
        loader.destroy();
    }

    // These are background objects that can't be interacted with the game in any way.
    // That's why they are in the Core Class rather than in the Game Class.
    private void createGreenTowers() {
        loader.loadVertexObject(Shape.RAMP);
    }
}