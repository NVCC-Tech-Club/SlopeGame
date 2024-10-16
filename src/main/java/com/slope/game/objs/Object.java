package com.slope.game.objs;

import com.slope.game.ObjectLoader;
import com.slope.game.utils.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Object {

    // Position, scale, and rotation
    private Vector3f position;
    private Vector3f scale;
    private Vector3f rotation;

    // Model Matrix
    private Matrix4f modelMatrix;

    public Object() {
        this.position = new Vector3f(0f, 0f, 0f);
        this.scale = new Vector3f(1f, 1f, 1f);
        this.rotation = new Vector3f(0.0f, 0.0f, 0.0f);
        this.modelMatrix = new Matrix4f();
    }

    public void rotate(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void scale(float x, float y, float z) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }

    public void update() {
        modelMatrix.identity()
                .translate(position)
                .rotateX((float)Math.toRadians(rotation.x))
                .rotateY((float)Math.toRadians(rotation.y))
                .rotateZ((float)Math.toRadians(rotation.z))
                .scale(scale);
    }

    // Getting the transformation matrix back
    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}