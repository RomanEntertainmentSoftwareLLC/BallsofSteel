#version 300 es

precision mediump float;

uniform sampler2D texture0;
uniform sampler2D texture1;
uniform vec4 u_RGBA;

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;

void main() {
    vec4 texture_color0 = texture(texture0, v_texturecoordinates);
    vec4 texture_color1 = texture(texture1, v_texturecoordinates);

    color = vec4((texture_color0.r + texture_color1.r) * u_RGBA.r,
                        (texture_color0.g + texture_color1.g) * u_RGBA.g,
                        (texture_color0.b + texture_color1.b) * u_RGBA.b,
                        1.0);
    color *= v_color.a * u_RGBA.a;
}