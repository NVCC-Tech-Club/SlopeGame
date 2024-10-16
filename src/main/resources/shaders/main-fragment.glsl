#version 410 core

in vec3 color;
in vec2 fragTexCoords;
in vec4 outColor;
out vec4 fragColor;

uniform sampler2D textureSampler;

void main() {
    vec4 tex = texture(textureSampler, fragTexCoords);
    fragColor = vec4(tex.r * outColor.r, tex.g * outColor.g, tex.b * outColor.b, tex.a * outColor.a);
    //fragColor = vec4(1.0);
}