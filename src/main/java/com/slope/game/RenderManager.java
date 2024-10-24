package com.slope.game;

import java.util.Locale;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import com.slope.game.objs.SphereObject;
import com.slope.game.utils.Model;

public final class RenderManager {
    // Nice utils to have

    public static int maxGLBindings(int target) {
        return switch(target) {
            case GL_UNIFORM_BUFFER -> GL21.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS);
            default -> throw new IllegalStateException("Invalid Target: 0x" + Integer.toHexString(target).toUpperCase(Locale.ROOT));
        };
    }


    // How the renderer works at runtime.

    private UniformBlockState uniformBlockState;
    private ShaderManager shaderManager;
    private int __dirtyLink = -1;

    // Sphere Stuff
    private final SizedShaderBlock<SphereObject> sphereBlock;

    // Camera Stuff.
    private final SizedShaderBlock<CameraMatrices> camBlock;
    private final CameraMatrices camMatrices;

    public RenderManager(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
        this.camBlock = new SizedShaderBlock<>(this, GL_UNIFORM_BUFFER, CameraMatrices.SIZE, CameraMatrices::write);
        this.sphereBlock = new SizedShaderBlock<>(this, GL_UNIFORM_BUFFER, SphereObject.SIZE, SphereObject::write);
    }

    public void init() {
        shaderManager = new ShaderManager();
        uniformBlockState = new UniformBlockState(shaderManager);

        try {
            shaderManager.createShaderProgram();
            shaderManager.createVertexShader(ResourceLoader.loadShader("shaders/main-vertex.glsl"));
            shaderManager.createFragmentShader(ResourceLoader.loadShader("shaders/main-fragment.glsl"));
            shaderManager.createVertexShader(1, ResourceLoader.loadShader("shaders/sphere-vertex.glsl"));
            shaderManager.createFragmentShader(1, ResourceLoader.loadShader("shaders/sphere-fragment.glsl"));
            link(0);
            createGameUniforms();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void renderSpecific(int index, ObjectLoader loader, Model m) {
        renderBegin(index);
        renderModel(loader, m);
        renderEnd();
    }

    public void renderInstances(ObjectLoader loader) {
        renderBegin(0);

        for(int i=0; i<loader.getModelCapacity(); i++) {
            renderModel(loader, loader.getModel(i));
        }
      
        renderEnd();
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

    public void renderSphere(SphereObject object) {
        sphereBlock.set(object);
        bind("SphereBlock", this.sphereBlock);
    }

    private void renderModel(ObjectLoader loader, Model m) {

        // Receive our components
        int ID = loader.getID(m.getIndex());
        int indicesCount = loader.getIndicesCount(m.getIndex());
        int textureID = loader.getTextures(m.getTexIndex());

        // Add model matrix
        shaderManager.setMatrixUniform("model", m.getModelMatrix());

        // Update uniform texture sampler
        shaderManager.setIntUniform("textureSampler", 0);

        // Bind VAO
        GL30.glBindVertexArray(ID);

        // Bind the element buffer object (EBO) for the indices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, loader.getEBO(m.getIndex()));

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
        GL21.glBindTexture(GL15.GL_TEXTURE_2D, 0);
    }

    private void renderBegin(int index) {
        link(0);
        shaderManager.bind(index);
        camMatrices.update(0.05f, 160.0f);
        renderCamera();

        //camMatrices.projectionMatrix.identity();
        //camMatrices.viewMatrix.identity();
    }

    private void renderEnd() {
        unbind(this.camBlock);
        shaderManager.unbind();
    }

    private void createGameUniforms() throws Exception {
        shaderManager.createUniform("textureSampler");
        shaderManager.setMatrixUniform("model", new Matrix4f().identity());
        shaderManager.createUniform("model");


        // shaderManager.setVec3Uniform("camPosition", camMatrices.getPosition());
        // shaderManager.createUniform(1, "camPosition");
    }

    private void link(int index) {
        if(__dirtyLink == index) {
            return;
        }

        __dirtyLink = index;

        try {
            shaderManager.link(index);
        }catch (Exception e) {
            return;
        }
    }
}