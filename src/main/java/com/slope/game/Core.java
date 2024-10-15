package com.slope.game;

import com.slope.game.utils.Model;
import org.lwjgl.opengl.GL21;
import org.joml.Vector3f;

public class Core implements IComponentManager {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    protected final CameraMatrices camMatrices;

    // TODO: Add ComputeShaderManager here.

    public Core() {
        camMatrices = new CameraMatrices();
        loader = new ObjectLoader();
        renderer = new RenderManager(camMatrices);
    }

    @Override
    public void init() {
        {
            // Model screen = renderer.setScreenModel(loader.createScreen());
            // loader.loadVertexObject(screen, 2);
        }

        createGreenTowers();

        camMatrices.init();
        renderer.init();
        loader.unbind();
    }

    @Override
    public IComponent addComponent(IComponent component, Class<IGraphics> handler) {
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
        camMatrices.updateViewMat();
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
        Model n = loader.loadGLTFModel(0,"src/main/resources/models/ramp.glb");
        n.update();

        loader.loadVertexObject(n, 3);
    }
}
