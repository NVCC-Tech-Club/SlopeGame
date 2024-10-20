#version 410

#define PI 3.14159265
#define CLOSEST_DIST 100000000.0
#define MAX_STEPS 80
#define MIN_DIST 0.00001
#define TAU PI * 2.0

out vec4 fragColor;

const float FOV = 1.0; 

layout(std140) uniform SphereBlock {
    mat4 projectionMatrix;  
    mat4 modelMatrix;
    vec2 resolution;        
    mat4 viewMatrix;        
};



struct ray {
    vec3 origin;
    vec3 direction;
};


bool hit_sphere(ray r, vec3 center, float radius, inout float distance) {
    vec3 oc = r.origin - center;
    float A = dot(r.direction, r.direction);
    float B = dot(oc, r.direction);
    float C = dot(oc, oc) - radius * radius;
    float discriminant = B * B - A * C;

    if (discriminant < 0.0) {
        return false;
    } else {
        distance = (-B - sqrt(discriminant)) / A;
        return true;
    }
}

// Function to get normalized UV coordinates
vec2 getUV() {
    return (gl_FragCoord.xy / resolution) * 2.0 - 1.0;  // Normalized coordinates between -1 and 1
}

// Function to render the scene
vec4 render() {
    vec3 ro = vec3(0.0, 0.0, -5.0); 
    vec2 uv = getUV();  

    
    uv.x *= resolution.x / resolution.y ; // Maintain aspect ratio

    // Creating a ray direction, adjusting for the field of view and aspect ratio
    vec3 rd = normalize(vec3(uv.x * tan(FOV / 2.0), uv.y * tan(FOV / 2.0) * (resolution.y / resolution.x), 1.0));

    ray r = ray(ro, rd);
    float distance = 0.0;

    
    vec3 spherePos = vec3(0.0, 0.0, 0.0);  // origin
    float sphereRadius = 1.0;

    // Check for intersection with the sphere
    if (hit_sphere(r, spherePos, sphereRadius, distance)) {
        return vec4(1.0, 0.0, 0.0, 1.0);  
    }

    return vec4(0.0);  
}

void main() {
    // Render the scene
    fragColor = render();
}
