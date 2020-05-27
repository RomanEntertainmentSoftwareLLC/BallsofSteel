package romanentertainmentsoftware.ballsofsteel;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUseProgram;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class ContrastBoostBuffer {
    public float red;
    public float green;
    public float blue;

    public int program;

    private int uRedHandle;
    private int uGreenHandle;
    private int uBlueHandle;

    public ContrastBoostBuffer() {

    }

    public ContrastBoostBuffer(int program) {
        this.program = program;
    }

    public void setup() {
        glUseProgram(program);
        uRedHandle = glGetUniformLocation(program, U_CONTRAST_RED);
        uGreenHandle = glGetUniformLocation(program, U_CONTRAST_GREEN);
        uBlueHandle = glGetUniformLocation(program, U_CONTRAST_BLUE);
    }

    public void update() {
        glUniform1f(uRedHandle, red);
        glUniform1f(uGreenHandle, green);
        glUniform1f(uBlueHandle, blue);
    }
}
