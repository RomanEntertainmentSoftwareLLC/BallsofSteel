package romanentertainmentsoftware.ballsofsteel;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1fv;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class BlurBuffer {
    public final int MAX_WEIGHTS = 51;
    public float sigma; // Threshold
    public int kernel_diameter;
    public float mu; // mean or kernel radius
    public float[] weights = new float[MAX_WEIGHTS];
    public int screen_width;
    public int number_of_times_blurred;

    private int uSigmaHandle;
    private int uKernelDiameterHandle;
    private int uMuHandle;
    private int uWeightsHandle;
    private int uScreenWidthHandle;

    private int uSigmaHandle2;
    private int uKernelDiameterHandle2;
    private int uMuHandle2;
    private int uWeightsHandle2;
    private int uScreenWidthHandle2;

    int horizontalProgram;
    int verticalProgram;

    public BlurBuffer() {

    }

    public BlurBuffer(int horizontalProgram, int verticalProgram) {
        this.horizontalProgram = horizontalProgram;
        this.verticalProgram = verticalProgram;

        number_of_times_blurred = 1;
    }

    public void setup(){
        glUseProgram(horizontalProgram);

        uSigmaHandle = glGetUniformLocation(horizontalProgram, U_SIGMA);
        uKernelDiameterHandle = glGetUniformLocation(horizontalProgram, U_KERNEL_DIAMETER);
        uMuHandle = glGetUniformLocation(horizontalProgram, U_MU);
        uWeightsHandle = glGetUniformLocation(horizontalProgram, U_WEIGHTS);
        uScreenWidthHandle = glGetUniformLocation(horizontalProgram, U_SCREEN_WIDTH);

        glUseProgram(verticalProgram);

        uSigmaHandle2 = glGetUniformLocation(verticalProgram, U_SIGMA);
        uKernelDiameterHandle2 = glGetUniformLocation(verticalProgram, U_KERNEL_DIAMETER);
        uMuHandle2 = glGetUniformLocation(verticalProgram, U_MU);
        uWeightsHandle2 = glGetUniformLocation(verticalProgram, U_WEIGHTS);
        uScreenWidthHandle2 = glGetUniformLocation(verticalProgram, U_SCREEN_WIDTH);
    }

    public void update_horizontal_blur() {
        glUniform1f(uSigmaHandle, sigma);
        glUniform1i(uKernelDiameterHandle, kernel_diameter);
        glUniform1i(uScreenWidthHandle, screen_width);
        glUniform1f(uMuHandle, mu);
        glUniform1fv(uWeightsHandle, MAX_WEIGHTS, weights, 0);
    }

    public void update_vertical_blur() {
        glUniform1f(uSigmaHandle2, sigma);
        glUniform1i(uKernelDiameterHandle2, kernel_diameter);
        glUniform1i(uScreenWidthHandle2, screen_width);
        glUniform1f(uMuHandle2, mu);
        glUniform1fv(uWeightsHandle2, MAX_WEIGHTS, weights, 0);
    }

}
