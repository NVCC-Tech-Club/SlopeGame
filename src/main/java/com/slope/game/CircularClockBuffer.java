package com.slope.game;

import com.slope.game.utils.BufferModel;

import java.nio.ByteBuffer;

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

    public void put(ByteBuffer buffer) {
        int newWriteIndex = (writeIndex + 1) % sizePerSlot;
        byte[] rawBytes;
        //getRawBuffer().get(rawBytes, sizePerSlot * newWriteIndex, );

        //if(newWriteIndex == null || ) {

        //}
    }
}
