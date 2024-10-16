package com.slope.game;

import com.slope.game.objs.SphereObject;
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

    // Sphere Stuff
    private final SizedShaderBlock<SphereObject> sphereBlock;

    // Camera Stuff.
    private final SizedShaderBlock<CameraMatrices> camBlock;
    private final CameraMatrices camMatrices;

    public RenderManager(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
<<<<<<< Updated upstream
        this.camBlock = new SizedShaderBlock<CameraMatrices>(this, GL_UNIFORM_BUFFER, CameraMatrices.SIZE, CameraMatrices::write);
=======

        this.camBlock = new SizedShaderBlock<>(this, GL_UNIFORM_BUFFER, CameraMatrices.SIZE, CameraMatrices::write);
        this.sphereBlock = new SizedShaderBlock<>(this, GL_UNIFORM_BUFFER, SphereObject.SIZE, SphereObject::write);
>>>>>>> Stashed changes
    }

    public void init() {
        shaderManager = new ShaderManager();
        uniformBlockState = new UniformBlockState(shaderManager);

        try {
            shaderManager.createVertexShader(ResourceLoader.loadFile("shaders/main-vertex.glsl"));
            shaderManager.createFragmentShader(ResourceLoader.loadFile("shaders/main-fragment.glsl"));
            shaderManager.link();
            shaderManager.createUniform("textureSampler");

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

            // Bind VAO
            GL30.glBindVertexArray(ID);

            // Mutliply camera model matrix.
            camMatrices.mulModelToView(m.getModelMatrix());

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

<<<<<<< Updated upstream
=======
        unbind(this.camBlock);
        shaderManager.unbind();
    }

    public void renderScreen(int programIndex, SphereObject sphere, ObjectLoader loader) {
        if(screen == null) {
            return;
        }

        shaderManager.bind(programIndex);

        int ID = loader.getID(screen.getIndex());
        int textureID = screen.getTexIndex();

        switch(programIndex) {
            case 0:
                // Add model matrix
                camMatrices.projectionMatrix.identity();
                camMatrices.viewMatrix.identity();
                renderCamera();

                shaderManager.setMatrixUniform("model", screen.getModelMatrix());

                // Update uniform texture sampler
                shaderManager.setIntUniform("textureSampler", 0);

                break;
            case 1:
                renderSphere(sphere);
                break;
        }

        // Bind VAO
        GL30.glBindVertexArray(ID);

        // Enable the vertex attribute array.
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // Active our texture.
        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        // Bind our texture.
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, textureID);

        // Draw the vertices as triangles.
        GL21.glDrawArrays(GL21.GL_TRIANGLES, 0, screen.getVertices().length);

        // Disable our attributes
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // Unbind the texture.
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, 0);

        // Unbind the VAO to avoid any accidental changes.
        GL30.glBindVertexArray(0);

        switch(programIndex) {
            case 0:
                unbind(this.camBlock);
                break;
            case 1:
                unbind(this.sphereBlock);
                break;
        }

>>>>>>> Stashed changes
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
        camMatrices.update(0.05f, 160.0f);
        camBlock.set(camMatrices);
        bind("CameraMatrices", this.camBlock);
    }
<<<<<<< Updated upstream
=======

    public void renderSphere(SphereObject object) {
        sphereBlock.set(object);
        bind("SphereBlock", this.sphereBlock);
    }

    private void createGameUniforms() throws Exception {
        shaderManager.createUniform("textureSampler");
        shaderManager.setMatrixUniform("model", new Matrix4f().identity());
        shaderManager.createUniform("model");
    }
>>>>>>> Stashed changes
}