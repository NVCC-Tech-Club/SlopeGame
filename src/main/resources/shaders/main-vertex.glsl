#version 410 core

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec4 inColor;

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
out vec4 outColor;

uniform mat4 model;

void main() {
    mat4 u = CamMatrix.projectionMatrix * CamMatrix.viewMatrix * model;
    gl_Position = u * vec4(pos, 1.0);
    //gl_Position = CamMatrix.viewMatrix * model * vec4(pos, 1.0);
    //color = vec3(pos.x + 0.8, 0.8, pos.y + 0.8);
    fragTexCoords = texCoord;
    outColor = inColor;
}
