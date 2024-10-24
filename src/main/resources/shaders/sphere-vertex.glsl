#version 410 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoord;

layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat3 rotationMatrix;
    vec3 position;
    float nearPlane;
    float farPlane;
} CamMatrix;

//uniform vec2 resolution;
out vec2 fragTexCoords;

void main() {
    fragTexCoords = texCoord;
    gl_Position = vec4(pos, 1.0);
}