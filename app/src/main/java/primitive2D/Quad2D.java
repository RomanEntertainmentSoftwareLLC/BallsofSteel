package primitive2D;

/**
 * Created by Roman Entertainment Software LLC on 4/15/2018.
 */

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import math2D.Vertex2D;
import romanentertainmentsoftware.ballsofsteel.R;
import romanentertainmentsoftware.ballsofsteel.Shader;
import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class Quad2D {
    private String tag;

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
    private int uTextureUnitHandle;
    private int aPositionHandle;
    private int aColorHandle;
    private int aTextureCoordinateHandle;

    private int program;

    public boolean texture_enabled = true;

    public Vertex2D position = new Vertex2D();
    public float width;
    public float height;

    public Quad2D(){

    }

    public Quad2D(Context context, String tag, int program, float x1, float y1,
                  float x2, float y2,
                  float x3, float y3,
                  float x4, float y4,
                  float red, float green, float blue, float alpha,
                  float t, float v){
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

        Log.d(tag, "Populating vertexList");
        vertexList = new float[]{x1, y1, x2, y2, x3, y3,
                                 x2, y2, x3, y3, x4, y4};

        Log.d(tag, "Populating colorList");
        colorList = new float[]{red, green, blue, alpha,
                                red, green, blue, alpha,
                                red, green, blue, alpha,
                                red, green, blue, alpha,
                                red, green, blue, alpha,
                                red, green, blue, alpha};

        Log.d(tag, "Populating textureCoordList");
        textureCoordList = new float[]{0f, 0f, t, 0f, 0f, v,
                                       t, 0f, 0f, v, t, v};

        Log.d(tag, "Creating vertexBuffer");
        vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexList);
        Log.d(tag, "Creating colorBuffer");
        colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(colorList);
        Log.d(tag, "Creating textureCoordBuffer");
        textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureCoordList);

        glUseProgram(program);

        Log.d(tag, "aPositionHandle glGetAttribLocation");
        aPositionHandle = 0;                    //glGetAttribLocation(program, A_POSITION);
        Log.d(tag, "aColorHandle glGetAttribLocation");
        aColorHandle = 1;                       //glGetAttribLocation(program, A_COLOR);
        Log.d(tag, "aTextureCoordinateHandle glGetAttribLocation");
        aTextureCoordinateHandle = 2;           //glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        Log.d(tag, "uTextureUnitHandle glGetUniformLocation");
        uTextureUnitHandle = glGetUniformLocation(program, U_TEXTURE_UNIT);
        Log.d(tag, "uRGBAHandle glGetUniformLocation");
        uRGBAHandle = glGetUniformLocation(program, U_RGBA);
        Log.d(tag, "uMatrixHandle glGetUniformLocation");
        uMatrixHandle = glGetUniformLocation(program, U_MVPMATRIX);

        // Clear the arrays when we are done with them:
        vertexList = null;
        colorList = null;
        textureCoordList = null;
    }

    public void setTexture(int texture){
        if(texture_enabled == true) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            glUniform1i(uTextureUnitHandle, 0);
        }
        else {
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