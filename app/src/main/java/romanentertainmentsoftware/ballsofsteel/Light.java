package romanentertainmentsoftware.ballsofsteel;

import android.opengl.Matrix;

import math3D.Vector3D;
import math3D.Vertex3D;

import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

/**
 * Created by Roman Entertainment Software LLC on 5/6/2018.
 */

public class Light {
    public enum TYPE {
        pointLight,
        directionalLight,
        spotLight
    }

    private int program;

    public float[] modelMatrix = new float[16];
    public float[] mvpMatrix = new float[16];
    public float[] modelViewMatrix = new float[16];

    private Color ambientColor = new Color();
    private Color diffuseColor = new Color();
    private Color specularColor = new Color();
    public Vertex3D position = new Vertex3D();
    private Vector3D directionVector = new Vector3D();

    private int uAmbientColorHandle;
    private int uDiffuseColorHandle;
    private int uSpecularColorHandle;
    private int uLightPositionHandle;
    private int uLightDirectionHandle;
    private int uAmbientEnabledHandle;
    private int uDiffuseEnabledHandle;
    private int uSpecularEnabledHandle;
    private int uSpecularIntensityHandle;
    private int uLightEnabledHandle;
    private int uLightTypeHandle;

    public Light(int program){
        this.program = program;

        glUseProgram(program);

        ambientColor.red = 1f;
        ambientColor.green = 1f;
        ambientColor.blue = 1f;
        ambientColor.alpha = 1f;

        uAmbientColorHandle = glGetUniformLocation(program, U_AMBIENT_COLOR);
        uDiffuseColorHandle = glGetUniformLocation(program, U_DIFFUSE_COLOR);
        uSpecularColorHandle = glGetUniformLocation(program, U_SPECULAR_COLOR);
        uLightPositionHandle = glGetUniformLocation(program, U_LIGHT_POSITION);
        uLightDirectionHandle = glGetUniformLocation(program, U_LIGHT_DIRECTION);
        uAmbientEnabledHandle = glGetUniformLocation(program, U_AMBIENT_ENABLED);
        uDiffuseEnabledHandle = glGetUniformLocation(program, U_DIFFUSE_ENABLED);
        uSpecularEnabledHandle = glGetUniformLocation(program, U_SPECULAR_ENABLED);
        uSpecularIntensityHandle = glGetUniformLocation(program, U_SPECULAR_INTENSITY);
        uLightEnabledHandle = glGetUniformLocation(program, U_LIGHT_ENABLED);
        uLightTypeHandle = glGetUniformLocation(program, U_LIGHT_TYPE);
    }

    public void updateMatrices(Camera3D camera){
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(mvpMatrix, 0);

        Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);

        Matrix.multiplyMM(modelViewMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, camera.projectionMatrix, 0, modelViewMatrix, 0);
    }

    public void ambient(float red, float green, float blue){
        ambientColor.red = red;
        ambientColor.green = green;
        ambientColor.blue = blue;
        ambientColor.alpha = 1f;

        glUniform4f(uAmbientColorHandle, red, green, blue, 1f);
    }

    public void diffuse(float red, float green, float blue){
        diffuseColor.red = red;
        diffuseColor.green = green;
        diffuseColor.blue = blue;
        diffuseColor.alpha = 1f;

        glUniform4f(uDiffuseColorHandle, red, green, blue, 1f);
    }

    public void specular(float red, float green, float blue){
        specularColor.red = red;
        specularColor.green = green;
        specularColor.blue = blue;
        specularColor.alpha = 1f;

        glUniform4f(uSpecularColorHandle, red, green, blue, 1f);
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;

        glUseProgram(program);

        glUniform3f(uLightPositionHandle, position.x, position.y, position.z);
    }


    public void direction(float x, float y, float z){
        directionVector.x = x;
        directionVector.y = y;
        directionVector.z = z;

        glUniform3f(uLightDirectionHandle, directionVector.x, directionVector.y, directionVector.z);
    }

    public void ambientEnable(boolean value){
        int ambientEnabled;

        if (value)
            ambientEnabled = 1;
        else
            ambientEnabled = 0;

        glUniform1i(uAmbientEnabledHandle, ambientEnabled);
    }

    public void diffuseEnable(boolean value){
        int diffuseEnabled;

        if (value)
            diffuseEnabled = 1;
        else
            diffuseEnabled = 0;

        glUniform1i(uDiffuseEnabledHandle, diffuseEnabled);
    }

    public void specularEnable(boolean value){
        int specularEnabled;

        if (value)
            specularEnabled = 1;
        else
            specularEnabled = 0;

        glUniform1i(uSpecularEnabledHandle, specularEnabled);
    }

    public void specularIntensity(float value){
        glUniform1f(uSpecularIntensityHandle, value);
    }

    public void enable(boolean value){
        int lightEnabled;

        if (value)
            lightEnabled = 1;
        else
            lightEnabled = 0;

        glUniform1i(uLightEnabledHandle, lightEnabled);
    }

    public void setLightType(TYPE lightType) {
        int result = 0;

        if (lightType == TYPE.pointLight)
            result = 0;
        else if (lightType == TYPE.directionalLight)
            result = 1;
        else if (lightType == TYPE.spotLight)
            result = 2;
        glUniform1i(uLightTypeHandle, result);
    }

    public void release(){
        if (ambientColor != null){
            ambientColor = null;
        }

        if (diffuseColor != null){
            diffuseColor = null;
        }

        if (specularColor != null){
            specularColor = null;
        }

        if (directionVector != null){
            directionVector = null;
        }
    }
}


