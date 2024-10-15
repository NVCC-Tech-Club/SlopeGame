package com.slope.game.utils

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
    val colorArray: FloatArray // Added colorArray
) {
    var index: Int = 0

    // Position, scale, and rotation
    private var position: Vector3f = Vector3f(0f, 0f, 0f)
    private var scale: Vector3f = Vector3f(1f, 1f, 1f)
    private var rotation: Vector3f = Vector3f(0.0f, 0.0f, 0.0f)

    // Model Matrix
    private val modelMatrix: Matrix4f = Matrix4f()

    fun rotate(x: Float, y: Float, z: Float) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    fun scale(x: Float, y: Float, z: Float) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
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

    // New method to store color data in a buffer
    fun storeColorsInBuffer(): FloatBuffer? {
        return storeDataInBuffer(colorArray)
    }

    fun update() {
        modelMatrix.identity()
            .translate(position)
            .rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
            .scale(scale)
    }

    private fun storeDataInBuffer(obj: FloatArray): FloatBuffer? {
        val buffer = BufferUtils.createFloatBuffer(obj.size)
        buffer.put(obj).flip()
        return buffer
    }

    // Getting the transformation matrix back
    fun getModelMatrix(): Matrix4f {
        return modelMatrix
    }
}