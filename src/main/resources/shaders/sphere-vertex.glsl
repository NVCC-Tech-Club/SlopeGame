#version 410

layout(location = 0) in vec3 pos;

uniform vec2 resolution;

void main() {
    gl_Position = vec4(pos, 1.0);
}