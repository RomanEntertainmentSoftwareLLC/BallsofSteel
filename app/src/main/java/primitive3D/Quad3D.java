package primitive3D;

/**
 * Created by Roman Entertainment Software LLC on 4/24/2018.
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

public class Quad3D {
    private final String tag = "QUAD3D";

    private Context context;
    
    private float x1, y1, z1, w1;
    private float x2, y2, z2, w2;
    private float x3, y3, z3, w3;
    private float x4, y4, z4, w4;

    private float red, green, blue, alpha;

    private float t, v;

    private float[] vertexList;
    private float[] colorList;
    private float[] textureCoordList;
    private float[] normalList;

    public float[] worldPositionMatrix = new float[16];
    public float[] worldRotateMatrix = new float[16];
    public float[] worldRotateMatrixX = new float[16];
    public float[] worldRotateMatrixY = new float[16];
    public float[] worldRotateMatrixZ = new float[16];

    public float[] localPositionMatrix = new float[16];
    public float[] localRotateMatrix = new float[16];
    public float[] localRotateMatrixX = new float[16];
    public float[] localRotateMatrixY = new float[16];
    public float[] localRotateMatrixZ = new float[16];

    public float[] modelMatrix = new float[16];
    public float[] localMatrix = new float[16];
    public float[] worldMatrix = new float[16];
    public float[] invertedModelViewMatrix = new float[16];
    public float[] unScaledModelViewMatrix = new float[16];
    public float[] scaledModelViewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureCoordBuffer;
    private FloatBuffer normalBuffer;

    private int uRGBAHandle;
    private int uMVPMatrixHandle;
    private int uMVMatrixHandle;
    private int uMMatrixHandle;
    private int uTextureUnitHandle;
    private int aPositionHandle;
    private int aColorHandle;
    private int aTextureCoordinateHandle;
    private int uTextureEnabledHandle;
    private int aNormalHandle;
    private int uTwoSidedHandle;
    private int uCubeMapUnitHandle;
    private int uCubeMapEnabledHandle;
    private int uReverseReflectionHandle;
    private int uObjectPositionHandle;

    private int program;

    public boolean texture_enabled = true;
    public boolean twoSided = true;

    public Vertex3D local_position;
    public Vector3D local_angle;
    public Vertex3D world_position;
    public Vector3D world_angle;
    public Vector3D scalar;

    public Quad3D(){

    }

    public Quad3D(Context context, int program, float x1, float y1, float z1, float w1,
                  float x2, float y2, float z2, float w2,
                  float x3, float y3, float z3, float w3,
                  float x4, float y4, float z4, float w4,
                  float red, float green, float blue, float alpha,
                  float t, float v){
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

        this.x3 = x3;
        this.y3 = y3;
        this.z3 = z3;
        this.w3 = w3;

        this.x4 = x4;
        this.y4 = y4;
        this.z4 = z4;
        this.w4 = w4;

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

        this.t = t;
        this.v = v;

        Log.d(tag, "Populating vertexList");
        vertexList = new float[]{x1, y1, z1, w1,
                                 x2, y2, z2, w2,
                                 x3, y3, z3, w3,
                                 x4, y4, z4, w4};

        Log.d(tag, "Populating colorList");
        colorList = new float[]{red, green, blue, alpha,
                                red, green, blue, alpha,
                                red, green, blue, alpha,
                                red, green, blue, alpha};

        Log.d(tag, "Populating textureCoordList");
        textureCoordList = new float[]{0f, 0f,
                t, 0f,
                0f, v,
                t, v};

        Log.d(tag, "Populating normalList");
        normalList = new float[12];

        Vector3D vectorA = new Vector3D();
        Vector3D vectorB = new Vector3D();
        Vector3D normal = new Vector3D();

        local_position = new Vertex3D();
        local_angle = new Vector3D();
        world_position = new Vertex3D();
        world_angle = new Vector3D();
        scalar = new Vector3D(1f, 1f, 1f);

        // Vertex 1
        vectorA.set(new Vertex3D(x1, y1, z1), new Vertex3D(x2, y2, z2));
        Log.d("vectorA", String.valueOf(vectorA.x) + ", " + String.valueOf(vectorA.y) + ", " + String.valueOf(vectorA.z));

        vectorB.set(new Vertex3D(x1, y1, z1), new Vertex3D(x3, y3, z3));
        Log.d("vectorB", String.valueOf(vectorB.x) + ", " + String.valueOf(vectorB.y) + ", " + String.valueOf(vectorB.z));

        normal = vectorA._setNormal(vectorB);
        Log.d("normal", String.valueOf(normal.x) + ", " + String.valueOf(normal.y) + ", " + String.valueOf(normal.z));

        for (int i = 0; i < 12; i += 3){
            normalList[i + 0] = normal.x;
            normalList[i + 1] = normal.y;
            normalList[i + 2] = normal.z;
        }


        Log.d(tag, "Creating vertexBuffer");
        vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexList);
        Log.d(tag, "Creating colorBuffer");
        colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(colorList);
        Log.d(tag, "Creating textureCoordBuffer");
        textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureCoordList);
        Log.d(tag, "Creating normalBuffer");
        normalBuffer = ByteBuffer.allocateDirect(normalList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(normalList);

        glUseProgram(program);

        Log.d(tag, "aPositionHandle glGetUniformLocation");
        aPositionHandle = 0; //glGetAttribLocation(program, A_POSITION);
        Log.d(tag, "aColorHandle glGetUniformLocation");
        aColorHandle = 1; //glGetAttribLocation(program, A_COLOR);
        Log.d(tag, "aTextureCoordinateHandle glGetUniformLocation");
        aTextureCoordinateHandle = 2; //glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        Log.d(tag, "aNormalHandle glGetAttribLocation");
        aNormalHandle = 3; //glGetAttribLocation(program, A_NORMAL);

        Log.d(tag, "uRGBAHandle glGetUniformLocation");
        uRGBAHandle = glGetUniformLocation(program, U_RGBA);
        Log.d(tag, "uMVPMatrixHandle glGetUniformLocation");
        uMVPMatrixHandle = glGetUniformLocation(program, U_MVPMATRIX);
        Log.d(tag, "uMVMatrixHandle glGetUniformLocation");
        uMVMatrixHandle = glGetUniformLocation(program, U_MVMATRIX);
        Log.d(tag, "uMMatrixHandle glGetUniformLocation");
        uMMatrixHandle = glGetUniformLocation(program, U_MMATRIX);
        Log.d(tag, "uTextureUnitHandle glGetUniformLocation");
        uTextureUnitHandle = glGetUniformLocation(program, U_TEXTURE_UNIT);
        Log.d(tag, "uTextureEnabledHandle glGetUniformLocation");
        uTextureEnabledHandle = glGetUniformLocation(program, U_TEXTURE_ENABLED);
        Log.d(tag, "uCubeMapUnitHandle glGetUniformLocation");
        uCubeMapUnitHandle = glGetUniformLocation(program, U_CUBEMAP_UNIT);
        Log.d(tag, "uCubeMapEnabledHandle glGetUniformLocation");
        uCubeMapEnabledHandle = glGetUniformLocation(program, U_CUBEMAP_ENABLED);

        uTwoSidedHandle = glGetUniformLocation(program, U_TWOSIDED_ENABLED);
        uReverseReflectionHandle = glGetUniformLocation(program, U_REVERSE_REFLECTION);
        uObjectPositionHandle = glGetUniformLocation(program, U_OBJECT_POSITION);
        
        vertexList = null;
        colorList = null;
        textureCoordList = null;
        normalList = null;
    }

    private void checkTwoSided(){
        if (twoSided == true){
            glUniform1i(uTwoSidedHandle, 1);
        }
        else{
            glUniform1i(uTwoSidedHandle, 0);
        }
    }

    public void setTexture(int texture){
        if(texture_enabled == true){
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            glUniform1i(uTextureUnitHandle, 0);
            
            glUniform1i(uTextureEnabledHandle, 1);
        }
        else{
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
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

    public void updateMatrices(Camera3D camera){
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Matrix.setIdentityM(worldPositionMatrix, 0);
        Matrix.setIdentityM(worldRotateMatrix, 0);
        Matrix.setIdentityM(worldRotateMatrixX, 0);
        Matrix.setIdentityM(worldRotateMatrixY, 0);
        Matrix.setIdentityM(worldRotateMatrixZ, 0);

        Matrix.setIdentityM(localPositionMatrix, 0);
        Matrix.setIdentityM(localRotateMatrix, 0);
        Matrix.setIdentityM(localRotateMatrixX, 0);
        Matrix.setIdentityM(localRotateMatrixY, 0);
        Matrix.setIdentityM(localRotateMatrixZ, 0);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(localMatrix, 0);
        Matrix.setIdentityM(worldMatrix, 0);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Matrix.translateM(worldPositionMatrix, 0, world_position.x, world_position.y, world_position.z);

        Matrix.rotateM(worldRotateMatrixZ, 0, world_angle.z, 0f, 0f, 1f);
        Matrix.rotateM(worldRotateMatrixX, 0, world_angle.x, 1f, 0f, 0f);
        Matrix.rotateM(worldRotateMatrixY, 0, world_angle.y, 0f, 1f, 0f);

        Matrix.multiplyMM(worldRotateMatrix, 0, worldRotateMatrixZ, 0,worldRotateMatrixX, 0);
        Matrix.multiplyMM(worldRotateMatrix, 0, worldRotateMatrix, 0,worldRotateMatrixY, 0);

        Matrix.multiplyMM(worldMatrix, 0, worldPositionMatrix, 0,worldRotateMatrix, 0);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Matrix.translateM(localPositionMatrix, 0, local_position.x, local_position.y, local_position.z);

        Matrix.rotateM(localRotateMatrixZ, 0, local_angle.z, 0f, 0f, 1f);
        Matrix.rotateM(localRotateMatrixX, 0, local_angle.x, 1f, 0f, 0f);
        Matrix.rotateM(localRotateMatrixY, 0, local_angle.y, 0f, 1f, 0f);

        Matrix.multiplyMM(localRotateMatrix, 0, localRotateMatrixZ, 0,localRotateMatrixX, 0);
        Matrix.multiplyMM(localRotateMatrix, 0, localRotateMatrix, 0,localRotateMatrixY, 0);

        Matrix.multiplyMM(localMatrix, 0, localPositionMatrix, 0,localRotateMatrix, 0);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Matrix.multiplyMM(modelMatrix, 0, worldMatrix, 0, localMatrix, 0);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //The mvMatrix is the one fed into the shader, and must be done WITHOUT SCALING the object.
        //Had to comment out the next line in order for directional light to STAT PUT and not go with the camera!
        glUniformMatrix4fv(uMMatrixHandle, 1, false, modelMatrix, 0);
        //mvMatrix = modelMatrix;
        Matrix.multiplyMM(unScaledModelViewMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
        glUniformMatrix4fv(uMVMatrixHandle, 1, false, unScaledModelViewMatrix, 0);

        //Now we can scale the object and project it
        Matrix.scaleM(modelMatrix, 0, scalar.x, scalar.y, scalar.z);

        Matrix.multiplyMM(scaledModelViewMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
        Matrix.invertM(invertedModelViewMatrix, 0, scaledModelViewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, camera.projectionMatrix, 0, scaledModelViewMatrix, 0);

        glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0);

        glUniform3f(uObjectPositionHandle, world_position.x, world_position.y, world_position.z);
    }

    public void draw(){
        glDepthFunc(GL_LESS);
        checkTwoSided();
        enableVertexAttribArrays();
        bindData();
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        disableVertexAttribArrays();
    }

    private void enableVertexAttribArrays(){
        glEnableVertexAttribArray(aPositionHandle);
        glEnableVertexAttribArray(aColorHandle);
        glEnableVertexAttribArray(aTextureCoordinateHandle);
        glEnableVertexAttribArray(aNormalHandle);
    }

    private void disableVertexAttribArrays(){
        glDisableVertexAttribArray(aPositionHandle);
        glDisableVertexAttribArray(aColorHandle);
        glDisableVertexAttribArray(aTextureCoordinateHandle);
        glDisableVertexAttribArray(aNormalHandle);
    }

    private void bindData(){
        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_3D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_3D, vertexBuffer);

        colorBuffer.position(0);
        glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, colorBuffer);

        textureCoordBuffer.position(0);
        glVertexAttribPointer(aTextureCoordinateHandle, TEXTURE_COORDINATES_COMPONENT_COUNT, GL_FLOAT, false, TEXTURE_COORDINATE_COMPONENT_STRIDE, textureCoordBuffer);

        normalBuffer.position(0);
        glVertexAttribPointer(aNormalHandle, NORMAL_COMPONENT_COUNT, GL_FLOAT, false, NORMAL_COMPONENT_STRIDE, normalBuffer);
    }

    public void release(){
        vertexList = null;
        colorList = null;
        textureCoordList = null;
        normalList = null;
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

        if (textureCoordBuffer != null){
            textureCoordBuffer.clear();
            textureCoordBuffer = null;
        }

        if (normalBuffer != null){
            normalBuffer.clear();
            normalBuffer = null;
        }
    }
}
