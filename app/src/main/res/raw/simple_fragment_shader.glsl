#version 300 es

precision mediump float;

in vec4 ambient_color;
//in vec4 diffuse_color;
out vec4 color;

void main() {
    color = vec4(ambient_color.r,
                 ambient_color.g,
                 ambient_color.b,
                 1.0);
}

