#version 300 es

precision mediump float;
const int MAX_WEIGHTS = 51;

uniform sampler2D u_textureunit;
uniform vec4 u_RGBA;
uniform float u_sigma;
uniform int u_kernel_diameter;
uniform int u_screen_width;
uniform float u_mu;
uniform float u_weights[MAX_WEIGHTS];

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;

void main() {
    vec4 sum = vec4(0.0);
    float texel_size = 1.0 / float(u_screen_width);
    float total = 0.0;

    for (int i = 0; i < u_kernel_diameter; i++) {
        total = v_texturecoordinates.y + (float(i) - u_mu) * texel_size;
        vec4 texture_color = texture(u_textureunit, vec2(v_texturecoordinates.x, total));
        sum += vec4(texture_color.r * u_weights[i / 4],
        texture_color.g * u_weights[i / 4],
        texture_color.b * u_weights[i / 4],
        1.0);
    }

    color = vec4(sum.r * u_RGBA.r, sum.g * u_RGBA.g, sum.b * u_RGBA.b, 1.0);
    color *= v_color.a * u_RGBA.a;
}