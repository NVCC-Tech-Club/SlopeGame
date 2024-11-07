package com.slope.game;

import org.joml.Vector3f;

public class Physics {
    private static final Vector3f c = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final float GRAVITY_STRENGTH = -9.8f;
    private static Vector3f sphere_pos = new Vector3f(0.0f, 0.0f, 0.0f);
    private static float gravityVelocity = 0.0f;
    private static float xVelocity = 0.0f;
    private static final float SPHERE_RADIUS = 3.0f;
    
    private static float floor = -100.0f; //testing floor

    private static Vector3f normalVector;
    private static float distanceToPlane;
    private static float accelerationAlongSlope = 0.0f;

    private static final Vector3f p1 = new Vector3f(5.0f, -110.0f, -20.0f);
    private static final Vector3f p3 = new Vector3f(-200.0f, -50.0f, 0.0f);

    private static final Vector3f p2 = new Vector3f(5.0f, -110.0f, 20.0f);



    private static final Vector3f p4 = new Vector3f(-5.0f, -100.0f, 5.0f);



    public static Vector3f getPosition() { // Gravity Pull
        //gravityPos = 0.5f * GRAVITY_STRENGTH * (Engine.getMain().getITime() * Engine.getMain().getITime());

        gravityVelocity += GRAVITY_STRENGTH * Engine.getMain().getFrameArea();
        sphere_pos.set(sphere_pos.x, sphere_pos.y += gravityVelocity * Engine.getMain().getFrameArea(), sphere_pos.z);
        Vector3f edgeVector1 = new Vector3f(p1).sub(p2);
        Vector3f edgeVector2 = new Vector3f(p3).sub(p2);

        normalVector = edgeVector1.cross(edgeVector2);
        normalVector = normalVector.normalize();
    
        distanceToPlane = normalVector.dot(new Vector3f(sphere_pos).sub(p1));
        
        float slopeAngle = getSlopeAngle();
        if(checkCollision()){
                accelerationAlongSlope = (float) (100 * GRAVITY_STRENGTH * Math.sin(Math.toRadians(slopeAngle)));
                xVelocity += accelerationAlongSlope * Engine.getMain().getFrameArea();
        }
        
        // Determine slope direction and update position along the slope
        Vector3f slopeDirection = new Vector3f(normalVector).cross(new Vector3f(0, 1, 0)).normalize();
        sphere_pos.set(sphere_pos.x += xVelocity * Engine.getMain().getFrameArea(), sphere_pos.y,sphere_pos.z);
        //sphere_pos.add(new Vector3f(slopeDirection).mul(xVelocity * Engine.getMain().getFrameArea()));
        

        System.out.println("SLOPE ANGLE: " + slopeAngle + ", xVelocity: " + xVelocity);
        //System.out.println("SPHERE POS: " + sphere_pos);
        return sphere_pos;
    }

    private static float getSlopeAngle() {
        return (float) Math.toDegrees(Math.acos(normalVector.dot(new Vector3f(0, 1, 0))));
    }

    private static boolean checkCollision() {
        if (Math.abs(distanceToPlane) <= SPHERE_RADIUS) {
            Vector3f projectedPoint = new Vector3f(sphere_pos).sub(new Vector3f(normalVector).mul(distanceToPlane));
            if (isPointInTriangle(projectedPoint, p1, p2, p3)) {
                // float correctionDistance = SPHERE_RADIUS - Math.abs(distanceToPlane);
                
                // sphere_pos.add(new Vector3f(normalVector).mul(correctionDistance));
                gravityVelocity = -gravityVelocity * 0.1f;
                xVelocity *= 0.8f;
                }
                return true;
        }


        return false;
    } 
    private static boolean isPointInTriangle(Vector3f point, Vector3f v0, Vector3f v1, Vector3f v2) {
        Vector3f edge1 = new Vector3f(v1).sub(v0);
        Vector3f edge2 = new Vector3f(v2).sub(v1);
        Vector3f edge3 = new Vector3f(v0).sub(v2);
    
        Vector3f c1 = new Vector3f(point).sub(v0).cross(edge1);
        Vector3f c2 = new Vector3f(point).sub(v1).cross(edge2);
        Vector3f c3 = new Vector3f(point).sub(v2).cross(edge3);
    
        // Check if the point is on the same side of each edge
        return c1.dot(c2) >= 0 && c2.dot(c3) >= 0;
    }



    public static float checkFloor(){
        if (sphere_pos.y - 3.0 <= floor){
            return -gravityVelocity;
        }
        return 0.0f;
    }
    
}
