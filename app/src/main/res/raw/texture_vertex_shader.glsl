#version 300 es

uniform mat4 u_mvp_matrix;

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec4 a_color;
layout (location = 2) in vec2 a_texturecoordinates;

out vec2 v_texturecoordinates;
out vec4 v_color;

void main() {
    v_color = a_color;
    v_texturecoordinates = a_texturecoordinates;
    gl_Position = u_mvp_matrix * a_position;
}