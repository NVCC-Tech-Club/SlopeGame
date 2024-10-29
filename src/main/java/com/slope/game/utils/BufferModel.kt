package com.slope.game.utils

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class BufferModel {
    private val rawBuffer: ByteBuffer;

    constructor(vertices: FloatArray, indices: IntArray, texCoord: FloatArray, colorArray: FloatArray) {
        val totalSize =
            (Int.SIZE_BYTES + vertices.size * Float.SIZE_BYTES) +
            (Int.SIZE_BYTES + indices.size * Float.SIZE_BYTES) +
            (Int.SIZE_BYTES + texCoord.size * Float.SIZE_BYTES) +
            (Int.SIZE_BYTES + colorArray.size * Float.SIZE_BYTES)
        var size: Int = 0;

        rawBuffer = MemoryUtil.memAlloc(totalSize)
        rawBuffer.putInt(size, vertices.size)
        size += Integer.BYTES

        for(i in 0 until vertices.size) {
            rawBuffer.putFloat(size, vertices.get(i))
            size += Float.SIZE_BYTES
        }

        rawBuffer.putInt(size, indices.size)
        size += Integer.BYTES

        for(i in 0 until indices.size) {
            rawBuffer.putInt(size, indices.get(i))
            size += Int.SIZE_BYTES
        }

        rawBuffer.putInt(size, texCoord.size)
        size += Integer.BYTES

        for(i in 0 until texCoord.size) {
            rawBuffer.putFloat(size, texCoord.get(i))
            size += Float.SIZE_BYTES
        }

        rawBuffer.putInt(size, colorArray.size)
        size += Integer.BYTES

        for(i in 0 until colorArray.size) {
            rawBuffer.putFloat(size, colorArray.get(i))
            size += Float.SIZE_BYTES
        }
    }

    fun getRawBuffer(): ByteBuffer {
        return rawBuffer;
    }

    fun destroy() {
        MemoryUtil.memFree(rawBuffer);
    }
}