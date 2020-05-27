package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;
import android.content.res.Resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static android.opengl.GLES30.*;

/**
 * Created by Roman Entertainment Software LLC on 5/4/2018.
 */

public class Shader {

    public static String readTextFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        }
        catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }

        return body.toString();
    }
    private static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    private static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            return 0;
        }

        glShaderSource(shaderObjectId, shaderCode);
        glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }

    private static int linkProgram(int vertexShaderId, int[] fragmentShaderId, int numberOfFragmentShaders) {
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);

        for (int i = 0; i < numberOfFragmentShaders; i++){
            glAttachShader(programObjectId, fragmentShaderId[i]);
        }


        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            return 0;
        }

        glDeleteShader(vertexShaderId);

        for (int i = 0; i < numberOfFragmentShaders; i++){
            glDeleteShader(fragmentShaderId[i]);
        }


        return programObjectId;
    }

    public static int buildProgram(String vertexShaderSource, String[] fragmentShaderSource, int numberOfFragmentShaders){
        int vertexShader = compileVertexShader(vertexShaderSource);
        int[] fragmentShader = new int[numberOfFragmentShaders];

        for (int i = 0; i < numberOfFragmentShaders; i++){
            fragmentShader[i] = compileFragmentShader(fragmentShaderSource[i]);
        }
        int program = linkProgram(vertexShader, fragmentShader, numberOfFragmentShaders);
        return program;
    }

}
