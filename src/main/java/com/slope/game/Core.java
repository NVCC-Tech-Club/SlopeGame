package com.slope.game;

import com.slope.game.utils.Model;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class Core implements IComponentManager {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final FrameBuffer frameBuffer;
    protected final CameraMatrices camMatrices;

    public Core() {
        camMatrices = new CameraMatrices();
        loader = new ObjectLoader();
        renderer = new RenderManager(camMatrices);
        frameBuffer = new FrameBuffer();
    }

    @Override
    public void init() {
        createGreenTowers();

        {
            final int width = Engine.getMain().getPrimaryWindow().getFramebufferWidth();
            final int height = Engine.getMain().getPrimaryWindow().getFramebufferHeight();

            frameBuffer.init(width, height);
        }

        {
            Model screen = renderer.setScreenModel(loader.createScreen(frameBuffer.getTextureID()));
            loader.loadVertexObject(screen, 3);
            screen.scale(0.5f, 0.5f, 0.5f);
            screen.update();
        }

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

        frameBuffer.bind();
        GL21.glViewport(0, 0, width, height);
        renderer.clear();
        renderer.renderInstances(loader);
        FrameBuffer.unbind();

        GL21.glViewport(0, 0, width, height);
        renderer.renderScreen(loader);
    }

    @Override
    public void update() {
        //camMatrices.updateViewMat();
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
        Model n = loader.loadGLTFModel(0,"src/main/resources/models/ramp.glb");
        n.rotate(90.0f, 0.0f, 0.0f);
        n.scale(2.0f, 2.0f, 2.0f);
        n.update();

        loader.loadVertexObject(n, 3);
    }
}