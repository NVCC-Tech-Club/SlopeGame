package com.slope.game;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {
    private static final int BIT_32_CAPACITY = 32;
    private static boolean RE = false;

    private List<SimpleEntry<Long, Long>> vertexData = new ArrayList<SimpleEntry<Long, Long>>();

    public void loadVertexObject(Shape sp) {
        long vertexAmount = (long) sp.getVertexAmount() / 3;
        int VAO = createVAO();
        int VBO = storeShapeInAttribList(sp);

        // Store VAO and vertex count in a 64-bit long (32 bits each)
        long vaoWithCount = ((long) VAO << BIT_32_CAPACITY) | (vertexAmount & 0xFFFFFFFFL);

        // Store VBO (shifted left) and a placeholder for instance VBO (-1 for now)
        long vboWithInstance = ((long) VBO << BIT_32_CAPACITY) | (-1L & 0xFFFFFFFFL);

        // Store both VAO and vertex count in a 64-bit datatype since 32 + 32 = 64.
        // Also include the VBO.
        vertexData.add(new SimpleEntry<Long, Long>(vaoWithCount, vboWithInstance));
    }

    private int createVAO() {
        int VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);
        return VAO;
    }

    private int storeShapeInAttribList(Shape sp) {
        int VBO = GL15.glGenBuffers();

        // Bind buffer object to element array (Basically indices)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, sp.storeIndicesInBuffer(), GL15.GL_STATIC_DRAW);

        // Bind buffer object to array
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, sp.storeVerticesInBuffer(), GL15.GL_STATIC_DRAW);

        // Set vertex attribute pointer for the shape
        GL20.glVertexAttribPointer(0, sp.getVertexCount(), GL21.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return VBO;
    }

    public int getCapacity() {
        return vertexData.size();
    }

    // Get the left most 32-bit chunk which is our VAO.
    public int getID(int index) {
        return (int) (vertexData.get(index).getKey() >> BIT_32_CAPACITY);
    }

    // Get the VBO by extracting the left-most 32 bits.
    public int getVBO(int index) {
        return (int) (vertexData.get(index).getValue() >> BIT_32_CAPACITY);
    }

    // Get the instance VBO by masking the lower 32 bits.
    public int getInstanceVBO(int index) {
        return (int) (vertexData.get(index).getValue() & 0xFFFFFFFFL);
    }

    // Get the vertex count by masking the lower 32 bits.
    public int getVertexCount(int index) {
        return (int) (vertexData.get(index).getKey() & 0xFFFFFFFFL);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void destroy() {
        while(getCapacity() != 0) {
            GL30.glDeleteVertexArrays(getID(0));
            GL30.glDeleteBuffers(getVBO(0));
            vertexData.remove(0);
        }
    }
}