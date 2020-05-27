#version 300 es

precision mediump float;

uniform int u_light_enabled;
uniform int u_ambient_enabled;
uniform vec4 u_ambient_color;
in vec4 v_color;
out vec4 ambient_color;

void ambientLight(){
    if (u_light_enabled == 1) {
        if (u_ambient_enabled == 1){
            ambient_color = u_ambient_color;
        }
        else {
            ambient_color = vec4(1.0, 1.0, 1.0, 1.0);
        }
    }
    else {
        ambient_color = vec4(1.0, 1.0, 1.0, 1.0);
    }
}

void main() {
    ambientLight();
}
