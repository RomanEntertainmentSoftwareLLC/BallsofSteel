#version 300 es

precision mediump float;

out vec4 color;

void main() {
    color = vec4(1.0, 1.0, 1.0, 1.0); //vec4(vec3(gl_FragCoord.z), 1.0);
}
