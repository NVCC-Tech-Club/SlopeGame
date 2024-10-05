layout (location = 0) in vec3 aPos; //vertex position

layout (location = 1) in vec3 aColor; //vertex color

out vec3 vertexColor;

void main() {
    gl_Position = vec4(aPos, 1.0); //transform

    vertexColor = aColor; //color
}