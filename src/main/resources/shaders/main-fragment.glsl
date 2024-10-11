#version 410 core

in vec3 color;
in vec2 fragTexCoords;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    fragColor = texture(textureSampler, fragTexCoords);
    //fragColor = vec4(color, 1.0f);
}
