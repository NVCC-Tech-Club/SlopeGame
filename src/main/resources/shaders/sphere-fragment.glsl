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

vec3 viewPosFromDepth(float depth, vec2 uv) {
    float z = depth * 2.0 - 1.0;

    vec4 positionCS = vec4(uv * 2.0 - 1.0, z, 1.0);
    vec4 positionVS = SphBlock.projectionMatrix * positionCS;
    positionVS /= positionVS.w;

    return positionVS.xyz;
}

vec3 viewDirFromUv(vec2 uv) {
    return (SphBlock.viewMatrix * vec4(normalize(viewPosFromDepth(1.0, uv)), 0.0)).xyz;
}

bool hit_sphere(ray r, float radius, inout float distance) {
    vec3 oc = r.origin;
    float A = dot(r.direction, r.direction);
    float B = -2.0 * dot(r.direction, oc);
    float C = dot(oc, oc) - radius * radius;
    float discriminant = B*B - 4*A*C;
    return discriminant >= 0;
}

vec2 getUV(vec2 rayOffset) {
    return (2.0 * (gl_FragCoord.xy + rayOffset) - SphBlock.resolution.xy) / SphBlock.resolution.y;
}

vec3 render(vec2 uv) {
    vec3 ro = vec3(0.0, 0.0, 0.0);
    vec3 rd = viewDirFromUv(uv);

    ray r = ray(ro, rd);
    float distance = 0.0;
    vec3 col = vec3(0.0);

    if (hit_sphere(r, 0.5, distance)) {
        return vec3(1.0, 0.0, 0.0);
    }

    return vec3(0.0);
}

void main() {
    sphere sp = sphere(vec3(0.0), 1.0f);
    //sp.position = vec3(0.0);
    //sp.radius = 1.0f;
    //fragColor = vec4(render(getUV(vec2(0.0))), 1.0);
    fragColor = vec4(getUV(vec2(0.0)), 0.0, 1.0);
}