package com.slope.game;
import java.util.Locale;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import com.slope.game.utils.Model;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.Locale;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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

    private Model screen;
    private UniformBlockState uniformBlockState;
    private ShaderManager shaderManager;
    private Vector2f resolution;
    private int __dirtyLink = -1;

    // Sphere Stuff
    private final SizedShaderBlock<Sphere> sphereBlock;

    // Camera Stuff.
    private final SizedShaderBlock<CameraMatrices> camBlock;
    private final CameraMatrices camMatrices;

    public RenderManager(CameraMatrices camMatrices) {
        this.camMatrices = camMatrices;
        this.camBlock = new SizedShaderBlock<>(this, GL_UNIFORM_BUFFER, CameraMatrices.SIZE, CameraMatrices::write);
        this.sphereBlock = new SizedShaderBlock<>(this, GL_UNIFORM_BUFFER, Sphere.SIZE, Sphere::write);

        {
            this.resolution = new Vector2f(0, 0);
        }
    }

    public void init() {
        shaderManager = new ShaderManager();
        uniformBlockState = new UniformBlockState(shaderManager);

        {
            final int width = Engine.getMain().getPrimaryWindow().getFramebufferWidth();
            final int height = Engine.getMain().getPrimaryWindow().getFramebufferHeight();
            this.resolution = new Vector2f(width, height);
        }

        try {
            shaderManager.createShaderProgram();
            shaderManager.createVertexShader(ResourceLoader.loadShader("shaders/main-vertex.glsl"));
            shaderManager.createFragmentShader(ResourceLoader.loadShader("shaders/main-fragment.glsl"));
            shaderManager.createVertexShader(1, ResourceLoader.loadShader("shaders/sphere-vertex.glsl"));
            shaderManager.createFragmentShader(1, ResourceLoader.loadShader("shaders/sphere-fragment.glsl"));

            shaderManager.createShaderProgram();
            shaderManager.createVertexShader(2, ResourceLoader.loadShader("shaders/tower-vertex.glsl"));
            shaderManager.createFragmentShader(2, ResourceLoader.loadShader("shaders/tower-fragment.glsl"));

            createGameUniforms();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void renderInstances(ObjectLoader loader) {
        shaderManager.bind(0);
        camMatrices.update(CameraMatrices.Z_NEAR, CameraMatrices.Z_FAR);
        renderCamera();

        int size = loader.getModelCapacity();
        for(int i=0; i<size; i++) {

            // Receive our components
            Model m = loader.getModel(i);

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

        shaderManager.bind(2);
        size = loader.getModelInstanceCapacity();

        for(int i=0; i<size; i++) {

            // Receive our components
            Model m = loader.getModelInstance(i);

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

        unbind(this.camBlock);
    }

    public void renderScreen(int programIndex, Sphere sphere, ObjectLoader loader) {
        if(screen == null) {
            return;
        }

        shaderManager.bind(programIndex);
        int ID = loader.getID(screen.getIndex());
        int textureID = screen.getTexIndex() & 0xFF;
        int depthTextureID = screen.getTexIndex() >> ObjectLoader.BIT_16_CAPACITY;

        // Bind VAO
        GL30.glBindVertexArray(ID);

        // Add model matrix
        camMatrices.update(CameraMatrices.Z_NEAR, CameraMatrices.Z_FAR);
        renderCamera();

        // Add resolution vector
        {
            shaderManager.setVec2Uniform("iResolution", resolution);
            shaderManager.setVec3Uniform("camPosition", camMatrices.getPosition());
        }

        // Update uniform texture sampler
        shaderManager.setIntUniform("textureSampler0", 0);
        shaderManager.setIntUniform("textureSampler1", 1);
        shaderManager.setIntUniform("textureSampler2", 2);

        // Enable the vertex attribute array.
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        // Active our texture 0
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, textureID);

        // Active our texture 1
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, loader.getTextures(0));

        // Active our texture 2
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, depthTextureID);
        //System.out.println(depthTextureID);

        // Draw the vertices as triangles.
        GL21.glDrawArrays(GL21.GL_TRIANGLES, 0, screen.getVertices().length);

        // Disable our attributes
        GL20.glDisableVertexAttribArray(0);

        // Unbind the texture.
        GL21.glBindTexture(GL21.GL_TEXTURE_2D, 0);

        // Unbind the VAO to avoid any accidental changes.
        GL30.glBindVertexArray(0);

        unbind(this.camBlock);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
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

    public void begin() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void destroy() {
        shaderManager.destroy();
    }

    private void renderCamera() {
        camBlock.set(camMatrices);
        bind("CameraMatrices", this.camBlock);
    }

    public void renderSphere(Sphere object) {
        sphereBlock.set(object);
        bind("SphereBlock", this.sphereBlock);
    }

    public void onWindowResize(int width, int height) {
        resolution.set(width, height);
    }

    public void end() {
        shaderManager.unbind();
    }

    private void createGameUniforms() throws Exception {
        link(0);
        link(1);
        link(2);

        shaderManager.bind(0);
        shaderManager.createUniform("textureSampler");
        shaderManager.createUniform("model");

        shaderManager.bind(1);
        shaderManager.createUniform(1,"iResolution");
        shaderManager.createUniform(1, "camPosition");
        shaderManager.createUniform(1, "textureSampler0");
        shaderManager.createUniform(1, "textureSampler1");
        shaderManager.createUniform(1, "textureSampler2");

        shaderManager.bind(2);
        shaderManager.createUniform(2, "textureSampler");
        shaderManager.createUniform(2, "model");

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