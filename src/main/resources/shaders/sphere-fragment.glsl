#version 410 core

#define MAX_STEPS 80
#define PI 3.14159265
#define TAU (2*PI)

const vec3 c = vec3(0.0, 5.0, 0.0);

in vec3 color;
in vec2 fragTexCoords;
in vec4 outColor;
out vec4 fragColor;

uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;
uniform vec2 iResolution;
uniform vec3 camPosition;

layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    float nearPlane;
    float farPlane;
} CamMatrix;

vec3 rotateZ(vec3 p, float angle) {
    float cosT = cos(angle);
    float sinT = sin(angle);

    return vec3(
        p.x * cosT - p.y * sinT,
        p.x * sinT + p.y * cosT,
        p.z
    );
}

vec3 rotateY(vec3 p, float angle) {
    float cosT = cos(angle);
    float sinT = sin(angle);

    return vec3(
        p.x * cosT + p.z * sinT,
        p.y,
        p.z * cosT - p.x * sinT
    );
}

vec3 rotateX(vec3 p, float angle) {
    float cosT = cos(angle);
    float sinT = sin(angle);

    return vec3(
        p.y * sinT + p.x * cosT,
        p.y * cosT - p.x * sinT,
        p.z
    );
}

float atan2(in float y, in float x) {
    return y > 0.0 ? atan(y, x) + PI : -atan(y, -x);
}

vec2 sphereUV(vec3 p) {
    p = rotateX(p, 0);
    p = rotateY(p, 0);
    p = rotateZ(p, 0);

    float r = length(p);
    float phi = atan2(p.z, p.x);
    return vec2(phi / TAU, acos(p.y / r) / PI);
}

// Source: https://iquilezles.org/articles/distfunctions/
float sdSphere(vec3 p, float s) {
    return length(p)-s;
}


// Raymarching Algorithm made by Diego Fonseca + various online videos
// Source for linear view transformation: https://jamie-wong.com/2016/07/15/ray-marching-signed-distance-functions/
bool raymarched(vec2 uv, vec2 ndc, inout vec3 p) {

    // Get World View
    vec4 clipSpacePos = vec4(ndc, -1.0, 1.0);
    vec4 viewSpacePos = inverse(CamMatrix.projectionMatrix) * clipSpacePos;
    viewSpacePos /= viewSpacePos.w;

    // Ray Marching
    vec3 ro = camPosition;
    vec3 rd = normalize((inverse(CamMatrix.viewMatrix) * vec4(viewSpacePos.xyz, 0.0)).xyz);

    // Total distance
    float t = 0.0;

    // Raymarching
    // TODO: Instead of MIN and MAX distance use z-near and z-far.
    for(int i=0; i<MAX_STEPS; i++) {
        p = ro + rd * t;
        float d = sdSphere(p - c, 3.0);
        t += d;

        if(d < CamMatrix.nearPlane) {
            return true;
        }

        if(d > CamMatrix.farPlane) {
            return false;
        }
    }

    return false;
}

float LinearizeDepth(float d)  {
    float z = d * 2.0 - 1.0;
    return (2.0 * CamMatrix.nearPlane * CamMatrix.farPlane)
    / (CamMatrix.farPlane + CamMatrix.nearPlane - z * (CamMatrix.farPlane - CamMatrix.nearPlane));
}

void main() {
    vec3 p = vec3(0.0);
    vec2 uv = (2.0 * gl_FragCoord.xy - iResolution.xy) / iResolution.y;
    vec2 ndc = (gl_FragCoord.xy / iResolution) * 2.0 - 1.0;
    bool hit = raymarched(uv, ndc, p);
    vec4 col = vec4(0.0);
    float depth = texture(textureSampler2, fragTexCoords).r;
    vec4 tex = texture(textureSampler0, fragTexCoords);


    if(hit) {
        vec3 nor = normalize(p - c);
        vec2 spTexCoord = sphereUV(nor);
        col = texture(textureSampler1, spTexCoord * 6);

        float sphDepth = length((p - c) - camPosition);
        float ndcDepth = (sphDepth - CamMatrix.nearPlane) / (CamMatrix.farPlane - CamMatrix.nearPlane);
        ndcDepth = clamp(ndcDepth, 0.0, 1.0);

        if(ndcDepth < depth) {
            fragColor = sqrt(1.0 - ndcDepth) * vec4(0.0, col.g, 0.0, col.a);
            return;
        }
    }

    fragColor = sqrt(1.0 - depth) * tex;
}