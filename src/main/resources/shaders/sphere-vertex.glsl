#version 410

layout(location = 0) in vec3 pos;

//uniform vec2 resolution;

out vec3 position;

void main() {
    position = pos;
    gl_Position = vec4(position, 1.0);
}