#version 300 es

precision mediump float;

uniform sampler2D u_textureunit;
uniform vec4 u_RGBA;
uniform float u_bright_filter_range;

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;


void main() {
    vec4 texture_color = texture(u_textureunit, vec2(v_texturecoordinates.x, v_texturecoordinates.y));

    if ((texture_color.r + texture_color.g + texture_color.b) < (u_bright_filter_range * 3.0)) {
        texture_color.r = 0.0;
        texture_color.g = 0.0;
        texture_color.b = 0.0;
        texture_color.a = 0.0;
    }

    color = vec4(texture_color.r * u_RGBA.r, texture_color.g * u_RGBA.g, texture_color.b * u_RGBA.b, texture_color.a);
    color *= v_color.a * u_RGBA.a;
}
