#version 410 core

in vec3 color;
in vec2 fragTexCoords;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 tex = texture(textureSampler, fragTexCoords);
    fragColor = tex;
    //fragColor = vec4(1.0, 1.0, 0.0, 1.0f);
}
