package com.slope.game;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class UniformBlockManager implements IComponentManager {
    private final Object2IntMap<SizedShaderBlock<?>> boundedBlocks;
    private final IntSet usedBindings;

    public UniformBlockManager() {
        this.boundedBlocks = new Object2IntArrayMap<>();
        this.usedBindings = new IntOpenHashSet();
    }

    public int bind(SizedShaderBlock<?> block) {
        if (!(block instanceof SizedShaderBlock<?>)) {
            throw new UnsupportedOperationException("Unable To Bind " + block.getClass());
        }

        return 0;
    }

    @Override
    public void init() {

    }

    @Override
    public void render() {

    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public IComponent addComponent(IComponent component, Class<IGraphics> handler) {
        return null;
    }
}
