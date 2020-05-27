package object3D;

/**
 * Created by Roman Entertainment Software LLC on 5/17/2018.
 */

import android.content.Context;
import android.content.res.Resources;
import android.opengl.Matrix;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import math3D.Vector3D;
import math3D.Vertex3D;
import romanentertainmentsoftware.ballsofsteel.Camera3D;
import romanentertainmentsoftware.ballsofsteel.TextureCoord2D;
import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

public class WireframeObject3D {
    private final String TAG = "WIREFRAMEOBJECT3D";

    Context context;
    int resourceID;

    public ArrayList<Vertex3D> vertexArrayList;
    public ArrayList<TextureCoord2D> textureCoordArrayList;
    public ArrayList<Vector3D> normalArrayList;
    public ArrayList<Face3D> faceArrayList;

    private float red, green, blue, alpha;

    private float[] vertexList;
    private short[] indexList; // You can only use shorts for indices;
    private float[] colorList;
    private float[] textureCoordList;
    private float[] normalList;

    public float[] modelMatrix = new float[16];
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    public int numberOfVertices;

    private int[] vertexBufferObject = new int[1];
    private int[] indexBufferObject = new int[1];

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer; // You can only use shorts for index buffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureCoordBuffer;
    private FloatBuffer normalBuffer;

    private int uRGBAHandle;
    private int uMVPMatrixHandle;
    private int uMVMatrixHandle;
    private int aPositionHandle;
    private int aColorHandle;

    private int program;

    public boolean vertexBufferEnabled;

    private boolean reverseNormals;

    public Vertex3D position;
    public Vector3D angle;


    public WireframeObject3D(){

    }

    public WireframeObject3D(Context context, int program, int resourceID, Vertex3D position, Vector3D angle, float red, float green, float blue, float alpha){
        this.context = context;
        this.program = program;
        this.resourceID = resourceID;
        this.position = position;
        this.angle = angle;

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
    }

