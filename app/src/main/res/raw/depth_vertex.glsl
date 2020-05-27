#version 300 es

uniform mat4 u_mvp_matrix;
layout (location = 0) in vec4 a_position;

// DO NOT TOUCH IT WORKS!!!

void main() {
    gl_Position = u_mvp_matrix * a_position;
}
