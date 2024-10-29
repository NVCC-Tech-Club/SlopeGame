#version 410 core

#define TOWERS_PER_ROW 7

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
    float gap_offset = (gl_InstanceID % TOWERS_PER_ROW >= 3) ? 270.0f : 0.0;
    float x_offset = (gl_InstanceID / TOWERS_PER_ROW) * 75.0;

    vec3 instanceOffset = vec3(-x_offset * 2.0, (sin(float(gl_InstanceID)) * 50.0f) - x_offset, (float(gl_InstanceID % TOWERS_PER_ROW) * 150.0) - 266.0f + gap_offset);

    mat4 instanceModel = model;
    instanceModel[3].xyz += instanceOffset;

    mat4 u = CamMatrix.projectionMatrix * CamMatrix.viewMatrix * instanceModel;
    gl_Position = u * vec4(pos, 1.0);

    fragTexCoords = texCoord;
    outColor = inColor;
}