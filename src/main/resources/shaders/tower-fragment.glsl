#version 410 core

in vec2 fragTexCoords;
in vec4 outColor;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 tex = texture(textureSampler, fragTexCoords);
    fragColor = vec4(tex.r * 1.0, tex.g * 1.0, tex.b * outColor.b, tex.a * outColor.a);
}