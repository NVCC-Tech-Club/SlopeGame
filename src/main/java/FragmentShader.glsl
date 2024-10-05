in vec3 vertexColor; //input color vertex shader

out vec4 fragColor; //output color

void main(){
    fragColor = vec4(vertexColor, 1.0); //frag color
}