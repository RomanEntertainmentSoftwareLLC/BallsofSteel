#version 300 es

precision mediump float;

uniform sampler2D u_textureunit;
uniform vec4 u_RGBA;
uniform float u_gamma;
uniform float u_exposure;

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;

void main() {
    vec4 texture_color = texture(u_textureunit, vec2(v_texturecoordinates.x, v_texturecoordinates.y));

    float one_over_gamma = 1.0 / u_gamma;

    // Exposure tone mapping
    vec4 mapped = vec4(pow(1.0 - exp(-texture_color.r * u_exposure), one_over_gamma),
                       pow(1.0 - exp(-texture_color.g * u_exposure), one_over_gamma),
                       pow(1.0 - exp(-texture_color.b * u_exposure), one_over_gamma),
                       1.0);

    color = vec4(mapped.r, mapped.g, mapped.b, 1.0);
    color *= v_color.a * u_RGBA.a;
}
