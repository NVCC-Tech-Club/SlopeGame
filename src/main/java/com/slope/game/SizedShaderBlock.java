package com.slope.game;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

import com.slope.game.utils.Validate;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;

public class SizedShaderBlock<T> {
    private final int binding;
    private final BiConsumer<T, ByteBuffer> serializer;
    private final RenderManager renderer;
    private int buffer;
    private int size;
    private T value;
    private boolean dirty;

    public SizedShaderBlock(RenderManager renderer, int binding, int size, BiConsumer<T, ByteBuffer> serializer) {
        this.binding = binding;
        this.serializer = serializer;
        this.renderer = renderer;
        this.size = size;
        this.value = null;
    }

    public void setValue(@Nullable T value) {
        this.value = value;
        this.dirty = true;
    }

    public void bind(int index) {
        Validate.inclusiveBetween(0, RenderManager.maxGLBindings(GL31.GL_UNIFORM_BUFFER), index);

        // If the buffer is yet to be initialized, then create a new buffer object
        // and bind it as whatever buffer type. Since this is for a uniform shader
        // block, the buffer is updating constantly which is why I used `GL_DYNAMIC_DRAW`
        // and given the fixed size.
        if(buffer == 0) {
            buffer = GL30.glGenBuffers();
            GL30.glBindBuffer(binding, buffer);
            GL30.glBufferData(binding, size, GL_DYNAMIC_DRAW);
            GL30.glBindBuffer(binding, 0);
            dirty = true;
        }

        if(dirty) {
            dirty = false;
            GL30.glBindBuffer(binding, buffer);

            // Have a checker to see if we can directly allocate to the stack.
            try(MemoryStack stack = MemoryStack.stackPush()) {

                // If given value, then add it as a component of our uniform
                // shader block, that's why it's sub data and not just data.
                if(value != null) {

                    // No, this is not a heap allocator, the C equivalent
                    // to this `stack.malloc` is `alloca` since we are allocating
                    // from the stack, not the heap.
                    ByteBuffer stackBuffer = stack.malloc(size);
                    serializer.accept(value, stackBuffer);
                    stackBuffer.rewind();
                    glBufferSubData(binding, 0, stackBuffer);
                }else {
                    // If not given a value, then just fill our component with
                    // some "filler memory."
                    glBufferSubData(binding, 0, stack.calloc(0));
                }
            }

            // We are done here, let's unbind
            glBindBuffer(binding, 0);
        }

        // Bind to a specific bind slot on the GPU via. (layout binding = index)
        glBindBufferBase(binding, index, buffer);
    }

    public void unbind(int index) {
        Validate.inclusiveBetween(0, RenderManager.maxGLBindings(binding), index);

        // Unbind to a specific bind slot on the GPU via. (layout binding = index)
        glBindBufferBase(binding, index, 0);
    }

    public void set(@Nullable T value) {
        this.value = value;
        this.dirty = true;
    }

    public int getBinding() {
        return binding;
    }

    public void destroy() {
        renderer.unbind(this);
        if(buffer != 0) {
            glDeleteBuffers(buffer);
            this.buffer = 0;
        }
    }
}
