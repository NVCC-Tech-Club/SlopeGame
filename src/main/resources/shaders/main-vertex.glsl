#version 410 core

in vec3 pos;
out vec3 color;

void main() {
    gl_Position = vec4(pos, 1.0);
    color = vec3(pos.x + 0.8, 0.8, pos.y + 0.8);
}
