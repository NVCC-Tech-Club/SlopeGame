#version 410 core

#define MAX_STEPS 80
#define MIN_DIST 0.001
#define MAX_DIST 100.0

in vec3 color;
in vec2 fragTexCoords;
in vec4 outColor;
out vec4 fragColor;

uniform sampler2D textureSampler;
uniform vec2 iResolution;

layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat3 rotationMatrix;
    vec3 position;
    float nearPlane;
    float farPlane;
} CamMatrix;

vec3 viewPosFromDepth(float depth, vec2 uv) {
    float z = depth * 2.0 - 1.0;

    vec4 positionCS = vec4(uv * 2.0 - 1.0, z, 1.0);
    vec4 positionVS = CamMatrix.projectionMatrix * positionCS;
    positionVS /= positionVS.w;

    return positionVS.xyz;
}

vec3 viewDirFromUv(vec2 uv) {
    return (CamMatrix.viewMatrix * vec4(normalize(viewPosFromDepth(1.0, uv)), 0.0)).xyz;
}

float sdSphere( vec3 p, float s) {
    return length(p)-s;
}

bool raymarched(vec2 uv) {
    vec3 ro = CamMatrix.position - vec3(0, 0, -3);
    vec3 rd = normalize(normalize(vec3(uv, 1.0)) * 1);

    // Total distance
    float t = 0.0;

    // Raymarching
    for(int i=0; i<MAX_STEPS; i++) {
        vec3 p = ro + rd * t;
        float d = sdSphere(p, 1.0);
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

    bool hit = raymarched(uv);

    vec4 tex = hit ? vec4(1.0, 0.0, 0.0, 1.0) : texture(textureSampler, fragTexCoords);
    fragColor = tex;
}