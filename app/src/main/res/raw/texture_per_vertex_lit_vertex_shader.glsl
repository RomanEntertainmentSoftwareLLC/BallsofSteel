#version 300 es

uniform mat4 u_model_matrix;
uniform mat4 u_view_matrix;
uniform mat4 u_mvp_matrix;
uniform vec3 u_camera_position;
uniform vec3 u_light_position;

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec4 a_color;
layout (location = 2) in vec2 a_texturecoordinates;
layout (location = 3) in vec3 a_normal;

out vec2 v_texturecoordinates;
out vec3 v_texturecoordinates3D;
out vec4 v_color;
out vec3 v_normal;
out vec3 v_position;
out vec3 v_camera_position;
out vec3 v_model_normal;
out vec3 v_model_position;
out vec3 v_model_camera_position;
out vec3 v_model_light_position;

out vec3 v_model_view_position;
out vec3 v_model_view_camera_position;
out vec3 v_model_view_normal;

void main() {
    v_color = a_color;
    v_texturecoordinates = a_texturecoordinates;
    v_texturecoordinates3D = vec3(a_position);
    gl_Position = u_mvp_matrix * vec4(vec3(a_position), 1.0);
    v_normal = a_normal;
    v_position = vec3(a_position);
    v_camera_position = vec3(u_camera_position);

    v_model_position = vec3(u_model_matrix * vec4(vec3(a_position), 1.0));
    v_model_normal = normalize(vec3(u_model_matrix * vec4(a_normal, 0.0)));
    v_model_camera_position = vec3(u_model_matrix * vec4(u_camera_position, 1.0));
    v_model_light_position = vec3(u_model_matrix * vec4(u_light_position, 1.0));

    v_model_view_position = vec3(u_view_matrix * u_model_matrix * vec4(vec3(a_position), 1.0));
    v_model_view_camera_position = vec3(u_view_matrix * u_model_matrix * vec4(u_camera_position, 1.0));
    v_model_view_normal = normalize(vec3(u_view_matrix * u_model_matrix * vec4(a_normal, 0.0)));
}