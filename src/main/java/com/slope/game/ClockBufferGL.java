package com.slope.game;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class ClockBufferGL extends CircularClockBuffer {
    private int VAO;
    private int EBO;
    private int VBO;

    public ClockBufferGL(int size, int slots) {
        super(size, slots);

        this.VAO = GL30.glGenVertexArrays();
        this.EBO = GL30.glGenBuffers();
        this.VBO = GL30.glGenBuffers();
    }

    public void renderStage() {
        MemoryStack stack = MemoryStack.stackPush();
        try {
            stack.push();
            ByteBuffer capacityVBOBuffer = stack.malloc(totalVBOSize);

            stack.push();
            ByteBuffer capacityEBOBuffer = stack.malloc(totalEBOSize);
            int curVBOCapacity = 0;
            int curEBOCapacity = 0;

            for(int i=0; i<getSlots(); i++) {
                byte[] currentBytes = get();

                // Unlike `SizedShaderBlock` I am fully utilizing the stack.
                // What I mean is that I need more than one item, so I need to manage
                // my memory stack carefully.
                stack.push();
                ByteBuffer stackBuffer = stack.malloc(getSizePerSlot());
                stackBuffer.get(currentBytes);

                // Let's organzie our info variables at the vary bottom of the stack.
                final int eboSize = stackBuffer.getInt(0);
                final int vboSize = stackBuffer.getInt(Integer.BYTES);

                capacityVBOBuffer.put(curVBOCapacity, stackBuffer, Integer.BYTES * 2, vboSize);
                capacityEBOBuffer.put(curEBOCapacity, stackBuffer, (Integer.BYTES * 2) + vboSize, eboSize);

                stack.pop();
                curVBOCapacity += vboSize;
                curEBOCapacity += eboSize;
            }

            GL30.glBindVertexArray(VAO);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
            GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);

            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, capacityVBOBuffer, GL15.GL_DYNAMIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL21.GL_FLOAT, false, 9 * Float.BYTES, 0);
            GL20.glVertexAttribPointer(1, 2, GL21.GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
            GL20.glVertexAttribPointer(2, 4, GL21.GL_FLOAT, false, 9 * Float.BYTES, 5 * Float.BYTES);

            GL30.glBindVertexArray(0);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        } finally {
            stack.pop();
            stack.pop();
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        GL30.glDeleteVertexArrays(VAO);
        GL30.glDeleteBuffers(EBO);
        GL30.glDeleteBuffers(VBO);
    }
}