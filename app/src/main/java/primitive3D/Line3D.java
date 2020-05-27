package primitive3D;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import romanentertainmentsoftware.ballsofsteel.Camera3D;
import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

/**
 * Created by Roman Entertainment Software LLC on 5/17/2018.
 */
public class Line3D {
    private final String TAG = "LINE3D";

    private Context context;

    private float x1, y1, z1, w1;
    private float x2, y2, z2, w2;

    private float red, green, blue, alpha;

    private float[] vertexList;
    private float[] colorList;

    private float[] modelMatrix = new float[16];
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int uRGBAHandle;
    private int uMVPMatrixHandle;
    private int uMVMatrixHandle;
    private int aPositionHandle;
    private int aColorHandle;

    private int program;

    public Line3D(){

    }

    public Line3D(Context context, int program, float x1, float y1, float z1, float w1,
                  float x2, float y2, float z2, float w2,
                  float red, float green, float blue, float alpha){
        this.context = context;
        this.program = program;

        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.w1 = w1;

        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.w2 = w2;

        if (red <= 0f) red = 0f;
        if (green <= 0f) green = 0f;
        if (blue <= 0f) blue = 0f;
        if (alpha <= 0f) alpha = 0f;

        if (red >= 1f) red = 1f;
        if (green >= 1f) green = 1f;
        if (blue >= 1f) blue = 1f;
        if (alpha >= 1f) alpha = 1f;

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;

        //Log.d(TAG, "Populating vertexList");
        vertexList = new float[]{x1, y1, z1, w1,
                x2, y2, z2, w2};

        //Log.d(TAG, "Populating colorList");
        colorList = new float[]{red, green, blue, alpha,
                red, green, blue, alpha};

        //Log.d(TAG, "Creating vertexBuffer");
        vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexList);
        //Log.d(TAG, "Creating colorBuffer");
        colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(colorList);

        glUseProgram(program);

        Log.d(TAG, "uRGBAHandle glGetUniformLocation");
        uRGBAHandle = glGetUniformLocation(program, U_RGBA);
        Log.d(TAG, "uMVPMatrixHandle glGetUniformLocation");
        uMVPMatrixHandle = glGetUniformLocation(program, U_MVPMATRIX);
        Log.d(TAG, "uMVMatrixHandle glGetUniformLocation");
        uMVMatrixHandle = glGetUniformLocation(program, U_MVMATRIX);

        Log.d(TAG, "aPositionHandle glGetAttribLocation");
        aPositionHandle = 0; //glGetAttribLocation(program, A_POSITION);
        Log.d(TAG, "aColorHandle glGetAttribLocation");
        aColorHandle = 1; //glGetAttribLocation(program, A_COLOR);

        //glUniform4f(uRGBAHandle, red, green, blue, alpha);

        vertexList = null;
        colorList = null;
    }

    public void rgba(float red, float green, float blue, float alpha){
        if (red <= 0f) red = 0f;
        if (green <= 0f) green = 0f;
        if (blue <= 0f) blue = 0f;
        if (alpha <= 0f) alpha = 0f;

        if (red >= 1f) red = 1f;
        if (green >= 1f) green = 1f;
        if (blue >= 1f) blue = 1f;
        if (alpha >= 1f) alpha = 1f;

        glUniform4f(uRGBAHandle, red, green, blue, alpha);
    }

    public void updateMatrices(Camera3D camera, float x, float y, float z, float angle, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ){
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, x, y, z);

        if (angleX != 0f || angleY != 0f || angleZ != 0f)
            Matrix.rotateM(modelMatrix, 0, angle, angleX, angleY, angleZ);

        //The mvMatrix is the one fed into the shader, and must be done WITHOUT SCALING the object.
        //Had to comment out the next line in order for directional light to STAT PUT and not go with the camera!
        mvMatrix = modelMatrix;
        //Matrix.multiplyMM(mvMatrix, 0, Matrix4x4.convertTo1DArray(Render.camera.viewMatrix), 0, modelMatrix, 0);
        glUniformMatrix4fv(uMVMatrixHandle, 1, false, mvMatrix, 0);

        //Now we can scale the object and project it
        Matrix.scaleM(modelMatrix, 0, scaleX, scaleY, scaleZ);

        Matrix.multiplyMM(mvpMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, camera.projectionMatrix, 0, mvpMatrix, 0);

        glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0);
    }

    public void draw(float lineThickness){
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glDisable(GL_CULL_FACE);
        enableVertexAttribArrays();
        bindData();
        glLineWidth(lineThickness);
        glDrawArrays(GL_LINES, 0, 2);
        disableVertexAttribArrays();
    }

    private void enableVertexAttribArrays(){
        glEnableVertexAttribArray(aPositionHandle);
        glEnableVertexAttribArray(aColorHandle);
    }

    private void disableVertexAttribArrays(){
        glDisableVertexAttribArray(aPositionHandle);
        glDisableVertexAttribArray(aColorHandle);
    }

    private void bindData(){
        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_3D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_3D, vertexBuffer);

        colorBuffer.position(0);
        glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, colorBuffer);
    }

    public void release(){
        vertexList = null;
        colorList = null;
        modelMatrix = null;
        mvMatrix = null;
        mvpMatrix = null;

        if (vertexBuffer != null){
            vertexBuffer.clear();
            vertexBuffer = null;
        }

        if (colorBuffer != null){
            colorBuffer.clear();
            colorBuffer = null;
        }
    }

}
