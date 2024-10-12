package com.slope.game.utils

import org.joml.Matrix4f
import org.joml.Vector3f

class Matrix {

//    position ,scale and rotation
    var position: Vector3f = Vector3f(0f, 0f, 0f)
    var scale: Vector3f = Vector3f(1f, 1f, 1f)
    var rotation: Vector3f = Vector3f(0f, 0f, 0f)
    private val modelMatrix: Matrix4f = Matrix4f()




    fun update() {
        modelMatrix.identity()
            .translate(position)
            .rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
            .scale(scale)
    }

    fun  getModelMatrix(): Matrix4f{
        return modelMatrix
    }
}