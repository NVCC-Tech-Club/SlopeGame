package com.slope.game.objs;

import java.nio.ByteBuffer;
import java.util.ArrayList; // Import your ShaderManager
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.slope.game.CameraMatrices;
import com.slope.game.ShaderManager;

public class SphereObject extends Object {
    private CameraMatrices camMatrices;
    private ShaderManager shaderManager; 
    private int vaoID, vboID;
    private List<Float> vertices; // Storing vertices dynamically
    private int[] indices;

    public SphereObject(CameraMatrices camMatrices, ShaderManager shaderManager) {
        this.camMatrices = camMatrices;
        this.shaderManager = shaderManager; // Initialize ShaderManager
        createSphere(1.0f, 36, 18); // radius, sectors, stacks
        setupVAO(); // Set up the VAO and VBO
    }

    private void createSphere(float radius, int sectors, int stacks) {
        float x, y, z, xy; // vertex position
        float sectorStep = 2 * (float) Math.PI / sectors;
        float stackStep = (float) Math.PI / stacks;

        vertices = new ArrayList<>(); 

        for (int i = 0; i <= stacks; ++i) {
            float stackAngle = (float) Math.PI / 2 - i * stackStep; // starting from pi/2 to -pi/2
            xy = radius * (float) Math.cos(stackAngle); // r * cos(u)
            z = radius * (float) Math.sin(stackAngle); // r * sin(u)

            for (int j = 0; j <= sectors; ++j) {
                float sectorAngle = j * sectorStep;

                // Vertex position
                x = xy * (float) Math.cos(sectorAngle); // r * cos(u) * cos(v)
                y = xy * (float) Math.sin(sectorAngle); // r * cos(u) * sin(v)
                vertices.add(x);
                vertices.add(y);
                vertices.add(z);
            }
        }

        
        indices = new int[stacks * sectors * 6]; // 6 indices per quad
        int index = 0;

        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < sectors; j++) {
                int first = (i * (sectors + 1)) + j;
                int second = first + sectors + 1;

                // First triangle
                indices[index++] = first;
                indices[index++] = second;
                indices[index++] = first + 1;

                // Second triangle
                indices[index++] = second;
                indices[index++] = second + 1;
                indices[index++] = first + 1;
            }
        }
    }

    private void setupVAO() {
        // Creating VAO
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
    
        // Creating VBO for vertices
        vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    
        // Filling up the VBO with vertex data
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_STATIC_DRAW);
    
        // Setting up the vertex attributes
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);
    
        // Creating EBO for indices
        int eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
    
        // Unbinding VBO and VAO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }
    

    public void render() {
        // Binding shader
        shaderManager.bind();

        // Update uniforms if needed
        shaderManager.setMatrixUniform("projection", camMatrices.getProjectionMatrix());
        shaderManager.setMatrixUniform("model", getModelMatrix());

        // Binding VAO
        GL30.glBindVertexArray(vaoID);
        
        // Drawing the sphere using the indices
        GL30.glDrawElements(GL11.GL_TRIANGLES, indices.length, GL11.GL_UNSIGNED_INT, 0); 

        // Unbind VAO
        GL30.glBindVertexArray(0);
        
        
        shaderManager.unbind();
    }

    public void write(ByteBuffer buffer) {
        this.camMatrices.getProjectionMatrix().get(0, buffer);
        this.getModelMatrix().get(Float.BYTES * 16, buffer);
    }
}
