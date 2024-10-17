#version 410

#define PI 3.14159265
#define CLOSEST_DIST 100000000.0
#define MAX_STEPS 80
#define MIN_DIST 0.00001
#define TAU PI * 2.0

out vec4 fragColor;
in vec3 position;

const float FOV = 1.0;

layout(std140) uniform SphereBlock {
    mat4 projectionMatrix;
    mat4 modelMatrix;
    vec2 resolution;
    mat4 viewMatrix;
    vec3 camPosition;
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

bool hit_sphere(ray r, float radius, inout float distance) {
    vec3 oc = r.origin;
    float A = dot(r.direction, r.direction);
    float B = dot(r.direction, oc);
    float C = dot(oc, oc) - radius * radius;
    float discriminant = B*B - 4*A*C;

    if (discriminant < 0.0) {
        return false;
    } else {
        distance = (-B - sqrt(discriminant)) / (2.0 * A);
        return true;
    }
}

vec2 getUV(vec2 rayOffset) {
    return (2.0 * (gl_FragCoord.xy + rayOffset) - SphBlock.resolution.xy) / SphBlock.resolution.y;
}

vec4 render(vec2 uv) {
    vec3 ro = vec3(0.0, 0.0, -1.1);
    vec3 rd = normalize(normalize(vec3(uv, FOV)));

    ray r = ray(ro, rd);
    float distance = 0.0;

    if (hit_sphere(r, 1.0, distance)) {
        return vec4(1.0, 0.0, 0.0, 1.0);
    }

    return vec4(0.0);
}

void main() {
    sphere sp = sphere(vec3(0.0), 1.0f);
    //sp.position = vec3(0.0);
    //sp.radius = 1.0f;
    fragColor = render(getUV(vec2(0.0)));
    //fragColor = vec4(getUV(vec2(0.0)), 0.0, 1.0);
}