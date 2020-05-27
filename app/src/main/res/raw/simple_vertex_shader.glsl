#version 300 es

uniform mat4 u_model_matrix;
uniform mat4 u_mvp_matrix;
uniform vec3 u_light_position;
uniform vec3 u_light_direction;
uniform vec3 u_object_position;
uniform int u_light_type;

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec4 a_color;
layout (location = 2) in vec2 a_texturecoordinates;
layout (location = 3) in vec3 a_normal;

out vec4 v_color;
out vec2 v_texturecoordinates;
out vec3 v_model_normal;
out vec3 light_direction;

void main() {
    gl_Position = u_mvp_matrix * vec4(vec3(a_position), 1.0);
    v_color = a_color;
    v_texturecoordinates = a_texturecoordinates;
    v_model_normal = mat3(transpose(inverse(u_model_matrix))) * a_normal;

    if (u_light_type == 0) {
        // Point Light
        light_direction = normalize(u_light_position - u_object_position);
    }
    else if (u_light_type == 1) {
        // Directional Light
        light_direction = normalize(u_light_direction);
    }
    else if (u_light_type == 2) {
        // TODO Spot Light
        light_direction = vec3(0.0);
    }
}
