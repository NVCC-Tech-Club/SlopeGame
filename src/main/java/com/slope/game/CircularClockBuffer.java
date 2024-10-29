package com.slope.game;

import com.slope.game.utils.BufferModel;

public class CircularClockBuffer extends BufferModel {
    private int slots;
    private int sizePerSlot;

    public CircularClockBuffer(int size, int slots) {
        super(size * slots);

        this.slots = slots;
        this.sizePerSlot = size;
    }
}
