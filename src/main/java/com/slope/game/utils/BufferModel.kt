package com.slope.game.utils

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

open class BufferModel {
    var rawBuffer: ByteBuffer
        private set

    var size: Int = 0
        private set

    var vboSize: Int = 0
        private set
    
    var eboSize: Int = 0
        private set

    constructor(vertices: FloatArray, texCoord: FloatArray, colorArray: FloatArray, indices: IntArray) {
        val totalSize =
            Int.SIZE_BYTES * 2 +
            (vertices.size * Float.SIZE_BYTES) +
            (texCoord.size * Float.SIZE_BYTES) +
            (colorArray.size * Float.SIZE_BYTES) +
            (indices.size * Int.SIZE_BYTES)
        size = 0;
        vboSize = 
            (vertices.size * Float.SIZE_BYTES) +
            (texCoord.size * Float.SIZE_BYTES) +
            (colorArray.size * Float.SIZE_BYTES)
        eboSize = (indices.size * Int.SIZE_BYTES)

        rawBuffer = MemoryUtil.memAlloc(totalSize)

        rawBuffer.putInt(size, eboSize)
        size += Integer.BYTES

        rawBuffer.putInt(size, vboSize)
        size += Integer.BYTES

        for(i in 0 until vertices.size) {
            rawBuffer.putFloat(size, vertices.get(i))
            size += Float.SIZE_BYTES
        }

        for(i in 0 until texCoord.size) {
            rawBuffer.putFloat(size, texCoord.get(i))
            size += Float.SIZE_BYTES
        }

        for(i in 0 until colorArray.size) {
            rawBuffer.putFloat(size, colorArray.get(i))
            size += Float.SIZE_BYTES
        }

        for(i in 0 until indices.size) {
            rawBuffer.putInt(size, indices.get(i))
            size += Int.SIZE_BYTES
        }
    }

    constructor(rawSize: Int) {
        rawBuffer = MemoryUtil.memCalloc(rawSize)
        this.size = rawSize;
    }

    fun rewrite(buffer: ByteBuffer) {
        rawBuffer = buffer;
    }

    open fun destroy() {
        MemoryUtil.memFree(rawBuffer);
    }
}