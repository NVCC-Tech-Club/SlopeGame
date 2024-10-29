package com.slope.game.utils

import com.slope.game.objs.Object
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer


// The `PropModel` class is suppose to be a background object that does not interact with the game at all
// No physics, just there as like horizon models like the green towers.
// The `PropModel` is also used for the screen since the screen does not interact with the game, just displays
// the current framebuffer, that's it!
// That's why this class is very bare bone since there's no purpose for it other than less emptyness.
// @author Diego Fonseca
data class PropModel(
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