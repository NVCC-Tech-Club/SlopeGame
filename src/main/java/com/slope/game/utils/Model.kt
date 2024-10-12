package com.slope.game.utils

import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.joml.Matrix4f
import org.joml.Vector3f

data class Model(val vertices: FloatArray, val indices: IntArray, val texCoord: FloatArray) {
    private val transformationMatrix = Matrix()

    // transformation
    var position: Vector3f
        get() = transformationMatrix.position
        set(value) {
            transformationMatrix.position.set(value)
            transformationMatrix.update()
        }

    var rotation: Vector3f
        get() = transformationMatrix.rotation
        set(value) {
            transformationMatrix.rotation.set(value)
            transformationMatrix.update()
        }

    var scale: Vector3f
        get() = transformationMatrix.scale
        set(value) {
            transformationMatrix.scale.set(value)
            transformationMatrix.update()
        }

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

    // getting the transformation matrix back
    fun getTransformationMatrix(): Matrix4f {
        return transformationMatrix.getModelMatrix()
    }
}
