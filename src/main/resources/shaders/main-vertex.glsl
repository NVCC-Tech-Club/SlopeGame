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

//out vec3 color;
out vec2 fragTexCoords;

void main() {
    gl_Position = CamMatrix.projectionMatrix * CamMatrix.viewMatrix * vec4(pos, 1.0);
    //color = vec3(pos.x + 0.8, 0.8, pos.y + 0.8);
    fragTexCoords = texCoord;
}
