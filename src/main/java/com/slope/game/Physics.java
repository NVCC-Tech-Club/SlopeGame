package com.slope.game;

import org.joml.Vector3f;

public class Physics {
    private static final Vector3f c = new Vector3f(0.0f, 5.0f, 0.0f);
    private static final float GRAVITY_STRENGTH = -0.1f;
    private static Vector3f sphere_pos = new Vector3f(0.0f, 0.0f, 0.0f);

    public static Vector3f getPosition() { // Gravity Pull
        sphere_pos.set(sphere_pos.x, sphere_pos.y + GRAVITY_STRENGTH * Engine.getMain().getITime(), sphere_pos.z);
        //System.out.println(sphere_pos.y);
        return sphere_pos;
    }
}
