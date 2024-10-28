package com.slope.game.utils

import com.slope.game.objs.Object
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.joml.Matrix4f
import org.joml.Vector3f

data class Model(
    val texIndex: Int,
    val vertices: FloatArray,
    val indices: IntArray,
    val texCoord: FloatArray,
    val colorArray: FloatArray, // Added colorArray
    val amount: Int
) : Object() {
    var index: Int = 0

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

    // New method to store color data in a buffer
    fun storeColorsInBuffer(): FloatBuffer? {
        return storeDataInBuffer(colorArray)
    }

    private fun storeDataInBuffer(obj: FloatArray): FloatBuffer? {
        val buffer = BufferUtils.createFloatBuffer(obj.size)
        buffer.put(obj).flip()
        return buffer
    }
}