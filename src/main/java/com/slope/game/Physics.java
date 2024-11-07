package com.slope.game;

import org.joml.Vector3f;

public class Physics {
    private static final Vector3f c = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final float GRAVITY_STRENGTH = -9.8f;
    private static Vector3f sphere_pos = new Vector3f(0.0f, 0.0f, 0.0f);
    private static float gravityVelocity = 0.0f;
    private static float gravityPos= 0.0f;
    private static final float SPHERE_RADIUS = 3.0f;
    
    private static float floor = -100.0f; //testing floor

    private static Vector3f normalVector;
    private static float distanceToPlane;

    private static final Vector3f p1 = new Vector3f(-5.0f, -100.0f, -5.0f);
    private static final Vector3f p2 = new Vector3f(5.0f, -100.0f, -5.0f);

    private static final Vector3f p3 = new Vector3f(5.0f, -110.0f, 5.0f);


    private static final Vector3f p4 = new Vector3f(-5.0f, -100.0f, 5.0f);



    public static Vector3f getPosition() { // Gravity Pull
        //gravityPos = 0.5f * GRAVITY_STRENGTH * (Engine.getMain().getITime() * Engine.getMain().getITime());

        gravityVelocity += GRAVITY_STRENGTH * Engine.getMain().getFrameArea();
        sphere_pos.set(sphere_pos.x, sphere_pos.y += gravityVelocity * Engine.getMain().getFrameArea(), sphere_pos.z);
        Vector3f edgeVector1 = new Vector3f(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
        Vector3f edgeVector2 = new Vector3f(p3.x - p2.x, p3.y - p2.y, p3.z - p2.z);

        normalVector = edgeVector1.cross(edgeVector2);
        normalVector = normalVector.normalize();
        

        

        distanceToPlane = normalVector.dot(new Vector3f(sphere_pos).sub(p1));
        

        checkCollision();
        System.out.println("SLOPE ANGLE: " + getSlopeAngle());
        return sphere_pos;
    }

    private static float getSlopeAngle() {
        return (float) Math.toDegrees((float) Math.atan2(normalVector.y, normalVector.z));
    }

    private static void checkCollision() {
    if (Math.abs(distanceToPlane) <= SPHERE_RADIUS) {
        float correctionDistance = SPHERE_RADIUS - Math.abs(distanceToPlane);
        
        sphere_pos.add(new Vector3f(normalVector).mul(correctionDistance));
        gravityVelocity = -gravityVelocity * 0.3f;
    }

    
}



    public static float checkFloor(){
        if (sphere_pos.y - 3.0 <= floor){
            return -gravityVelocity;
        }
        return 0.0f;
    }
    
}
