#version 300 es

precision mediump float;

// Texture units
uniform sampler2D u_textureunit;
uniform sampler2D u_textureunit2;
uniform samplerCube u_cubemap_unit;

// Texture enabled
uniform int u_texture_enabled;
uniform int u_texture_enabled2;
uniform int u_cubemap_enabled;

// Matrices
uniform mat4 u_view_matrix;
uniform mat4 u_model_matrix; // model: local and world matrices combined
uniform mat4 u_model_view_matrix;
uniform mat4 u_model_view_reflection_matrix;

// Light enabled
uniform int u_light_enabled;

// Light Color
uniform vec4 u_ambient_color;
uniform vec4 u_diffuse_color;
uniform vec4 u_specular_color;

// Enabled light types
uniform int u_ambient_enabled;
uniform int u_diffuse_enabled;
uniform int u_specular_enabled;

// Light intensities
//TODO diffuse intensity
uniform float u_specular_intensity;

// Positions
uniform vec3 u_object_position;
uniform vec3 u_camera_position;
uniform vec3 u_light_position;

// Angles
uniform vec3 u_camera_angle;
uniform vec3 u_light_direction;

// Selectable color;
uniform vec4 u_RGBA;

// Reflection
uniform int u_reverse_reflection;
uniform int u_reflective_cube_enabled;
uniform float u_reflectiveness;

// Light type
uniform int u_light_type;

in vec2 v_texturecoordinates;
in vec3 v_texturecoordinates3D;
in vec4 v_color;
in vec3 v_normal;
in vec3 v_position;
in vec3 v_camera_position;
in vec3 v_model_normal;
in vec3 v_model_position;
in vec3 v_model_camera_position;
in vec3 v_model_light_position;

in vec3 v_model_view_position;
in vec3 v_model_view_camera_position;
in vec3 v_model_view_normal;

out vec4 color;

vec4 ambient_color;
vec4 diffuse_color;
vec4 specular_color;

mat4 view_matrix;
mat4 model_view_matrix;

// Not used
//////////////////////////////////
//vec3 model_position;
//vec3 model_camera_position;
//vec3 model_light_position;
//vec3 model_normal;
//////////////////////////////////

// Not used
//////////////////////////////////
//vec3 modelview_position;
//vec3 modelview_camera_position;
//vec3 modelview_light_position;
//vec3 modelview_normal;
//////////////////////////////////

float diffuseFactor;
float specularityFactor;
vec3 finalLitColor;
vec3 linearColor;
vec3 gamma = vec3(1.0/2.2);
vec3 hdrColor;
vec3 toneMap;

vec3 normal;
vec3 camera_world_position;
vec3 object_world_position;
vec3 light_world_position;
vec3 light_direction;

void ambientLight(){
    if(u_ambient_enabled == 1){
        ambient_color = u_ambient_color;
    }
    else{
        ambient_color = vec4(1.0, 1.0, 1.0, 1.0);
    }
}

void diffuseLight(){
    if(u_diffuse_enabled == 1){
        // Use only modelNormal, not modelViewNormal
        // Reason is because it will change colors as you move the camera around,
        // which is not a real world scenario

        // Observations:
        // normal = normalize(v_model_normal) means the values of the norm will never change.
        //          For example when the object rotates, the light colors are stuck on
        //          that side of the object!

        diffuseFactor = max(0.0, dot(normal, light_direction));
        diffuse_color = clamp(vec4(diffuseFactor * u_diffuse_color.rgb, 1.0), 0.0, 1.0);
    }
}

void specularLight(){
    if (u_specular_enabled == 1){
        // Use model_position, not vec3(v_model_position). When the object rotates, once the normals
        // point the other way, specular light disappears! With model_position, the light
        // stays on them at least the whole way round.

        // the reflect() method does this formula: reflect(I, N) = I - 2.0 * dot(N, I) * N

        vec3 reflect_direction = reflect(-light_direction, vec3(normal.x, normal.y, normal.z));
        vec3 camera_direction = normalize(camera_world_position - object_world_position);
        float cos_angle = max(0.0, dot(camera_direction, reflect_direction));

        specularityFactor = 0.0;

        if (diffuseFactor >= 0.0) {
            specularityFactor = pow(cos_angle, u_specular_intensity);
        }

        specular_color =  clamp(vec4(vec3(u_specular_color) * specularityFactor, 1.0), 0.0, 1.0);
    }
}

