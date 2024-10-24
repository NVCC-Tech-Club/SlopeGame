#version 410 core

in vec2 fragTexCoords;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    // Render the scene
    vec4 tex = texture(textureSampler, fragTexCoords);
    fragColor = vec4(tex.r, tex.g, tex.b, tex.a);
}
