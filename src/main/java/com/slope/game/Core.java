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
        Model n = loader.loadGLTFModel("src/main/resources/models/ramp.glb");

        loader.loadTexture("textures/173texture.jpeg");
        Model m = loader.loadOBJModel("models/SCP.obj");

        //  Trying to rotate the ramp :)
        m.setPosition(new Vector3f(0.0f, 0.0f, 0.0f));
        m.setRotation(new Vector3f(00.0f, 90.0f, 00.0f));
        m.setScale(new Vector3f(1.0f, 1.0f, 1.0f));
        m.getTransformationMatrix();



        loader.loadVertexObject(n, 3);
        loader.loadVertexObject(m, 3);

        System.out.println("Position: " + m.getPosition());
        System.out.println("Rotation: " + m.getPosition());
        System.out.println("Scale: " + m.getPosition());


    }


}
