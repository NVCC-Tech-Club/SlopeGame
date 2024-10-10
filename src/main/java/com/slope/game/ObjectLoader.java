package com.slope.game;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader implements IGraphics {
    private static final int BIT_32_CAPACITY = 32;
    private static boolean RE = false;

    private List<Long> vaoList = new ArrayList<Long>();
    private List<Integer> vboList = new ArrayList<Integer>();
    private List<Long> eboList = new ArrayList<Long>();

    public void loadVertexObject(Shape sp) {
        long vertexAmount = (long) sp.getVertexAmount() / 3;
        int VAO = createVAO();
        int EBO = storeIndexInAttribList(sp);
        storeDataInAttribList(sp.storeVerticesInBuffer(),0, 3);
        storeDataInAttribList(sp.storeTexCoordsInBuffer(), 1, 2);

        // Store VAO and vertex count in a 64-bit long (32 bits each)
        long vaoWithCount = ((long) VAO << BIT_32_CAPACITY) | (vertexAmount & 0xFFFFFFFFL);
        long eboWithCount = ((long) EBO << BIT_32_CAPACITY) | (sp.getIndexCount() & 0xFFFFFFFFL);

        // Store both VAO and vertex count in a 64-bit datatype since 32 + 32 = 64.
        // Also include the VBO.
        vaoList.add(vaoWithCount);
        eboList.add(eboWithCount);
    }

    private int createVAO() {
        int VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);
        return VAO;
    }

    private void storeDataInAttribList(FloatBuffer buffer, int index, int size) {
        int VBO = GL15.glGenBuffers();
        vboList.add(VBO);

        // Bind buffer object to array
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Set vertex attribute pointer for the shape
        GL20.glVertexAttribPointer(index, size, GL21.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttribList(IntBuffer buffer, int index, int size) {
        int VBO = GL15.glGenBuffers();
        vboList.add(VBO);

        // Bind buffer object to array
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Set vertex attribute pointer for the shape
        GL20.glVertexAttribPointer(index, size, GL21.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private int storeIndexInAttribList(Shape sp) {
        int EBO = GL15.glGenBuffers();

        // Bind buffer object to element array (Basically indices)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, sp.storeIndicesInBuffer(), GL15.GL_STATIC_DRAW);
        return EBO;
    }

    public int loadTexture(String filename) {
        int width, height;
        ByteBuffer buffer;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            // STBImage.
        }

        return 0;
    }

    public int getCapacity() {
        return vaoList.size();
    }

    // Get the left most 32-bit chunk which is our VAO.
    public int getID(int index) {
        return (int) (vaoList.get(index) >> BIT_32_CAPACITY);
    }

    // Get the VBO by extracting the left-most 32 bits.
    public int getVBO(int index) {
        return vboList.get(index);
    }

    public int getEBO(int index) {
        return (int) (eboList.get(index) >> BIT_32_CAPACITY);
    }

    public int getIndicesCount(int index) {
        return (int) (eboList.get(index) & 0xFFFFFFFFL);
    }

    // Get the vertex count by masking the lower 32 bits.
    public int getVertexCount(int index) {
        return (int) (vaoList.get(index) & 0xFFFFFFFFL);
    }

    @Override
    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    @Override
    public void destroy() {
        while(getCapacity() != 0) {
            GL30.glDeleteVertexArrays(getID(0));
            vaoList.remove(0);
        }

        while(vboList.size() != 0) {
            GL30.glDeleteBuffers(getVBO(0));
            vboList.remove(0);
        }

        while(eboList.size() != 0) {
            GL30.glDeleteBuffers(getEBO(0));
            eboList.remove(0);
        }
    }
}