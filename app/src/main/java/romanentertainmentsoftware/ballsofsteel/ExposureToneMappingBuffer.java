package romanentertainmentsoftware.ballsofsteel;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUseProgram;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class ExposureToneMappingBuffer {
    public float gamma;
    public float exposure;
    public int program;
    private int uGammaHandle;
    private int uExposureHandle;

    public ExposureToneMappingBuffer() {

    }

    public ExposureToneMappingBuffer(int program) {
        this.program = program;
    }

    public void setup() {
        glUseProgram(program);
        uGammaHandle = glGetUniformLocation(program, U_GAMMA);
        uExposureHandle = glGetUniformLocation(program, U_EXPOSURE);
    }

    public void update() {
        glUniform1f(uGammaHandle, gamma);
        glUniform1f(uExposureHandle, exposure);
    }
}
