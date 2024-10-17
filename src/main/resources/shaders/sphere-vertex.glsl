#version 410

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 texCoord;
layout(location = 2) in vec4 inColor;

//uniform vec2 resolution;

void main() {
    gl_Position = vec4(pos, 1.0);
}