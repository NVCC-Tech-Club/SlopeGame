package com.slope.game.objs;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.slope.game.CameraMatrices;
import com.slope.game.ShaderManager;
import com.slope.game.FrameBuffer; 

public class SphereObject extends Object {
    private CameraMatrices camMatrices;
    private ShaderManager shaderManager;
    private FrameBuffer frameBuffer; 
    private int vaoID, vboID;
    private List<Float> vertices;
    private int[] indices;

    public SphereObject(CameraMatrices camMatrices, ShaderManager shaderManager, FrameBuffer frameBuffer) {
        this.camMatrices = camMatrices;
        this.shaderManager = shaderManager;
        this.frameBuffer = frameBuffer; // Initializing FrameBuffer
        createSphere(1.0f, 36, 18); // radius, sectors, stacks
        setupVAO(); // Set up the VAO and VBO
    }

    private void createSphere(float radius, int sectors, int stacks) {
        float x, y, z, xy;
        float sectorStep = 2 * (float) Math.PI / sectors;
        float stackStep = (float) Math.PI / stacks;

        vertices = new ArrayList<>();

        for (int i = 0; i <= stacks; ++i) {
            float stackAngle = (float) Math.PI / 2 - i * stackStep;
            xy = radius * (float) Math.cos(stackAngle);
            z = radius * (float) Math.sin(stackAngle);

            for (int j = 0; j <= sectors; ++j) {
                float sectorAngle = j * sectorStep;

                x = xy * (float) Math.cos(sectorAngle);
                y = xy * (float) Math.sin(sectorAngle);
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
            }
        }

        indices = new int[stacks * sectors * 6];
        int index = 0;

        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < sectors; j++) {
                int first = (i * (sectors + 1)) + j;
                int second = first + sectors + 1;

                indices[index++] = first;
                indices[index++] = second;
                indices[index++] = first + 1;

                indices[index++] = second;
                indices[index++] = second + 1;
                indices[index++] = first + 1;
            }
        }
    }

    private void setupVAO() {
        // Bind the FrameBuffer before setting up the VAO
        frameBuffer.bind(); // Bind the FBO

        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        int eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        // Unbind FBO
        frameBuffer.unbind();
    }

    public void render() {
        shaderManager.bind();

        shaderManager.setMatrixUniform("projection", camMatrices.getProjectionMatrix());
        shaderManager.setMatrixUniform("model", getModelMatrix());

        // Bind the FrameBuffer and use its texture
        frameBuffer.bindTexture();

        GL30.glBindVertexArray(vaoID);
        GL30.glDrawElements(GL11.GL_TRIANGLES, indices.length, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

        frameBuffer.unbindTexture(); // Unbind the texture after rendering

        shaderManager.unbind();
    }

    public void write(ByteBuffer buffer) {
        camMatrices.getProjectionMatrix().get(0, buffer);
        getModelMatrix().get(Float.BYTES * 16, buffer);
    }
}
