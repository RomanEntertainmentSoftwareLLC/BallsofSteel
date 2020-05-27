#version 300 es

precision mediump float;

uniform sampler2D u_textureunit;
uniform vec4 u_RGBA;
uniform float u_red;
uniform float u_green;
uniform float u_blue;

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;

void main() {
    vec4 texture_color = texture(u_textureunit, v_texturecoordinates);

    color = vec4(u_red * texture_color.r,
                        u_green * texture_color.g,
                        u_blue * texture_color.b,
                        1.0);
}
