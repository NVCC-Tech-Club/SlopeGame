package com.slope.game;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class RenderManager {
    private ShaderManager shaderManager;

    public RenderManager() {}

    public void init() {
        shaderManager = new ShaderManager();

        try {
            shaderManager.createVertexShader(ResourceLoader.loadShader("shaders/main-vertex.glsl"));
            shaderManager.createFragmentShader(ResourceLoader.loadShader("shaders/main-fragment.glsl"));
            shaderManager.link();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void renderInstances(ObjectLoader loader) {
        clear();
        shaderManager.bind();

        for(int i=0; i<loader.getCapacity(); i++) {

            // Receive our components
            int ID = loader.getID(i);
            int vertexCount = loader.getVertexCount(i);
            int VBO = loader.getVBO(0);

            // Bind VAO
            GL30.glBindVertexArray(ID);

            // Enable the vertex attribute array.
            GL20.glEnableVertexAttribArray(0);

            // Draw the vertices as triangles.
            GL21.glDrawArrays(GL11.GL_TRIANGLES,0, vertexCount);

            // Disable the vertex attribute array after rendering.
            GL20.glDisableVertexAttribArray(0);

            // Unbind the VAO to avoid any accidental changes.
            GL30.glBindVertexArray(0);

            // Bind VBO for instance-specific data (e.g., instance position)
            // GL20.glEnableVertexAttribArray(1);

            // Set the divisor for instance attribute
            // GL33.glVertexAttribDivisor(1, 1);
        }

        shaderManager.unbind();
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void destroy() {
        shaderManager.destroy();
    }
}