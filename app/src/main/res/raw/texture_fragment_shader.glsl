#version 300 es

precision mediump float;

uniform sampler2D u_textureunit;
uniform vec4 u_RGBA;

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;

void main() {
    color = texture(u_textureunit, v_texturecoordinates) * vec4(v_color.r * u_RGBA.r, v_color.g * u_RGBA.g, v_color.b * u_RGBA.b, 1.0);
    color *= v_color.a * u_RGBA.a;
}
