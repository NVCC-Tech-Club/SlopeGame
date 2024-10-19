#version 410

#define PI 3.14159265
#define CLOSEST_DIST 100000000.0
#define MAX_STEPS 80
#define MIN_DIST 0.00001
#define TAU PI * 2.0

out vec4 fragColor;
in vec3 position;

const float FOV = 1.0;

uniform vec3 camPosition;

layout(std140) uniform SphereBlock {
    mat4 projectionMatrix;
    mat4 modelMatrix;
    vec2 resolution;
    mat4 viewMatrix;
} SphBlock;

struct ray {
    vec3 origin;
    vec3 direction;
};

struct sphere {
    vec3 position;
    float radius;
};

struct march {
    float dist;
    int steps;
};

bool hit_sphere(ray r, vec3 center, float radius, inout float distance) {
    vec3 oc = r.origin - center;
    float A = dot(r.direction, r.direction);
    float B = dot(oc, r.direction);
    float C = dot(oc, oc) - radius * radius;
    float discriminant = B*B - A*C;

    if (discriminant < 0.0) {
        return false;
    } else {
        distance = (-B - sqrt(discriminant)) / A;
        return true;
    }
}

vec2 getUV(vec2 rayOffset) {
    return (2.0 * (gl_FragCoord.xy + rayOffset) - SphBlock.resolution.xy) / SphBlock.resolution.y;
}

vec4 render(vec2 uv) {
    // Camera position
    vec3 ro = camPosition;
    
    // Convert UV to ray direction
    vec3 rd = normalize(vec3(uv, FOV));

    ray r = ray(ro, rd);
    float distance = 0.0;

    // Sphere position and radius
    vec3 spherePos = vec3(0.0, 0.0, -10.0);  // Place the sphere in front of the camera
    float sphereRadius = 85.0;
    
    if (hit_sphere(r, spherePos, sphereRadius, distance)) {
        return vec4(1.0, 0.0, 0.0, 1.0);  // Red color for the sphere
    }

    return vec4(0.0);  // Background color (black)
}

void main() {
    fragColor = render(getUV(vec2(0.0)));
}
