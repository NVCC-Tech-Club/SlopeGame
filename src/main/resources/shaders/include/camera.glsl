layout(std140) uniform CameraMatrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat3 rotationMatrix;
    vec3 position;
    float nearPlane;
    float farPlane;
} CamMatrix;