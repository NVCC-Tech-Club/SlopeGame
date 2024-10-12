package com.slope.game.utils

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer

data class Model(val vertices: FloatArray, val indices: IntArray, val texCoord: FloatArray) {
    fun storeIndicesInBuffer(): IntBuffer? {
        val buffer = BufferUtils.createIntBuffer(indices.size)
        buffer.put(indices).flip()
        return buffer
    }

    fun storeVerticesInBuffer(): FloatBuffer? {
        return storeDataInBuffer(vertices)
    }

    fun storeTexCoordsInBuffer(): FloatBuffer? {
        return storeDataInBuffer(texCoord)
    }

    private fun storeDataInBuffer(obj: FloatArray): FloatBuffer? {
        val buffer = BufferUtils.createFloatBuffer(obj.size)
        buffer.put(obj).flip()
        return buffer
    }
}