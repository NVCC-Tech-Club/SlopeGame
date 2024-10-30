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
        GL30.glBindVertexArray(VAO);

        MemoryStack stack = MemoryStack.stackPush();
        try {
            stack.push();
            ByteBuffer capacityVBOBuffer = stack.malloc(totalVBOSize);
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

                {
                    stack.push();
                    capacityVBOBuffer.put(curVBOCapacity, stackBuffer, Integer.BYTES * 2, vboSize);
                    stack.pop();
                }

                {
                    stack.push();
                    capacityEBOBuffer.put(curEBOCapacity, stackBuffer, (Integer.BYTES * 2) + vboSize, eboSize);
                    stack.pop();
                }

                stack.pop();
                curVBOCapacity += vboSize;
                curEBOCapacity += eboSize;
            }
        } finally {
            stack.pop();
        }

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void destroy() {
        super.destroy();

        GL30.glDeleteVertexArrays(VAO);
        GL30.glDeleteBuffers(EBO);
        GL30.glDeleteBuffers(VBO);
    }
}