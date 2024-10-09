package com.slope.game;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import com.slope.game.utils.Validate;
import org.lwjgl.opengl.GL31;

public class SizedShaderBlock<T> implements IGraphics {
    private final int binding;
    private final BiConsumer<T, ByteBuffer> serializer;
    private int buffer;
    private int size;
    private T value;
    private boolean dirty;

    public SizedShaderBlock(int binding, int size, BiConsumer<T, ByteBuffer> serializer) {
        this.binding = binding;
        this.serializer = serializer;
        this.size = size;
    }

    public void bind(int index) {
        Validate.inclusiveBetween(0, RenderManager.maxGLBindings(GL31.GL_UNIFORM_BUFFER), index);
    }

    @Override
    public void unbind() {

    }

    @Override
    public void destroy() {

    }
}
