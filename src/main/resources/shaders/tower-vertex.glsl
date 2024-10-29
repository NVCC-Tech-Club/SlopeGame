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
    vec3 instanceOffset = vec3(0.0, sin(float(gl_InstanceID)) * 50.0f, float(gl_InstanceID) * 150.0);
    mat4 instanceModel = model;
    instanceModel[3].xyz += instanceOffset;

    mat4 u = CamMatrix.projectionMatrix * CamMatrix.viewMatrix * instanceModel;
    gl_Position = u * vec4(pos, 1.0);

    fragTexCoords = texCoord;
    outColor = inColor;
}