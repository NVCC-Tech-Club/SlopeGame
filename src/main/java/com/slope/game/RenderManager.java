package com.slope.game;

import com.slope.game.utils.Model;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.*;

import java.util.Locale;

import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

public final class RenderManager {
    // Nice utils to have

    public static int maxGLBindings(int target) {
        return switch(target) {
            case GL_UNIFORM_BUFFER -> GL21.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS);
            default -> throw new IllegalStateException("Invalid Target: 0x" + Integer.toHexString(target).toUpperCase(Locale.ROOT));
        };
    }


    // How the renderer works at runtime.

    private Model screen;
    private UniformBlockState uniformBlockState;
    private ShaderManager shaderManager;

    // Camera Stuff.
    private final SizedShaderBlock<CameraMatrices> camBlock;
    private final CameraMatrices camMatrices;

    public RenderManager(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
        this.camBlock = new SizedShaderBlock<CameraMatrices>(this, GL_UNIFORM_BUFFER, CameraMatrices.SIZE, CameraMatrices::write);
    }

    public void init() {
        shaderManager = new ShaderManager();
        uniformBlockState = new UniformBlockState(shaderManager);

        try {
            shaderManager.createVertexShader(ResourceLoader.loadFile("shaders/main-vertex.glsl"));
            shaderManager.createFragmentShader(ResourceLoader.loadFile("shaders/main-fragment.glsl"));
            shaderManager.link();
            shaderManager.createUniform("textureSampler");
            shaderManager.setMatrixUniform("model", new Matrix4f().identity());
            shaderManager.createUniform("model");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void renderInstances(ObjectLoader loader) {
        clear();
        shaderManager.bind();
        shaderManager.setIntUniform("textureSampler", 0);
        renderCamera();

        for(int i=0; i<loader.getModelCapacity(); i++) {

            // Receive our components
            Model m = loader.getModel(i);
            int ID = loader.getID(m.getIndex());
            int indicesCount = loader.getIndicesCount(m.getIndex());
            int textureID = loader.getTextures(m.getTexIndex());

            // Add model matrix
            shaderManager.setMatrixUniform("model", m.getModelMatrix());

            // Bind VAO
            GL30.glBindVertexArray(ID);

            // Bind the element buffer object (EBO) for the indices
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, loader.getEBO(i));

            // Enable the vertex attribute array.
            GL20.glEnableVertexAttribArray(0);

            // Enable the texture attribute array.
            GL20.glEnableVertexAttribArray(1);

            // Enable the color attribute array.
            GL20.glEnableVertexAttribArray(2);

            // Active our texture.
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            // Bind our texture.
            GL21.glBindTexture(GL21.GL_TEXTURE_2D, textureID);

            // Draw the vertices as triangles.
            GL21.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_INT, 0);

            // Disable our attributes
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);

            // Unbind the VAO to avoid any accidental changes.
            GL30.glBindVertexArray(0);

            // Unbind the EBO to avoid any accidental changes.
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

            // Unbind the texture.
            GL21.glBindBuffer(GL15.GL_TEXTURE_2D, 0);

            // Bind VBO for instance-specific data (e.g., instance position)
            // GL20.glEnableVertexAttribArray(1);

            // Set the divisor for instance attribute
            // GL33.glVertexAttribDivisor(1, 1);
        }

        unbind(this.camBlock);

        if(screen != null) {
            int ID = loader.getID(screen.getIndex());
            int textureID = loader.getTextures(screen.getTexIndex());

            // Reset Our Camera Matrices
            camMatrices.projectionMatrix.identity();
            camMatrices.viewMatrix.identity();
            renderCamera();

            // Add model matrix
            shaderManager.setMatrixUniform("model", screen.getModelMatrix());

            // Bind VAO
            GL30.glBindVertexArray(ID);

            // Enable the vertex attribute array.
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(2);

            // Draw the vertices as triangles.
            GL21.glDrawArrays(GL21.GL_TRIANGLES, 0, screen.getVertices().length);

            // Disable our attributes
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(2);

            // Unbind the VAO to avoid any accidental changes.
            GL30.glBindVertexArray(0);
        }

        unbind(this.camBlock);
        shaderManager.unbind();
    }

    public Model setScreenModel(Model value) {
        return screen = value;
    }

    // Assigns the specified block to the next available binding slot.
    public void bind(CharSequence name, SizedShaderBlock<?> block) {
        uniformBlockState.bind(name, block);
    }

    // Releases the specified block and frees its occupied binding slot.
    public void unbind(SizedShaderBlock<?> block) {
        uniformBlockState.unbind(block);
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void destroy() {
        shaderManager.destroy();
    }

    private void renderCamera() {
        camBlock.set(camMatrices);
        bind("CameraMatrices", this.camBlock);
    }
}