    public int loadFile()
    {
        ArrayList<Vertex3D> tempVertexArrayList = new ArrayList<Vertex3D>();
        ArrayList<TextureCoord2D> tempTextureCoordArrayList = new ArrayList<TextureCoord2D>();
        ArrayList<Vector3D> tempNormalArrayList = new ArrayList<Vector3D>();
        ArrayList<Face3D> tempFaceArrayList = new ArrayList<Face3D>();
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceID);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            String subString;
            String[] stringArray;
            String[] stringArray2;
            int[] indexNumberList = new int[3];
            int[] textureCoordNumberList = new int[3];
            int[] normalNumberList = new int[3];
            int i = 0;
            int j = 0;
            int k = 0;
            try {
                while ((nextLine = bufferedReader.readLine()) != null) {
                    if (nextLine.startsWith("v ")) {
                        subString = nextLine.substring(1).trim();
                        stringArray = subString.split(" ");
                        try {

                            tempVertexArrayList.add(new Vertex3D(Float.parseFloat(stringArray[0]),
                                    Float.parseFloat(stringArray[1]),
                                    Float.parseFloat(stringArray[2]), 1f));
                        }
                        catch(NumberFormatException e){
                            Log.d(TAG, "Error: Invalid number format in loading vertex list");
                            return 0;
                        }
                        String x = String.valueOf(tempVertexArrayList.get(i).x);
                        String y = String.valueOf(tempVertexArrayList.get(i).y);
                        String z = String.valueOf(tempVertexArrayList.get(i).z);
                        //Log.d(TAG, "vertex " + String.valueOf(i) + ": " + x + ", " + y + ", " + z);
                        i++;
                    }

                    if (nextLine.startsWith("vn ")) {
                        subString = nextLine.substring(2).trim();
                        stringArray = subString.split(" ");
                        try {
                            if(reverseNormals){
                                tempNormalArrayList.add(new Vector3D(-Float.parseFloat(stringArray[0]),
                                        -Float.parseFloat(stringArray[1]),
                                        -Float.parseFloat(stringArray[2])));
                            }
                            else{
                                tempNormalArrayList.add(new Vector3D(Float.parseFloat(stringArray[0]),
                                        Float.parseFloat(stringArray[1]),
                                        Float.parseFloat(stringArray[2])));
                            }

                        }
                        catch(NumberFormatException e){
                            Log.d(TAG, "Error: Invalid number format in loading normal list");
                            return 0;
                        }
                        String nx = String.valueOf(tempNormalArrayList.get(j).x);
                        String ny = String.valueOf(tempNormalArrayList.get(j).y);
                        String nz = String.valueOf(tempNormalArrayList.get(j).z);
                        //Log.d(TAG, "normal " + String.valueOf(j) + ": " + nx + ", " + ny + ", " + nz);
                        j++;
                    }

                    if (nextLine.startsWith("vt ")) {
                        subString = nextLine.substring(2).trim();
                        stringArray = subString.split(" ");
                        try {
                            tempTextureCoordArrayList.add(new TextureCoord2D(Float.parseFloat(stringArray[0]),
                                    Float.parseFloat(stringArray[1])));
                        }
                        catch(NumberFormatException e){
                            Log.d(TAG, "Error: Invalid number format in loading texture coordinate list");
                            return 0;
                        }
                        String tu = String.valueOf(tempTextureCoordArrayList.get(k).tu);
                        String tv = String.valueOf(tempTextureCoordArrayList.get(k).tv);
                        //Log.d(TAG, "texture coord " + String.valueOf(k) + ": " + tu + ", " + tv);
                        k++;
                    }

                    if (nextLine.startsWith("f ")) {
                        subString = nextLine.substring(1).trim();
                        stringArray = subString.split(" ");
                        for (int index = 0; index <= 2; index++) {
                            stringArray2 = stringArray[index].split("/");
                            try {
                                indexNumberList[index] = Integer.parseInt(stringArray2[0]) - 1;
                                if(indexNumberList[index] < 0){
                                    Log.d(TAG, "Error: indexNumberList[] is less than zero");
                                    return 0;
                                }
                            }
                            catch(NumberFormatException e){
                                Log.d(TAG, "Error: Invalid number format in loading indexNumberList[]");
                                return 0;
                            }

                            try{
                                textureCoordNumberList[index] = Integer.parseInt(stringArray2[1]) - 1;
                                if(textureCoordNumberList[index] < 0){
                                    Log.d(TAG, "Error: textureCoordNumberList[] is less than zero");
                                    return 0;
                                }
                            }
                            catch(NumberFormatException e){
                                Log.d(TAG, "Error: Invalid number format in loading textureCoordNumberList[]");
                                return 0;
                            }

                            try{
                                normalNumberList[index] = Integer.parseInt(stringArray2[2]) - 1;
                                if(normalNumberList[index] < 0){
                                    Log.d(TAG, "Error: normalNumberList[] is less than zero");
                                    return 0;
                                }
                            }
                            catch(NumberFormatException e){
                                Log.d(TAG, "Error: Invalid number format in loading normalNumberList[]");
                                return 0;
                            }
                        }
                        tempFaceArrayList.add(new Face3D(indexNumberList[0], textureCoordNumberList[0], normalNumberList[0],
                                indexNumberList[1], textureCoordNumberList[1], normalNumberList[1],
                                indexNumberList[2], textureCoordNumberList[2], normalNumberList[2]));
                    }

                    body.append(nextLine);
                    body.append('\n');
                }

                //Now that everything has successfully loaded, you can now populate the public variables.
                if(tempVertexArrayList != null && tempVertexArrayList.size() != 0){
                    // Since we now know the size, we can initialize the vertexArrayList with a proper size
                    // to avoid garbage collections:
                    // http://www.markodevcic.com/Post/Keeping_your_Android_app_running_smooth
                    vertexArrayList = new ArrayList<>(tempVertexArrayList.size());
                    // Copy all the values from the tempVertexArrayList to the vertexArrayList.
                    vertexArrayList.addAll(tempVertexArrayList);
                    // Remove the values from the tempVertexArrayList to avoid garbage collections from the heap.
                    tempVertexArrayList.clear();
                }

                if(tempTextureCoordArrayList != null && tempTextureCoordArrayList.size() != 0){
                    // Since we now know the size, we can initialize the textureCoordArrayList with a proper size
                    // to avoid garbage collections:
                    // http://www.markodevcic.com/Post/Keeping_your_Android_app_running_smooth
                    textureCoordArrayList = new ArrayList<>(tempTextureCoordArrayList.size());
                    // Copy all the values from the tempTextureCoordArrayList to the textureCoordArrayList.
                    textureCoordArrayList.addAll(tempTextureCoordArrayList);
                    // Remove the values from the tempTextureCoordArrayList to avoid garbage collections from the heap.
                    tempTextureCoordArrayList.clear();
                }

                if(tempNormalArrayList != null && tempNormalArrayList.size() != 0){
                    // Since we now know the size, we can initialize the normalArrayList with a proper size
                    // to avoid garbage collections:
                    // http://www.markodevcic.com/Post/Keeping_your_Android_app_running_smooth
                    normalArrayList = new ArrayList<>(tempNormalArrayList.size());
                    // Copy all the values from the tempNormalArrayList to the normalArrayList.
                    normalArrayList.addAll(tempNormalArrayList);
                    // Remove the values from the tempNormalArrayList to avoid garbage collections from the heap.
                    tempNormalArrayList.clear();

                }

                if(tempFaceArrayList != null && tempFaceArrayList.size() != 0){
                    // Since we now know the size, we can initialize the faceArrayList with a proper size
                    // to avoid garbage collections:
                    // http://www.markodevcic.com/Post/Keeping_your_Android_app_running_smooth
                    faceArrayList = new ArrayList<>(tempFaceArrayList.size());
                    // Copy all the values from the tempFaceArrayList to the normalArrayList.
                    faceArrayList.addAll(tempFaceArrayList);
                    // Remove the values from the tempFaceArrayList to avoid garbage collections from the heap.
                    tempFaceArrayList.clear();
                }

                // Might not be needed, but it's nice to nullify the temporary arraylists
                tempVertexArrayList = null;
                tempTextureCoordArrayList = null;
                tempNormalArrayList = null;
                tempFaceArrayList = null;

                Log.d(TAG, "Creating vertexList");
                vertexList = new float[faceArrayList.size() * POSITION_COMPONENT_COUNT_3D * NUMBER_OF_SIDES_PER_FACE];
                Log.d(TAG, "Creating indexList");
                indexList = new short[faceArrayList.size() * NUMBER_OF_SIDES_PER_FACE];
                Log.d(TAG, "Creating colorList");
                colorList = new float[faceArrayList.size() * COLOR_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE];
                Log.d(TAG, "Creating textureCoordList");
                textureCoordList = new float[faceArrayList.size() * TEXTURE_COORDINATES_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE];
                Log.d(TAG, "Creating normalList");
                normalList = new float[faceArrayList.size() * NORMAL_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE];

                int nextFace = 0;
                int step = POSITION_COMPONENT_COUNT_3D * NUMBER_OF_SIDES_PER_FACE;

                Log.d(TAG, "Populating vertexList");
                for (int currentVertex = 0; currentVertex < vertexList.length; currentVertex += step){
                    vertexList[currentVertex + 0] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(0)).x;
                    vertexList[currentVertex + 1] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(0)).y;
                    vertexList[currentVertex + 2] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(0)).z;
                    vertexList[currentVertex + 3] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(0)).w;

                    vertexList[currentVertex + 4] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(1)).x;
                    vertexList[currentVertex + 5] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(1)).y;
                    vertexList[currentVertex + 6] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(1)).z;
                    vertexList[currentVertex + 7] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(1)).w;

                    vertexList[currentVertex + 8] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(2)).x;
                    vertexList[currentVertex + 9] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(2)).y;
                    vertexList[currentVertex + 10] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(2)).z;
                    vertexList[currentVertex + 11] = vertexArrayList.get(faceArrayList.get(nextFace).indexNumberList.get(2)).w;

                    nextFace++;
                }

                numberOfVertices = vertexList.length / POSITION_COMPONENT_COUNT_3D;

                nextFace = 0;

                Log.d(TAG, "Populating indexList");
                for (int currentIndex = 0; currentIndex < indexList.length; currentIndex += NUMBER_OF_SIDES_PER_FACE){
                    indexList[currentIndex + 0] = faceArrayList.get(nextFace).indexNumberList.get(0).shortValue();
                    indexList[currentIndex + 1] = faceArrayList.get(nextFace).indexNumberList.get(1).shortValue();
                    indexList[currentIndex + 2] = faceArrayList.get(nextFace).indexNumberList.get(2).shortValue();
                }

                step = COLOR_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE;

                Log.d(TAG, "Populating colorList");
                for (int currentVertex = 0; currentVertex < colorList.length; currentVertex += step){
                    colorList[currentVertex + 0] = red;
                    colorList[currentVertex + 1] = green;
                    colorList[currentVertex + 2] = blue;
                    colorList[currentVertex + 3] = alpha;

                    colorList[currentVertex + 4] = red;
                    colorList[currentVertex + 5] = green;
                    colorList[currentVertex + 6] = blue;
                    colorList[currentVertex + 7] = alpha;

                    colorList[currentVertex + 8] = red;
                    colorList[currentVertex + 9] = green;
                    colorList[currentVertex + 10] = blue;
                    colorList[currentVertex + 11] = alpha;
                }

                nextFace = 0;
                step = TEXTURE_COORDINATES_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE;

                Log.d(TAG, "Populating textureCoordList");
                for (int currentVertex = 0; currentVertex < textureCoordList.length; currentVertex += step){
                    textureCoordList[currentVertex + 0] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(0)).tu;
                    textureCoordList[currentVertex + 1] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(0)).tv;

                    textureCoordList[currentVertex + 2] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(1)).tu;
                    textureCoordList[currentVertex + 3] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(1)).tv;

                    textureCoordList[currentVertex + 4] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(2)).tu;
                    textureCoordList[currentVertex + 5] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(2)).tv;

                    nextFace++;
                }

                nextFace = 0;
                step = NORMAL_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE;

                Log.d(TAG, "Populating normalList");
                for (int currentVertex = 0; currentVertex < normalList.length; currentVertex += step){
                    normalList[currentVertex + 0] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(0)).x;
                    normalList[currentVertex + 1] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(0)).y;
                    normalList[currentVertex + 2] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(0)).z;

                    normalList[currentVertex + 3] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(1)).x;
                    normalList[currentVertex + 4] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(1)).y;
                    normalList[currentVertex + 5] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(1)).z;

                    normalList[currentVertex + 6] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(2)).x;
                    normalList[currentVertex + 7] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(2)).y;
                    normalList[currentVertex + 8] = normalArrayList.get(faceArrayList.get(nextFace).normalNumberList.get(2)).z;

                    nextFace++;
                }

                Log.d(TAG, "Creating vertexBuffer");
                vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
                Log.d(TAG, "Creating indexBuffer");
                indexBuffer = ByteBuffer.allocateDirect(indexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asShortBuffer();
                Log.d(TAG, "Creating colorBuffer");
                colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
                Log.d(TAG, "Creating textureCoordBuffer");
                textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
                Log.d(TAG, "Creating normalBuffer");
                normalBuffer = ByteBuffer.allocateDirect(normalList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

                vertexBuffer.put(vertexList).position(0);
                indexBuffer.put(indexList).position(0);
                colorBuffer.put(colorList).position(0);
                textureCoordBuffer.put(textureCoordList).position(0);
                normalBuffer.put(normalList).position(0);

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

                Log.d(TAG, "Creating VertexBuffer");
                createVertexBuffer();

                glUniform4f(uRGBAHandle, red, green, blue, alpha);

                // Clear the large arrays when we are done with them. We only need the data from the array lists:
                vertexList = null;
                indexList = null;
                colorList = null;
                textureCoordList = null;
                normalList = null;

            }
            catch(IOException e){

            }
        }
        catch (Resources.NotFoundException nfe){
            throw new RuntimeException("Resource not found: " + resourceID, nfe);
        }

        return 1;
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

    public void updateMatrices(Camera3D camera, float angle, float angleX, float angleY, float angleZ, float scaleX, float scaleY, float scaleZ) {
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, position.x, position.y, position.z);

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
        if (faceArrayList != null){
            glEnable(GL_DEPTH_TEST);

            if(vertexBufferEnabled){
                glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
                enableVertexAttribArrays();
                bindData();
                glLineWidth(lineThickness);
                glDrawArrays(GL_LINES, 0, faceArrayList.size() * NUMBER_OF_SIDES_PER_FACE);
                glLineWidth(1f);
                disableVertexAttribArrays();
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            else{
                enableVertexAttribArrays();
                bindData();
                glLineWidth(lineThickness);
                glDrawArrays(GL_LINES, 0, faceArrayList.size() * NUMBER_OF_SIDES_PER_FACE);
                glLineWidth(1f);
                disableVertexAttribArrays();
            }
        }
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
        if (vertexBufferEnabled){
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
            int offset = 0;
            glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_3D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_3D, offset);
            offset += POSITION_COMPONENT_COUNT_3D;
            glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, numberOfVertices * offset * BYTES_PER_FLOAT);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        else{
            vertexBuffer.position(0);
            glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_3D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_3D, vertexBuffer);

            colorBuffer.position(0);
            glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, colorBuffer);
        }
    }

    public void createVertexBuffer(){
        glGenBuffers(1, vertexBufferObject, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);

        int vertexLine = POSITION_COMPONENT_COUNT_3D + COLOR_COMPONENT_COUNT;
        glBufferData(GL_ARRAY_BUFFER,numberOfVertices * vertexLine * BYTES_PER_FLOAT, null, GL_STATIC_DRAW);
        int offset = 0;
        glBufferSubData(GL_ARRAY_BUFFER, offset,
                numberOfVertices * POSITION_COMPONENT_COUNT_3D * BYTES_PER_FLOAT, vertexBuffer);
        offset += POSITION_COMPONENT_COUNT_3D;
        glBufferSubData(GL_ARRAY_BUFFER, numberOfVertices * offset * BYTES_PER_FLOAT,
                numberOfVertices * COLOR_COMPONENT_COUNT * BYTES_PER_FLOAT, colorBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void close(){
        disableVertexAttribArrays();

        if (vertexArrayList != null){
            vertexArrayList.clear();
            vertexArrayList = null;
        }

        if (textureCoordArrayList != null){
            textureCoordArrayList.clear();
            textureCoordArrayList = null;
        }

        if (normalArrayList != null) {
            normalArrayList.clear();
            normalArrayList = null;
        }

        if (faceArrayList != null){
            faceArrayList.clear();
            faceArrayList = null;
        }

        vertexList = null;
        indexList = null;
        colorList = null;
        textureCoordList = null;
        normalList = null;
        modelMatrix = null;
        mvMatrix = null;
        mvpMatrix = null;
        vertexBufferObject = null;
        indexBufferObject = null;

        if (vertexBuffer != null){
            vertexBuffer.clear();
            vertexBuffer = null;
        }

        if (indexBuffer != null) {
            indexBuffer.clear();
            indexBuffer = null;
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

        if (position != null)
            position = null;

        if (angle != null)
            angle = null;
    }
}
