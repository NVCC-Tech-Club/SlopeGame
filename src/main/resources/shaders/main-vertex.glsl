#version 410 core

layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat3 rotationMatrix;
    vec3 position;
    float nearPlane;
    float farPlane;
} CamMatrix;

in vec3 pos;
out vec3 color;

void main() {
    gl_Position = CamMatrix.projectionMatrix * vec4(pos, 1.0);
    color = vec3(pos.x + 0.8, 0.8, pos.y + 0.8);
}
