#version 300 es

precision mediump float;

//uniform int u_light_enabled;
//uniform int u_diffuse_enabled;
//uniform vec4 u_diffuse_color;

//in vec4 v_color;
//in vec3 v_model_normal;
//in vec3 light_direction;
//out vec4 diffuse_color;
//float diffuseFactor;

//void diffuseLight(){
    /*if (u_light_enabled == 1) {
        if (u_diffuse_enabled == 1){
            // Use only modelNormal, not modelViewNormal
            // Reason is because it will change colors as you move the camera around,
            // which is not a real world scenario

            // Observations:
            // normal = normalize(v_model_normal) means the values of the norm will never change.
            //          For example when the object rotates, the light colors are stuck on
            //          that side of the object!

            //diffuseFactor = max(0.0, dot(v_model_normal, light_direction));
            //diffuse_color = clamp(vec4(diffuseFactor * u_diffuse_color.rgb, 1.0), 0.0, 1.0);
            diffuse_color = clamp(vec4(u_diffuse_color.rgb, 1.0), 0.0, 1.0);
        }
    }*/

    //diffuse_color = vec4(1.0, 1.0, 1.0, 1.0);
//}

void main() {
    //diffuseLight();
}
