package primitive3D;

/**
 * Created by Roman Entertainment Software LLC on 5/15/2018.
 */
import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import math3D.Vector3D;
import math3D.Vertex3D;
import romanentertainmentsoftware.ballsofsteel.Camera3D;
import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class Particle3D {
    private final String TAG = "PARTICLE3D";

    private Context context;
    
    private float x1, y1, z1, w1;

    private float red, green, blue, alpha;

    public Vertex3D local_position;
    public Vertex3D world_position;
    public Vertex3D old_world_position;
    public Vertex3D transformed_position;

    private float[] vertexList;
    private float[] colorList;

    public float[] modelMatrix = new float[16];
    public float[] localMatrix = new float[16];
    public float[] worldMatrix = new float[16];
    public float[] invertedModelViewMatrix = new float[16];
    public float[] modelViewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private int uRGBAHandle;
    private int uMVPMatrixHandle;
    private int aPositionHandle;
    private int aColorHandle;

    private int program;

    public Particle3D(){

    }

    public Particle3D(Context context, int program, float x1, float y1, float z1, float w1,
                  float red, float green, float blue, float alpha){
        this.context = context;
        this.program = program;

        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.w1 = w1;

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

        Log.d(TAG, "Populating vertexList");
        vertexList = new float[]{x1, y1, z1, w1};

        Log.d(TAG, "Populating colorList");
        colorList = new float[]{red, green, blue, alpha};

        local_position = new Vertex3D(0f, 0f, 0f);
        world_position = new Vertex3D(x1, y1, z1);
        old_world_position = new Vertex3D(x1, y1, z1);
        transformed_position = new Vertex3D(0f, 0f, 0f);
    }

    public void setup(){
        Log.d(TAG, "Creating vertexBuffer");
        vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexList);
        Log.d(TAG, "Creating colorBuffer");
        colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(colorList);

        glUseProgram(program);

        Log.d(TAG, "uRGBAHandle glGetUniformLocation");
        uRGBAHandle = glGetUniformLocation(program, U_RGBA);
        Log.d(TAG, "uMVPMatrixHandle glGetUniformLocation");
        uMVPMatrixHandle = glGetUniformLocation(program, U_MVPMATRIX);

        Log.d(TAG, "aPositionHandle glGetUniformLocation");
        aPositionHandle = 0; //glGetAttribLocation(program, A_POSITION);
        Log.d(TAG, "aColorHandle glGetUniformLocation");
        aColorHandle = 1; //glGetAttribLocation(program, A_COLOR);
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

    private void transformWorldMatrices() {
        Matrix.translateM(worldMatrix, 0, world_position.x, world_position.y, world_position.z);
    }

    private void transformLocalMatrices() {
        Matrix.translateM(localMatrix, 0, local_position.x, local_position.y, local_position.z);
    }
    
    public void getTransformedPosition(){
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(localMatrix, 0);
        Matrix.setIdentityM(worldMatrix, 0);

        transformWorldMatrices();
        transformLocalMatrices();

        Matrix.multiplyMM(modelMatrix, 0, worldMatrix, 0, localMatrix, 0);

        transformed_position.x = modelMatrix[12];
        transformed_position.y = modelMatrix[13];
        transformed_position.z = modelMatrix[14];
    }

    public void updateMatrices(Camera3D camera) {
        getTransformedPosition();

        Matrix.multiplyMM(modelViewMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, camera.projectionMatrix, 0, modelViewMatrix, 0);

        glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0);

        Matrix.invertM(invertedModelViewMatrix, 0, modelViewMatrix, 0);
    }

    public void draw(){
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glDisable(GL_CULL_FACE);
        enableVertexAttribArrays();
        bindData();
        glDrawArrays(GL_POINTS, 0, 1);
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