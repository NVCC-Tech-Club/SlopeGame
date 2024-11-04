package com.slope.game;

import org.lwjgl.opengl.GL21;

import com.slope.game.utils.PropModel;

public class Core implements IComponentManager {
    protected final ObjectLoader loader;
    protected final RenderManager renderer;
    protected final FrameBuffer frameBuffer;
    protected final CameraMatrices camMatrices;

    public Core() {
        camMatrices = new CameraMatrices();
        loader = new ObjectLoader();
        renderer = new RenderManager(camMatrices);
        frameBuffer = new FrameBuffer();
    }

    @Override
    public void init() {
        {
            final int width = Engine.getMain().getPrimaryWindow().getFramebufferWidth();
            final int height = Engine.getMain().getPrimaryWindow().getFramebufferHeight();

            frameBuffer.init(width, height);
        }

        createGreenTowers();

        {
            PropModel screen = renderer.setScreenModel(loader.createScreen(
        frameBuffer.getTextureID() | (frameBuffer.getDepthTexture() << ObjectLoader.BIT_16_CAPACITY)
            ));
            loader.loadVertexObject(screen, 3);
            //screen.scale(0.5f, 0.5f, 0.5f);
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

        GL21.glViewport(0, 0, width, height);

        frameBuffer.bind();
        graphicsPass();
        FrameBuffer.unbind();

        renderer.begin();
        renderer.renderScreen(1, null, loader);
        renderer.end();
    }

    @Override
    public void update() {
        camMatrices.updateViewMat();
    }

    @Override
    public void destroy() {
        frameBuffer.destroy();
        renderer.destroy();
        loader.destroy();
    }

    @Override
    public void onWindowResize(int width, int height) {
        frameBuffer.onWindowResize(width, height);
        frameBuffer.onWindowResize(width, height);
    }

    public void graphicsPass() {
        renderer.begin();
        renderer.renderInstances(loader);
        renderer.end();
    }


    // These are background objects that can't be interacted with the game in any way.
    // That's why they are in the Core Class rather than the Game Class.
    private void createGreenTowers() {
        {
            /*
            loader.loadTexture("textures/Object.png");
            PropModel n = loader.loadGLTFModel(0, "src/main/resources/models/ramp.glb");
            n.rotate(90.0f, 0.0f, 0.0f);
            n.scale(2.0f, 2.0f, 2.0f);
            n.update();
            */

            loader.loadTexture("textures/Object.png");
            PropModel n = loader.loadGLTFModel(1,0, "src/main/resources/models/flat.glb");
            n.setPosition(0.0f, -107.5f, 250.0f);
            n.update();
            renderer.addPropModel(n);

            loader.loadVertexObject(n, 3);

            loader.loadTexture("textures/Object.png");
            PropModel m = loader.loadGLTFModel(300,0, "src/main/resources/models/tower.glb");
            m.setPosition(-250.0f, -400.0f, -250.0f);
            m.update();
            renderer.addPropModel(m);

            loader.loadVertexObject(m, 3);
        }
    }
}