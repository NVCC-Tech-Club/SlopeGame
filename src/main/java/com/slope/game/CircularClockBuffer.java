package com.slope.game;

import com.slope.game.utils.BufferModel;

public class CircularClockBuffer extends BufferModel {
    private int slots;
    private int sizePerSlot;
    private int writeIndex;
    private int readIndex;

    public CircularClockBuffer(int size, int slots) {
        super(size * slots);

        this.slots = slots;
        this.sizePerSlot = size;
        this.writeIndex = 0;
        this.readIndex = 0;
    }

    public void put(BufferModel buffer) {
        int newWriteIndex = (writeIndex + 1) % slots;
        int bufferSize = buffer.getSize();

        if(getRawBuffer().getInt(newWriteIndex * sizePerSlot) == 0 || writeIndex == readIndex) {
            getRawBuffer().put(writeIndex, buffer.getRawBuffer(), 0, bufferSize);
            writeIndex = newWriteIndex;
        }
    }

    public int getSlots() {
        return slots;
    }
}