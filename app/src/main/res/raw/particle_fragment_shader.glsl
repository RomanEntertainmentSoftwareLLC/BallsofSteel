#version 300 es

precision mediump float;

uniform vec4 u_RGBA;

in vec4 v_color;

out vec4 color;

void main() {
    color = vec4(v_color.r * u_RGBA.r, v_color.g * u_RGBA.g, v_color.b * u_RGBA.b,  v_color.a * u_RGBA.a);
}
