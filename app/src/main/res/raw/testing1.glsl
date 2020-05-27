#version 300 es

precision mediump float;

in vec4 v_color;
out vec4 vv_color;

void main() {
    vv_color = vec4(v_color.r * 0.5, v_color.g, v_color.b, v_color.a);
}
