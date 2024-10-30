package com.slope.game;

import com.slope.game.utils.BufferModel;

// Using the concept of a circular buffer
// The different is we can only update our read index for every completed cycle of write.
abstract class CircularClockBuffer extends BufferModel {
    private int slots;
    private int sizePerSlot;
    private int writeIndex;
    private int readIndex;
    private byte[] readOnlyCache;
    private boolean __hasRead;

    // Buffer Objects


    public CircularClockBuffer(int size, int slots) {
        super(size * slots);
        this.readOnlyCache = new byte[size];

        this.slots = slots;
        this.sizePerSlot = size;
        this.writeIndex = 0;
        this.readIndex = 0;
        this.__hasRead = false;
    }

    public void put(BufferModel buffer) {
        int newWriteIndex = (writeIndex + 1) % slots;
        int bufferSize = buffer.getSize();

        if((getRawBuffer().getInt(newWriteIndex * sizePerSlot) == 0 || writeIndex == readIndex) && __hasRead) {
            getRawBuffer().put(writeIndex, buffer.getRawBuffer(), 0, bufferSize);
            writeIndex = newWriteIndex;
            __hasRead = false;
        }
    }

    protected byte[] get() {
        getRawBuffer().get(readOnlyCache, readIndex * sizePerSlot, sizePerSlot);
        readIndex = (readIndex + 1) % slots;
        __hasRead = true;

        return readOnlyCache;
    }

    public int getSlots() {
        return slots;
    }

    public int getSizePerSlot() {
        return sizePerSlot;
    }
}