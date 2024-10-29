#version 410 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec4 inColor;

layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    float nearPlane;
    float farPlane;
} CamMatrix;

uniform mat4 model;

out vec2 fragTexCoords;
out vec4 outColor;

void main() {
    mat4 u = CamMatrix.projectionMatrix * CamMatrix.viewMatrix * model;
    gl_Position = u * vec4(pos, 1.0);

    fragTexCoords = texCoord;
    outColor = inColor;
}