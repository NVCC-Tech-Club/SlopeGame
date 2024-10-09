package com.slope.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.lwjgl.opengl.GL31;

import java.util.Objects;

import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class UniformBlockState {
    private final Object2IntMap<SizedShaderBlock<?>> boundedBlocks;
    private final Int2ObjectMap<CharSequence> shaderBindings;
    private final IntSet usedBindings;
    private final ShaderManager manager;
    private int nextBinding;

    public UniformBlockState(ShaderManager manager) {
        this.boundedBlocks = new Object2IntArrayMap<>();
        this.shaderBindings = new Int2ObjectArrayMap<>();
        this.usedBindings = new IntOpenHashSet();
        this.manager = manager;
    }

    public void bind(CharSequence name, SizedShaderBlock<?> block) {
        if (!(block instanceof SizedShaderBlock<?>)) {
            throw new UnsupportedOperationException("Unable To Bind " + block.getClass());
        }

        int binding = this.bind(block);
        CharSequence boundName = this.shaderBindings.get(binding);
        
        if(!Objects.equals(name, boundName)) {
            shaderBindings.put(binding, name);

            switch(block.getBinding()) {
                case GL_UNIFORM_BUFFER -> manager.setUniformBlock(name, binding);
                case GL_SHADER_STORAGE_BUFFER -> manager.setStorageBlock(name, binding);
            }
        }
    }

    public int bind(SizedShaderBlock<?> block) {
        if (!(block instanceof SizedShaderBlock<?>)) {
            throw new UnsupportedOperationException("Unable To Bind " + block.getClass());
        }

        // Check to so if the block has already been binded by checking the boundedBlocks.
        int binding = this.boundedBlocks.getOrDefault(block, -1);
        if(binding == -1) {

            // Check to see if the next bind exceeds the maximum capacity.
            if(nextBinding >= RenderManager.maxGLBindings(GL_UNIFORM_BUFFER)) {
                this.freeBindings();
            }

            binding = nextBinding;
            boundedBlocks.put(block, binding);

            //Next the next free bind slot.
            while(boundedBlocks.containsValue(nextBinding)) {
                this.nextBinding++;
            }
        }

        block.bind(binding);
        usedBindings.add(binding);
        return binding;
    }

    public void destroy() {
        this.usedBindings.clear();
    }

    private void unbind(int binding, SizedShaderBlock<?> block) {
        block.unbind(binding);

        CharSequence name = shaderBindings.remove(binding);
        if(name != null) {
            switch(block.getBinding()) {
                case GL_UNIFORM_BUFFER -> manager.setUniformBlock(name, 0);
                case GL_SHADER_STORAGE_BUFFER -> manager.setStorageBlock(name, 0);
            }
        }

        // Sort to get rid of empty slots.
        if(binding < nextBinding) {
            this.nextBinding = binding;
        }
    }

    // Looks for a useless bind slot that can be replaced.
    // Think it about it as a Garbage Collector for bindings.
    private void freeBindings() {

        // Create an iterator over the `boundedBlocks` map.
        ObjectIterator<Object2IntMap.Entry<SizedShaderBlock<?>>> iterator = this.boundedBlocks.object2IntEntrySet().iterator();

        // Starting from our `iterator` we remove bindings as we go.
        while(iterator.hasNext()) {
            Object2IntMap.Entry<SizedShaderBlock<?>> next = iterator.next();
            int binding = next.getIntValue();

            // If still being used, then skip.
            if(usedBindings.contains(binding)) {
                continue;
            }

            // If not still being used, then remove.
            unbind(binding, next.getKey());
            iterator.remove();
            this.nextBinding = binding;

            // Successful Return
            return;
        }

        throw new IllegalStateException("Too many shader blocks bounded, failed to find empty space!");
    }

    public void unbind(SizedShaderBlock<?> block) {
        if (!(block instanceof SizedShaderBlock<?>)) {
            throw new UnsupportedOperationException("Unable To Bind " + block.getClass());
        }

        if(boundedBlocks.containsKey(block)) {
            this.unbind(this.boundedBlocks.removeInt(block), block);
        }
    }
}