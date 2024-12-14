package com.slope.game;

import org.joml.Vector3f;

public class Physics {
    private static final Vector3f c = new Vector3f(0.0f, 5.0f, 0.0f);
    private static final float GRAVITY_STRENGTH = -9.8f;
    private static Vector3f sphere_pos = new Vector3f(0.0f, 0.0f, 0.0f);
    private static float gravityVelocity = 0.0f;
    private static float gravityPos= 0.0f;

    private static float floor = -100.0f; //testing floor

    public static Vector3f getPosition() { // Gravity Pull
        //gravityPos = 0.5f * GRAVITY_STRENGTH * (Engine.getMain().getITime() * Engine.getMain().getITime());

        gravityVelocity += GRAVITY_STRENGTH * Engine.getMain().getFrameArea();
        gravityVelocity += checkFloor();
        //gravityPos = checkFloor();
        //System.out.println(gravityVelocity);
        //System.out.println(gravityPos);
        sphere_pos.set(sphere_pos.x, sphere_pos.y += gravityVelocity * Engine.getMain().getFrameArea(), sphere_pos.z);
        //sphere_pos.set(sphere_pos.x, gravityPos, sphere_pos.z);

        //System.out.println(sphere_pos.y);
        return sphere_pos;
    }

    public static float checkFloor(){
        if (sphere_pos.y - 3.0 <= floor){
            return -gravityVelocity;
        }
        return 0.0f;
    }
}
