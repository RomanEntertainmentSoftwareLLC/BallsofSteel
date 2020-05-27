package romanentertainmentsoftware.ballsofsteel;

/**
 * Created by Roman Entertainment Software LLC on 4/18/2019.
 */

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import math2D.Vertex2D;
import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class MultiTexture2D {
    private final String TAG = "MultiTexture2D";

    //private Context context;

    private float x1, y1;
    private float x2, y2;
    private float x3, y3;
    private float x4, y4;

    private float red, green, blue, alpha;

    private float t, v;

    private float[] vertexList;
    private float[] colorList;
    private float[] textureCoordList;

    private float[] viewMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureCoordBuffer;

    private int uRGBAHandle;
    private int uMatrixHandle;
    private int uTexture0;
    private int uTexture1;
    private int aPositionHandle;
    private int aColorHandle;
    private int aTextureCoordinateHandle;

    private int program;

    public boolean texture_enabled = true;

    public Vertex2D position = new Vertex2D();
    public float width;
    public float height;

    public MultiTexture2D(){

    }

    public MultiTexture2D(Context context, int program, float x1, float y1,
                  float x2, float y2,
                  float x3, float y3,
                  float x4, float y4,
                  float red, float green, float blue, float alpha,
                  float t, float v){
        //this.context = context;
        this.program = program;

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;

        position.x = x1;
        position.y = y1;

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

        width = x2 - x1;
        height = y3 - y1;

        Log.d(TAG, "Populating vertexList");
        vertexList = new float[]{x1, y1, x2, y2, x3, y3,
                x2, y2, x3, y3, x4, y4};

        Log.d(TAG, "Populating colorList");
        colorList = new float[]{red, green, blue, alpha,
                red, green, blue, alpha,
                red, green, blue, alpha,
                red, green, blue, alpha,
                red, green, blue, alpha,
                red, green, blue, alpha};

        Log.d(TAG, "Populating textureCoordList");
        textureCoordList = new float[]{0f, 0f, t, 0f, 0f, v,
                t, 0f, 0f, v, t, v};

        //Log.d(TAG, "Creating shader program");
        //program = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), Shader.readTextFileFromResource(context, R.raw.texture_fragment_shader));

        Log.d(TAG, "Creating vertexBuffer");
        vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexList);
        Log.d(TAG, "Creating colorBuffer");
        colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(colorList);
        Log.d(TAG, "Creating textureCoordBuffer");
        textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureCoordList);

        Log.d(TAG, "uRGBAHandle glGetUniformLocation");
        uRGBAHandle = glGetUniformLocation(program, U_RGBA);
        Log.d(TAG, "uMatrixHandle glGetUniformLocation");
        uMatrixHandle = glGetUniformLocation(program, U_MVPMATRIX);
        //Log.d(TAG, "uTextureUnitHandle glGetUniformLocation");
        //uTexture0 = glGetUniformLocation(program, U_TEXTURE_UNIT);
        Log.d(TAG, "aPositionHandle glGetAttribLocation");
        aPositionHandle = glGetAttribLocation(program, A_POSITION);
        Log.d(TAG, "aColorHandle glGetAttribLocation");
        aColorHandle = glGetAttribLocation(program, A_COLOR);
        Log.d(TAG, "aTextureCoordinateHandle glGetAttribLocation");
        aTextureCoordinateHandle = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        uTexture0 = glGetUniformLocation(program, "texture0");
        uTexture1 = glGetUniformLocation(program, "texture1");

        // Clear the arrays when we are done with them:
        vertexList = null;
        colorList = null;
        textureCoordList = null;
    }

    public void setTextures(int texture0, int texture1){
        if(texture_enabled == true) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture0);
            glUniform1i(uTexture0, 0);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, texture1);
            glUniform1i(uTexture1, 1);
        }
        else {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, 0);
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

    public void updateMatrices(float[] projectionMatrix, float aspectRatio, float angle){
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, position.x * aspectRatio, position.y, 0f);

        Matrix.rotateM(modelMatrix, 0, angle, 0f, 0f, 1f);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        glUniformMatrix4fv(uMatrixHandle, 1, false, mvpMatrix, 0);
    }

    public void draw(){
        //These 2 lines are necessary or alphas will overlap backgrounds!
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        enableVertexAttribArrays();
        glDrawArrays(GL_TRIANGLES, 0, 6);
        disableVertexAttribArrays();

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void enableVertexAttribArrays(){
        glEnableVertexAttribArray(aPositionHandle);
        glEnableVertexAttribArray(aColorHandle);
        glEnableVertexAttribArray(aTextureCoordinateHandle);
    }

    private void disableVertexAttribArrays(){
        glDisableVertexAttribArray(aPositionHandle);
        glDisableVertexAttribArray(aColorHandle);
        glDisableVertexAttribArray(aTextureCoordinateHandle);
    }

    public void bindData(){
        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_2D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_2D, vertexBuffer);

        colorBuffer.position(0);
        glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, colorBuffer);

        textureCoordBuffer.position(0);
        glVertexAttribPointer(aTextureCoordinateHandle, TEXTURE_COORDINATES_COMPONENT_COUNT, GL_FLOAT, false, TEXTURE_COORDINATE_COMPONENT_STRIDE, textureCoordBuffer);
    }

    public void release(){
        disableVertexAttribArrays();
        viewMatrix = null;
        modelMatrix = null;
        mvpMatrix = null;
        position = null;

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
    }
}