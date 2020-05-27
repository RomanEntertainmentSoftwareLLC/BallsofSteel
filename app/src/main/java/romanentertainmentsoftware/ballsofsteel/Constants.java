package romanentertainmentsoftware.ballsofsteel;

/**
 * Created by Roman Entertainment Software LLC on 4/14/2018.
 */

public class Constants {
    public static final int MAX_FINGERS = 2;
    public static final int MAX_CONTROLLERS = 2;
    public static final int BYTES_PER_FLOAT = 4;
    public static final int POSITION_COMPONENT_COUNT_2D = 2;
    public static final int POSITION_COMPONENT_COUNT_3D = 4;
    public static final int COLOR_COMPONENT_COUNT = 4;
    public static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    public static final int NORMAL_COMPONENT_COUNT = 3;

    public static final int POSITION_COMPONENT_STRIDE_2D = POSITION_COMPONENT_COUNT_2D * BYTES_PER_FLOAT;
    public static final int POSITION_COMPONENT_STRIDE_3D = POSITION_COMPONENT_COUNT_3D * BYTES_PER_FLOAT;
    public static final int COLOR_COMPONENT_STRIDE = COLOR_COMPONENT_COUNT * BYTES_PER_FLOAT;
    public static final int TEXTURE_COORDINATE_COMPONENT_STRIDE = TEXTURE_COORDINATES_COMPONENT_COUNT * BYTES_PER_FLOAT;
    public static final int NORMAL_COMPONENT_STRIDE = NORMAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    public static final String U_RGBA = "u_RGBA";
    public static final String U_MVPMATRIX = "u_mvp_matrix";
    public static final String U_MVMATRIX = "u_model_view_matrix";
    public static final String U_MMATRIX = "u_model_matrix"; // model = local and world matrices combined
    public static final String U_VMATRIX = "u_view_matrix";
    public static final String U_TEXTURE_UNIT = "u_textureunit";
    public static final String U_TEXTURE_UNIT2 = "u_textureunit2";
    public static final String A_POSITION = "a_position";
    public static final String A_COLOR = "a_color";
    public static final String A_TEXTURE_COORDINATES = "a_texturecoordinates";
    public static final String U_TWOSIDED_ENABLED = "u_two_sided_enabled";
    public static final String U_TEXTURE_ENABLED = "u_texture_enabled";
    public static final String U_TEXTURE_ENABLED2 = "u_texture_enabled2";

    //Lighting constants
    public static final String U_AMBIENT_COLOR = "u_ambient_color";
    public static final String U_DIFFUSE_COLOR = "u_diffuse_color";
    public static final String U_SPECULAR_COLOR = "u_specular_color";
    public static final String U_LIGHT_POSITION = "u_light_position";
    public static final String U_LIGHT_DIRECTION = "u_light_direction";
    public static final String U_LIGHT_ENABLED = "u_light_enabled";
    public static final String U_AMBIENT_ENABLED = "u_ambient_enabled";
    public static final String U_DIFFUSE_ENABLED = "u_diffuse_enabled";
    public static final String U_SPECULAR_ENABLED = "u_specular_enabled";
    public static final String U_OBJECT_POSITION = "u_object_position";
    public static final String U_CAMERA_POSITION = "u_camera_position";
    public static final String U_CAMERA_ANGLE = "u_camera_angle";
    public static final String U_SPECULAR_INTENSITY = "u_specular_intensity";
    public static final String U_LIGHT_TYPE = "u_light_type";

    //Exposure Tone Mapping Constants
    public static final String U_GAMMA = "u_gamma";
    public static final String U_EXPOSURE = "u_exposure";

    //Bright Filter Constants
    public static final String U_BRIGHT_FILTER_RANGE = "u_bright_filter_range";

    //Blurring Constants
    public static final String U_SIGMA = "u_sigma";
    public static final String U_KERNEL_DIAMETER = "u_kernel_diameter";
    public static final String U_MU = "u_mu";
    public static final String U_WEIGHTS = "u_weights";
    public static final String U_SCREEN_WIDTH = "u_screen_width";

    //Contrast Boost Constants
    public static final String U_CONTRAST_RED = "u_red";
    public static final String U_CONTRAST_GREEN = "u_green";
    public static final String U_CONTRAST_BLUE = "u_blue";

    //Reflection constants
    public static final String U_CUBEMAP_UNIT = "u_cubemap_unit";
    public static final String U_CUBEMAP_ENABLED = "u_cubemap_enabled";
    public static final String U_REVERSE_REFLECTION = "u_reverse_reflection";
    public static final String U_REFLECTIVE_CUBE_ENABLED = "u_reflective_cube_enabled";
    public static final String U_REFLECTIVENESS = "u_reflectiveness";

    public static final int NUMBER_OF_SIDES_PER_FACE = 3;


}