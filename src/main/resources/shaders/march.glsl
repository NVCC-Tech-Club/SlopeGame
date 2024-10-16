#define PI 3.14159265
#define CLOSEST_DIST 100000000.0
#define MAX_STEPS 80
#define MIN_DIST 0.00001
#define TAU (2*PI)

struct march {
    float dist;
    int steps;
};

struct sphere {
    vec3 position;
    float radius;
};

struct ray {
    vec3 origin;
    vec3 direction;
};

float distanceSphere(vec3 p, float radius) {
    return length(p) - radius;
}

march coneMarch(ray r, sphere pl, float rot, sampler2D text) {
    float dist =0.0;
    float cd = CLOSEST_DIST;
    float ccr = 0.0; // Current Cone Radius
    vec3 p = r.origin;
    int steps = 0;

    while(dist < CLOSEST_DIST && steps++ < MAX_STEPS) {
        p = r.origin + r.direction * dist;
        cd = distanceSphere(p, pl.radius);
        ccr = dist * 0.0625;

        if(cd < ccr * 1.25) {
            break;
        }

        dist += cd;
    }

    return march(dist, steps);
}

void marchedSphere(ray r,
        sphere pl,
        float rot,
        march m,
        out vec3 hitPos,
        out vec3 nor,
        out bool hit,
        sampler2D text) {
    vec3 p = r.origin;
    float closestDist = CLOSEST_DIST;
    float minDist = MIN_DIST;
    int steps = m.steps;
    float dist = dot(p, p) + m.dist;

    while(closestDist > minDist && steps++ <= MAX_STEPS) {
        closestDist = distanceSphere(p, pl.radius);
        float cameraDist = dot(r.origin - p, r.origin - p);
        minDist = MIN_DIST + clamp(cameraDist / 40, 0.0, 0.1);

        if(closestDist <= 0.0) {
            break;
        }

        p += r.direction * closestDist;

        if(dot(p, p) > dist * 1.25) {
            hit = false;
            return;
        }
    }

    // Check if we hit the sphere
    if(closestDist <= minDist + MIN_DIST) {
        hitPos = p;
        nor = normalize(p);

        hit = true;
    }else {
        hit = false;
    }
}