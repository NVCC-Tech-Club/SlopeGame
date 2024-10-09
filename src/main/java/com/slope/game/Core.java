package com.slope.game;

import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL20;

import org.joml.Matrix4f;
import org.joml.Vector3f;



public class Core implements IComponentManager {
    private final RenderManager renderer;
    private final ObjectLoader loader;
    // TODO: Add ComputeShaderManager here. (Feeshy Task Only)

    // MONDAY TASK: Add Camere Class here. (Remove the comment)
    public class Camera {
        private final ShaderManager shaderManager;

        private Vector3f position;
        private Vector3f target;
        private Vector3f up;

        private Matrix4f viewMatrix;

        public Camera(ShaderManager shaderManager) {
            this.shaderManager = shaderManager;

            position = new Vector3f(0.0f, 0.0f, 3.0f); // camera position
            target = new Vector3f(0.0f, 0.0f, 0.0f); // camera direction
            up = new Vector3f(0.0f, 1.0f, 0.0f); //up

            viewMatrix = new Matrix4f();
        }


        public void update() {
            viewMatrix.identity().lookAt(position, target, up);
        }

        public void render() {
            shaderManager.bind();

            int viewMatrixLocation = GL20.glGetUniformLocation(shaderManager.getProgramID(), "viewMatrix");

            GL20.glUniformMatrix4fv(viewMatrixLocation, false, viewMatrix.get(new float[16]));

            shaderManager.unbind();
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }

        public void setTarget(Vector3f target) {
            this.target = target;
        }
        

    }


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
        //MONDAY TASK: Put the updater function for Camera Class here! (Remove this comment)
    }

    @Override
    public void destroy() {
        renderer.destroy();
        loader.destroy();
    }

    // These are background objects that can't be interacted with the game in any way.
    // That's why they are in the Core Class rather than the Game Class.
    private void createGreenTowers() {
        loader.loadVertexObject(Shape.RAMP);
    }
}