void main() {
    //mat4 TI_model_matrix = transpose(inverse(u_model_matrix));

    //model_position = vec3(u_model_matrix * vec4(v_position, 1.0));
    //model_camera_position = vec3(u_model_matrix * vec4(u_camera_position, 1.0));
    //model_light_position = vec3(u_model_matrix * vec4(u_light_position, 1.0));
    //model_normal = normalize(vec3(TI_model_matrix * vec4(v_normal, 0.0)));

    object_world_position = v_model_position;
    camera_world_position = u_camera_position;
    light_world_position = u_light_position;
    normal = v_model_normal;

    vec4 texture_color0 = texture(u_textureunit, v_texturecoordinates);
    vec4 texture_color1 = vec4(0.0, 0.0, 0.0, 1.0);
    vec4 texture_color2 = vec4(0.0, 0.0, 0.0, 1.0);

    vec3 N = normalize(mat3(transpose(inverse(u_model_matrix))) * v_normal);
    vec3 I = normalize(v_model_position - v_camera_position);
    vec3 R = normalize(reflect(I, N));

    if (u_cubemap_enabled == 1) {
        if (u_reflective_cube_enabled == 1) {
            texture_color1 = texture(u_cubemap_unit, R);
            texture_color1 = vec4(texture_color1.rgb * u_reflectiveness, texture_color1.a);
        }
        else {
            texture_color1 = texture(u_cubemap_unit, v_texturecoordinates3D);
            texture_color1 = vec4(texture_color1.rgb, texture_color1.a);
        }
    }

    if (u_texture_enabled2 == 1) {
        texture_color2 = texture(u_textureunit2, v_texturecoordinates);
        texture_color2 = vec4(texture_color2.rgb, texture_color2.a);
    }



    if (u_light_enabled == 1){
        if (u_light_type == 0) {
            // Point Light
            light_direction = normalize(light_world_position - object_world_position);
        }
        else if (u_light_type == 1) {
            // Directional Light
            light_direction = normalize(u_light_direction);
        }
        else if (u_light_type == 2) {
            // TODO Spot Light
            light_direction = vec3(0.0);
        }


        ambientLight();
        diffuseLight();
        specularLight();

        finalLitColor = vec3(1.0, 1.0, 1.0);

        if (u_light_type == 0) {
            // Point Light
            float dist = distance(light_world_position, object_world_position);
            float attenuation_constant = 1.0;
            float attenuation_linear = 0.001;
            float attenuation_exp = 0.0001;
            float attenuation = 1.0 / (attenuation_constant + attenuation_linear * dist + attenuation_exp * dist * dist);

            linearColor = vec3(u_ambient_color.r + attenuation * (diffuse_color.r + specular_color.r),
                                u_ambient_color.g + attenuation* (diffuse_color.g + specular_color.g),
                                u_ambient_color.b + attenuation * (diffuse_color.b + specular_color.b));

            vec3 gamma = vec3(1.0/2.2);
            finalLitColor = pow(linearColor, gamma);
        }
        else {
            // Directional Light
            linearColor = vec3(u_ambient_color.r + diffuse_color.r + specular_color.r,
                                u_ambient_color.g + diffuse_color.g + specular_color.g,
                                u_ambient_color.b + diffuse_color.b + specular_color.b);

            vec3 gamma = vec3(1.0/2.2);
            finalLitColor = pow(linearColor, gamma);
        }

        if (u_texture_enabled == 1 && u_cubemap_enabled == 1 && u_texture_enabled2 == 0) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color0.r + texture_color1.r,
            texture_color0.g + texture_color1.g,
            texture_color0.b + texture_color1.b,
            texture_color0.a + texture_color1.a) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 1 && u_cubemap_enabled == 0 && u_texture_enabled2 == 0){
            color = vec4(texture_color0.r,
            texture_color0.g,
            texture_color0.b,
            texture_color0.a) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 1 && u_texture_enabled2 == 0){
            color = vec4(texture_color1.r,
            texture_color1.g,
            texture_color1.b,
            texture_color1.a) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);
        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 0 && u_texture_enabled2 == 1){
            color = vec4(texture_color2.r,
            texture_color2.g,
            texture_color2.b,
            1.0) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);
        }
        else if (u_texture_enabled == 1 && u_cubemap_enabled == 0 && u_texture_enabled2 == 1) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color0.r + texture_color2.r,
            texture_color0.g + texture_color2.g,
            texture_color0.b + texture_color2.b,
            texture_color0.a + texture_color2.a) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 1 && u_texture_enabled2 == 1) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color1.r + texture_color2.r,
            texture_color1.g + texture_color2.g,
            texture_color1.b + texture_color2.b,
            texture_color1.a + texture_color2.a) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 1 && u_cubemap_enabled == 1 && u_texture_enabled2 == 1) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color0.r + texture_color1.r + texture_color2.r,
            texture_color0.g + texture_color1.g + texture_color2.g,
            texture_color0.b + texture_color1.b + texture_color2.b,
            texture_color0.a + texture_color1.a + texture_color2.a) *
            vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 0 && u_texture_enabled2 == 0){
            color = vec4(v_color.r * finalLitColor.r * u_RGBA.r,
            v_color.g * finalLitColor.g * u_RGBA.g,
            v_color.b * finalLitColor.b * u_RGBA.b,
            v_color.a * u_RGBA.a);
        }
    }
    else{
        // No light
        if (u_texture_enabled == 1 && u_cubemap_enabled == 1 && u_texture_enabled2 == 0){
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color0.r + texture_color1.r,
            texture_color0.g + texture_color1.g,
            texture_color0.b + texture_color1.b,
            texture_color0.a + texture_color1.a) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 1 && u_cubemap_enabled == 0 && u_texture_enabled2 == 0){
            color = vec4(texture_color0.r,
            texture_color0.g,
            texture_color0.b,
            texture_color0.a) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 1 && u_texture_enabled2 == 0){
            color = vec4(texture_color1.r,
            texture_color1.g,
            texture_color1.b,
            1.0) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 0 && u_texture_enabled2 == 1){
            color = vec4(texture_color2.r,
            texture_color2.g,
            texture_color2.b,
            1.0) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);
        }
        else if (u_texture_enabled == 1 && u_cubemap_enabled == 0 && u_texture_enabled2 == 1) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color0.r + texture_color2.r,
            texture_color0.g + texture_color2.g,
            texture_color0.b + texture_color2.b,
            texture_color0.a + texture_color2.a) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 0 && u_cubemap_enabled == 1 && u_texture_enabled2 == 1) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color1.r + texture_color2.r,
            texture_color1.g + texture_color2.g,
            texture_color1.b + texture_color2.b,
            texture_color1.a + texture_color2.a) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else if (u_texture_enabled == 1 && u_cubemap_enabled == 1 && u_texture_enabled2 == 1) {
            // Incase you forget how to add a toneMap overtime...
            // gl_FragColor = vec4(toneMap.rgb,  texture(u_textureunit, v_texturecoordinates).a) *
            // Problem is, is that it is not that colorful. its a dull resident evil greyish world then

            color = vec4(texture_color0.r + texture_color1.r + texture_color2.r,
            texture_color0.g + texture_color1.g + texture_color2.g,
            texture_color0.b + texture_color1.b + texture_color2.b,
            texture_color0.a + texture_color1.a + texture_color2.a) *
            vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);

        }
        else{
            color = vec4(v_color.r * u_RGBA.r,
            v_color.g * u_RGBA.g,
            v_color.b * u_RGBA.b,
            v_color.a * u_RGBA.a);
        }
    }
}