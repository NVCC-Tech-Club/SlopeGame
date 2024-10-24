#version 410 core

#define MAX_STEPS 80
#define MIN_DIST 0.05
#define MAX_DIST 160.0

in vec3 color;
in vec2 fragTexCoords;
in vec4 outColor;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec2 iResolution;
uniform vec3 camPosition;

layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    float nearPlane;
    float farPlane;
} CamMatrix;

// Source: https://iquilezles.org/articles/distfunctions/
float sdSphere(vec3 p, float s) {
    return length(p)-s;
}


// Raymarching Algorithm made by Diego Fonseca + various online videos
// Source for linear view transformation: https://jamie-wong.com/2016/07/15/ray-marching-signed-distance-functions/
bool raymarched(vec2 uv, vec2 ndc) {

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
        vec3 p = ro + rd * t;
        float d = sdSphere(p, 10.0);
        t += d;

        if(d < MIN_DIST) {
            return true;
        }

        if(d > MAX_DIST) {
            return false;
        }
    }

    return false;
}

void main() {
    vec2 uv = (2.0 * gl_FragCoord.xy - iResolution.xy) / iResolution.y;
    vec2 ndc = (gl_FragCoord.xy / iResolution) * 2.0 - 1.0;
    bool hit = raymarched(uv, ndc);

    vec4 tex = hit ? vec4(1.0, 0.0, 0.0, 1.0) : texture(textureSampler, fragTexCoords);
    fragColor = tex;
}