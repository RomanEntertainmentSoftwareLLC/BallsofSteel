#version 300 es

precision mediump float;

uniform sampler2D u_textureunit;
uniform vec2 dir;
uniform float resolution;
uniform float radius;

in vec2 v_texturecoordinates;
in vec4 v_color;

out vec4 color;

void main() {
    vec4 sum = vec4(0.0);

    float blur = radius / resolution;

    sum += texture(u_textureunit, vec2(v_texturecoordinates.x - 4.0 * blur * dir.x, v_texturecoordinates.y - 4.0 * blur * dir.y)) * 0.0162162162;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x - 3.0 * blur * dir.x, v_texturecoordinates.y - 3.0 * blur * dir.y)) * 0.0540540541;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x - 2.0 * blur * dir.x, v_texturecoordinates.y - 2.0 * blur * dir.y)) * 0.1216216216;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x - 1.0 * blur * dir.x, v_texturecoordinates.y - 1.0 * blur * dir.y)) * 0.1945945946;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x, v_texturecoordinates.y)) * 0.2270270270;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x + 1.0 * blur * dir.x, v_texturecoordinates.y + 1.0 * blur * dir.y)) * 0.1945945946;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x + 2.0 * blur * dir.x, v_texturecoordinates.y + 2.0 * blur * dir.y)) * 0.1216216216;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x + 3.0 * blur * dir.x, v_texturecoordinates.y + 3.0 * blur * dir.y)) * 0.0540540541;
    sum += texture(u_textureunit, vec2(v_texturecoordinates.x + 4.0 * blur * dir.x, v_texturecoordinates.y + 4.0 * blur * dir.y)) * 0.0162162162;
    color = v_color * vec4(sum.rgba) * v_color.a;
}


