package romanentertainmentsoftware.ballsofsteel;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUseProgram;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class BrightFilterBuffer {
    public float bright_filter_range;
    public int program;
    private int uBrightFilterRangeHandle;

    public BrightFilterBuffer() {

    }

    public BrightFilterBuffer(int program) {
        this.program = program;
    }

    public void setup(){
        glUseProgram(program);
        uBrightFilterRangeHandle = glGetUniformLocation(program, U_BRIGHT_FILTER_RANGE);
    }

    public void update() {
        glUniform1f(uBrightFilterRangeHandle, bright_filter_range);
    }
}
