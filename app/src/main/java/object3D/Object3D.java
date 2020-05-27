package object3D;

/**
 * Created by Roman Entertainment Software LLC on 5/3/2018.
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
import java.util.Arrays;

import math3D.Vector3D;
import math3D.Vertex3D;
import romanentertainmentsoftware.ballsofsteel.Camera3D;
import romanentertainmentsoftware.ballsofsteel.MainActivity;
import romanentertainmentsoftware.ballsofsteel.Render;
import romanentertainmentsoftware.ballsofsteel.TextureCoord2D;

import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES30.*;
import static romanentertainmentsoftware.ballsofsteel.Constants.*;

//TODO: Congratulations on loading the data! But you are FAR from complete O.O
//      Since you are an extremly forgetful dumbass /SMACK >.<, You now have to
//      do your own polygon code here WITHOUT using QUAD3D or creating POLY3D.
//      Reason being, is because the Object will need is own Vertex Buffer and
//      now an Index Buffer since you are using OBJ files with indices. This will take
//      some time to code, but the wait will be worth it. Once this works, you can then
//      draw the object.
//
//TODO: You will then need to figure out how to apply multiple textures
//      to the object, rather than one measly texture. An example of this will be the ship.

//TODO: Animation will be nice as well but I don't consider this a priority yet.

//TODO: find out about "vp" in object files

//TODO: find out about "s" in object files

//TODO: find out about "b" in object files

//TODO: find out about "o" in object files

//TODO: figure out how to load and use materials!!!

//DONE: Make a light shader! Let there be fucking light damn it!

//TODO: find out if you are loading a triangle, quad, or a line with some IF statements!


public class Object3D {
    //Constants
    private final int cubeMapSize = MainActivity.height;

    private String tag;
    Context context;
    boolean valid;
    int resourceID;

    // Object Arraylists
    public ArrayList<Vertex3D> vertexArrayList;
    public ArrayList<TextureCoord2D> textureCoordArrayList;
    public ArrayList<Vector3D> normalArrayList;
    public ArrayList<Face3D> faceArrayList;

    // Object Color
    private float red, green, blue, alpha;

    // Object Lists
    private float[] vertexList;
    private short[] indexList; // You can only use shorts for indices;
    private float[] colorList;
    private float[] textureCoordList;
    private float[] normalList;

    // Local Matrices
    public float[] localMatrix = new float[16];
    public float[] localPositionMatrix = new float[16];
    public float[] localRotateMatrix = new float[16];
    public float[] localRotateMatrixX = new float[16];
    public float[] localRotateMatrixY = new float[16];
    public float[] localRotateMatrixZ = new float[16];

    // World Matrices
    public float[] worldMatrix = new float[16];
    public float[] worldPositionMatrix = new float[16];
    public float[] worldRotateMatrix = new float[16];
    public float[] worldRotateMatrixX = new float[16];
    public float[] worldRotateMatrixY = new float[16];
    public float[] worldRotateMatrixZ = new float[16];

    // Transformed Matrices
    public float[] modelMatrix = new float[16];
    public float[] unScaledModelViewMatrix = new float[16];
    public float[] scaledModelViewMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    //Buffer Arrays
    private int[] vertexArrayObject = new int[1];
    private int[] vertexBufferObject = new int[1];
    private int[] indexBufferObject = new int[1]; //TODO: Indices

    //Buffers
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer; // You can only use shorts for index buffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer textureCoordBuffer;
    private FloatBuffer normalBuffer;

    // U-Handles
    private int uRGBAHandle;
    private int uMVPMatrixHandle;
    private int uMVMatrixHandle;
    private int uMMatrixHandle;
    private int uVMatrixHandle;
    private int uTextureUnitHandle;
    private int uTextureUnit2Handle;
    private int uCubeMapUnitHandle;
    private int aPositionHandle;
    private int aColorHandle;
    private int aTextureCoordinateHandle;
    private int aNormalHandle;
    private int uTextureEnabledHandle;
    private int uTextureEnabled2Handle;
    private int uCubeMapEnabledHandle;
    private int uReverseReflectionHandle;
    private int uObjectPositionHandle;
    private int uCameraPositionHandle;
    private int uCameraAngleHandle;
    private int uReflectiveCubeEnabledHandle;
    private int uReflectivenessHandle;

    private int program;
    public int numberOfVertices;
    private int numberOfFaces;

    public boolean texture_enabled = true;
    public boolean cubemap_enabled = false;
    public boolean texture2_enabled = false;
    public boolean reverseReflection = false;
    public boolean reflectiveCubeEnabled = false;

    private float numberOfRepeatedTextures;
    private boolean reverseNormals;
    public boolean vertexBufferEnabled;

    public Vertex3D local_position;
    public Vector3D local_angle;
    public Vertex3D world_position;
    public Vertex3D old_world_position;
    public Vector3D world_angle;
    public Vector3D scalar;
    public Vector3D velocity;
    public Vertex3D transformed_position;

    int[] frameBuffer = new int[1];
    int[] depthRenderBuffer = new int[1];

    public int[] dynamic_cubemap_texture = new int[1];

    public Object3D(){

    }

    public Object3D(Context context, String tag, int program, int resourceID, Vertex3D local_position, Vector3D local_angle, Vertex3D world_position, Vector3D world_angle, Vector3D scalar, float red, float green, float blue, float alpha, float numberOfRepeatedTextures, boolean reverseNormals){
        this.context = context;
        this.tag = tag;
        this.program = program;
        this.resourceID = resourceID;
        this.local_position = local_position;
        this.local_angle = local_angle;
        this.world_position = world_position;
        this.world_angle = world_angle;
        this.scalar = scalar;

        this.old_world_position = new Vertex3D();
        this.velocity = new Vector3D();
        this.transformed_position = new Vertex3D();

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

        this.numberOfRepeatedTextures = numberOfRepeatedTextures;
        this.reverseNormals = reverseNormals;
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
                            Log.d(tag, "Error: Invalid number format in loading vertex list");
                            return 0;
                        }
                        String x = String.valueOf(tempVertexArrayList.get(i).x);
                        String y = String.valueOf(tempVertexArrayList.get(i).y);
                        String z = String.valueOf(tempVertexArrayList.get(i).z);
                        //Log.d(tag, "vertex " + String.valueOf(i) + ": " + x + ", " + y + ", " + z);
                        i++;
                    }

                    if (nextLine.startsWith("vn ")) {
                        subString = nextLine.substring(2).trim();
                        stringArray = subString.split(" ");
                        try {
                            if(reverseNormals){
                                tempNormalArrayList.add(new Vector3D(Float.parseFloat(stringArray[0]),
                                        Float.parseFloat(stringArray[1]),
                                        -Float.parseFloat(stringArray[2])));
                            }
                            else{
                                tempNormalArrayList.add(new Vector3D(Float.parseFloat(stringArray[0]),
                                        Float.parseFloat(stringArray[1]),
                                        Float.parseFloat(stringArray[2])));
                            }

                        }
                        catch(NumberFormatException e){
                            Log.d(tag, "Error: Invalid number format in loading normal list");
                            return 0;
                        }
                        String nx = String.valueOf(tempNormalArrayList.get(j).x);
                        String ny = String.valueOf(tempNormalArrayList.get(j).y);
                        String nz = String.valueOf(tempNormalArrayList.get(j).z);
                        //Log.d(tag, "normal " + String.valueOf(j) + ": " + nx + ", " + ny + ", " + nz);
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
                            Log.d(tag, "Error: Invalid number format in loading texture coordinate list");
                            return 0;
                        }
                        String tu = String.valueOf(tempTextureCoordArrayList.get(k).tu);
                        String tv = String.valueOf(tempTextureCoordArrayList.get(k).tv);
                        //Log.d(tag, "texture coord " + String.valueOf(k) + ": " + tu + ", " + tv);
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
                                    Log.d(tag, "Error: indexNumberList[] is less than zero");
                                    return 0;
                                }
                            }
                            catch(NumberFormatException e){
                                Log.d(tag, "Error: Invalid number format in loading indexNumberList[]");
                                return 0;
                            }

                            try{
                                textureCoordNumberList[index] = Integer.parseInt(stringArray2[1]) - 1;
                                if(textureCoordNumberList[index] < 0){
                                    Log.d(tag, "Error: textureCoordNumberList[] is less than zero");
                                    return 0;
                                }
                            }
                            catch(NumberFormatException e){
                                Log.d(tag, "Error: Invalid number format in loading textureCoordNumberList[]");
                                return 0;
                            }

                            try{
                                normalNumberList[index] = Integer.parseInt(stringArray2[2]) - 1;
                                if(normalNumberList[index] < 0){
                                    Log.d(tag, "Error: normalNumberList[] is less than zero");
                                    return 0;
                                }
                            }
                            catch(NumberFormatException e){
                                Log.d(tag, "Error: Invalid number format in loading normalNumberList[]");
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

                    tempVertexArrayList = null;
                }
                else {
                    valid = false;
                    return 0;
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

                    tempTextureCoordArrayList = null;
                }
                else {
                    valid = false;
                    return 0;
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

                    tempNormalArrayList = null;
                }
                else {
                    valid = false;
                    return 0;
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

                    tempFaceArrayList = null;
                }
                else {
                    valid = false;
                    return 0;
                }

                Log.d(tag, "Creating vertexList...");
                vertexList = new float[faceArrayList.size() * POSITION_COMPONENT_COUNT_3D * NUMBER_OF_SIDES_PER_FACE];
                Log.d(tag, "Creating indexList...");
                indexList = new short[faceArrayList.size() * NUMBER_OF_SIDES_PER_FACE];
                Log.d(tag, "Creating colorList...");
                colorList = new float[faceArrayList.size() * COLOR_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE];
                Log.d(tag, "Creating textureCoordList...");
                textureCoordList = new float[faceArrayList.size() * TEXTURE_COORDINATES_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE];
                Log.d(tag, "Creating normalList...");
                normalList = new float[faceArrayList.size() * NORMAL_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE];

                int nextFace = 0;
                int step = POSITION_COMPONENT_COUNT_3D * NUMBER_OF_SIDES_PER_FACE;

                Log.d(tag, "Populating vertexList...");
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

                if (vertexArrayList != null){
                    vertexArrayList.clear();
                    vertexArrayList = null;
                }

                numberOfVertices = vertexList.length / POSITION_COMPONENT_COUNT_3D;

                nextFace = 0;

                Log.d(tag, "Populating indexList...");
                for (int currentIndex = 0; currentIndex < indexList.length; currentIndex += NUMBER_OF_SIDES_PER_FACE){
                    indexList[currentIndex + 0] = faceArrayList.get(nextFace).indexNumberList.get(0).shortValue();
                    indexList[currentIndex + 1] = faceArrayList.get(nextFace).indexNumberList.get(1).shortValue();
                    indexList[currentIndex + 2] = faceArrayList.get(nextFace).indexNumberList.get(2).shortValue();

                    nextFace++;
                }

                step = COLOR_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE;

                Log.d(tag, "Populating colorList...");
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

                Log.d(tag, "Populating textureCoordList...");
                for (int currentVertex = 0; currentVertex < textureCoordList.length; currentVertex += step){
                    textureCoordList[currentVertex + 0] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(0)).tu * numberOfRepeatedTextures;
                    textureCoordList[currentVertex + 1] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(0)).tv * numberOfRepeatedTextures;

                    textureCoordList[currentVertex + 2] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(1)).tu * numberOfRepeatedTextures;
                    textureCoordList[currentVertex + 3] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(1)).tv * numberOfRepeatedTextures;

                    textureCoordList[currentVertex + 4] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(2)).tu * numberOfRepeatedTextures;
                    textureCoordList[currentVertex + 5] = textureCoordArrayList.get(faceArrayList.get(nextFace).textureCoordNumberList.get(2)).tv * numberOfRepeatedTextures;

                    nextFace++;
                }

                if (textureCoordArrayList != null){
                    textureCoordArrayList.clear();
                    textureCoordArrayList = null;
                }

                nextFace = 0;
                step = NORMAL_COMPONENT_COUNT * NUMBER_OF_SIDES_PER_FACE;

                Log.d(tag, "Populating normalList...");
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

                if (normalArrayList != null) {
                    normalArrayList.clear();
                    normalArrayList = null;
                }

                numberOfFaces = faceArrayList.size();


                if (faceArrayList != null){
                    faceArrayList.clear();
                    faceArrayList = null;
                }
            }
            catch(IOException e){

            }
        }
        catch (Resources.NotFoundException nfe){
            throw new RuntimeException("Resource not found: " + resourceID, nfe);
        }

        valid = true;
        return 1;
    }

    public void setup(){
        if (valid == true) {
            Log.d(tag, "Creating vertexBuffer...");
            vertexBuffer = ByteBuffer.allocateDirect(vertexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
            Log.d(tag, "Creating indexBuffer...");
            indexBuffer = ByteBuffer.allocateDirect(indexList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asShortBuffer();
            Log.d(tag, "Creating colorBuffer...");
            colorBuffer = ByteBuffer.allocateDirect(colorList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
            Log.d(tag, "Creating textureCoordBuffer...");
            textureCoordBuffer = ByteBuffer.allocateDirect(textureCoordList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
            Log.d(tag, "Creating normalBuffer...");
            normalBuffer = ByteBuffer.allocateDirect(normalList.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

            vertexBuffer.put(vertexList).position(0);
            indexBuffer.put(indexList).position(0);
            colorBuffer.put(colorList).position(0);
            textureCoordBuffer.put(textureCoordList).position(0);
            normalBuffer.put(normalList).position(0);

            glUseProgram(program);

            Log.d(tag, "aPositionHandle glGetAttribLocation");
            aPositionHandle = 0; //glGetAttribLocation(program, A_POSITION);
            Log.d(tag, "aColorHandle glGetAttribLocation");
            aColorHandle = 1; //glGetAttribLocation(program, A_COLOR);
            Log.d(tag, "aTextureCoordinateHandle glGetAttribLocation");
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
            Log.d(tag, "uVMatrixHandle glGetUniformLocation");
            uVMatrixHandle = glGetUniformLocation(program, U_VMATRIX);

            Log.d(tag, "uTextureUnitHandle glGetUniformLocation");
            uTextureUnitHandle = glGetUniformLocation(program, U_TEXTURE_UNIT);
            Log.d(tag, "uTextureUnitHandle glGetUniformLocation");
            uTextureUnit2Handle = glGetUniformLocation(program, U_TEXTURE_UNIT2);
            Log.d(tag, "uTextureEnabledHandle glGetUniformLocation");
            uTextureEnabledHandle = glGetUniformLocation(program, U_TEXTURE_ENABLED);
            Log.d(tag, "uTextureEnabledHandle glGetUniformLocation");
            uTextureEnabled2Handle = glGetUniformLocation(program, U_TEXTURE_ENABLED2);
            Log.d(tag, "uCubeMapUnitHandle glGetUniformLocation");
            uCubeMapUnitHandle = glGetUniformLocation(program, U_CUBEMAP_UNIT);
            Log.d(tag, "uCubeMapEnabledHandle glGetUniformLocation");
            uCubeMapEnabledHandle = glGetUniformLocation(program, U_CUBEMAP_ENABLED);

            uReverseReflectionHandle = glGetUniformLocation(program, U_REVERSE_REFLECTION);
            uObjectPositionHandle = glGetUniformLocation(program, U_OBJECT_POSITION);
            uCameraPositionHandle = glGetUniformLocation(program, U_CAMERA_POSITION);
            uCameraAngleHandle = glGetUniformLocation(program, U_CAMERA_ANGLE);
            uReflectiveCubeEnabledHandle = glGetUniformLocation(program, U_REFLECTIVE_CUBE_ENABLED);
            uReflectivenessHandle = glGetUniformLocation(program, U_REFLECTIVENESS);

            Log.d(tag, "Creating VertexBuffer");
            createVertexBuffer();

            //glActiveTexture(GL_TEXTURE0);
            glUniform1i(uTextureUnitHandle, 0);
            //glActiveTexture(GL_TEXTURE1);
            glUniform1i(uCubeMapUnitHandle, 1);

            glUniform1i(uTextureUnit2Handle, 2);

            setupCubeMap();
            setupFBO();
        }
    }

    private void setupCubeMap() {
        // This method is needed to setup for cubemaps and reflection cubemaps

        if (valid == true) {
            //https://darrensweeney.net/2016/10/03/dynamic-cube-mapping-in-opengl/
            glGenTextures(1, dynamic_cubemap_texture, 0);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_CUBE_MAP, dynamic_cubemap_texture[0]);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

            for (int i = 0; i < 6; i++) {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, cubeMapSize, cubeMapSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
            }
        }
    }

    private void setupFBO() {
        // This method is needed to setup the framebuffer for cubemaps and reflection cubemaps.
        // Without it, the textures will all end up black or no reflection will take place.

        if (valid == true) {
            ////////////////////////////////////////////////////////////////////////
            // Generate framebuffer
            glGenFramebuffers(1, frameBuffer, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
            ////////////////////////////////////////////////////////////////////////
            // Generate depthFramebuffer
            glGenRenderbuffers(1, depthRenderBuffer, 0);
            glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer[0]);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, cubeMapSize, cubeMapSize);

            // Attach one of the faces of the cubemap texture to current framebuffer
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X, dynamic_cubemap_texture[0], 0);

            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBuffer[0]);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X, dynamic_cubemap_texture[0], 0);

            //glBindTexture(GL_TEXTURE_2D, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
            glActiveTexture(GL_TEXTURE0);
        }
    }

    public void setReflectiveness(float reflectiveness) {
        if (reflectiveness <= 0f) reflectiveness = 0f;
        if (reflectiveness >= 1f) reflectiveness = 1f;
        glUniform1f(uReflectivenessHandle, reflectiveness);
    }

    private void checkReflectiveCubeEnabled(){
        if (reflectiveCubeEnabled == true){
            glUniform1i(uReflectiveCubeEnabledHandle, 1);
        }
        else {
            glUniform1i(uReflectiveCubeEnabledHandle, 0);
        }
    }

    private void checkReverseReflection(){
        if (reverseReflection == true){
            glUniform1i(uReverseReflectionHandle, 1);
        }
        else{
            glUniform1i(uReverseReflectionHandle, 0);
        }
    }

    public void setTexture(int texture){
        if(texture_enabled == true){
            try {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, texture);
                glUniform1i(uTextureUnitHandle, 0);
                glUniform1i(uTextureEnabledHandle, 1);
            }
            catch (Exception e) {
                Log.d(tag, "Could not set texture");
            }
        }
        else{
            try {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, 0);
                glUniform1i(uTextureEnabledHandle, 0);
            }
            catch (Exception e) {
                Log.d(tag, "Could not unbind texture");
            }
        }
    }

    public void setTexture2(int texture){
        if(texture2_enabled == true){
            try {
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, texture);
                glUniform1i(uTextureUnit2Handle, 2);
                glUniform1i(uTextureEnabled2Handle, 1);
            }
            catch (Exception e) {
                Log.d(tag, "Could not set texture");
            }
        }
        else{
            try {
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, 0);
                glUniform1i(uTextureEnabled2Handle, 0);
            }
            catch (Exception e) {
                Log.d(tag, "Could not unbind texture");
            }
        }
    }

    public void setCubeMapTexture(int cubemapTexture){
        if (valid == true) {
            if (cubemap_enabled == true) {
                try {
                    //if (dynamic_cubemap_texture[0] != 0) {
                    glActiveTexture(GL_TEXTURE1);
                    glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture);
                    glUniform1i(uCubeMapUnitHandle, 1);
                    glUniform1i(uCubeMapEnabledHandle, 1);
                    //}
                } catch (Exception e) {
                    Log.d(tag, "Could not set cubemap texture");
                }

            } else {
                try {
                    glActiveTexture(GL_TEXTURE1);
                    glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
                    glUniform1i(uCubeMapEnabledHandle, 0);

                } catch (Exception e) {
                    Log.d(tag, "Could not unbind cubemap texture");
                }
            }
        }
    }

    public void rgba(float red, float green, float blue, float alpha){
        if (valid == true) {
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
    }

    private void transformWorldMatrices() {
        if (valid == true) {
            Matrix.setIdentityM(worldPositionMatrix, 0);
            Matrix.setIdentityM(worldRotateMatrix, 0);
            Matrix.setIdentityM(worldRotateMatrixX, 0);
            Matrix.setIdentityM(worldRotateMatrixY, 0);
            Matrix.setIdentityM(worldRotateMatrixZ, 0);

            Matrix.translateM(worldPositionMatrix, 0, world_position.x, world_position.y, world_position.z);

            Matrix.rotateM(worldRotateMatrixZ, 0, world_angle.z, 0f, 0f, 1f);
            Matrix.rotateM(worldRotateMatrixX, 0, world_angle.x, 1f, 0f, 0f);
            Matrix.rotateM(worldRotateMatrixY, 0, world_angle.y, 0f, 1f, 0f);

            Matrix.multiplyMM(worldRotateMatrix, 0, worldRotateMatrixZ, 0, worldRotateMatrixX, 0);
            Matrix.multiplyMM(worldRotateMatrix, 0, worldRotateMatrix, 0, worldRotateMatrixY, 0);

            Matrix.multiplyMM(worldMatrix, 0, worldPositionMatrix, 0, worldRotateMatrix, 0);
        }
    }

    private void transformLocalMatrices() {
        if (valid == true) {
            Matrix.setIdentityM(localPositionMatrix, 0);
            Matrix.setIdentityM(localRotateMatrix, 0);
            Matrix.setIdentityM(localRotateMatrixX, 0);
            Matrix.setIdentityM(localRotateMatrixY, 0);
            Matrix.setIdentityM(localRotateMatrixZ, 0);

            Matrix.translateM(localPositionMatrix, 0, local_position.x, local_position.y, local_position.z);

            Matrix.rotateM(localRotateMatrixZ, 0, local_angle.z, 0f, 0f, 1f);
            Matrix.rotateM(localRotateMatrixX, 0, local_angle.x, 1f, 0f, 0f);
            Matrix.rotateM(localRotateMatrixY, 0, local_angle.y, 0f, 1f, 0f);

            Matrix.multiplyMM(localRotateMatrix, 0, localRotateMatrixZ, 0, localRotateMatrixX, 0);
            Matrix.multiplyMM(localRotateMatrix, 0, localRotateMatrix, 0, localRotateMatrixY, 0);

            Matrix.multiplyMM(localMatrix, 0, localPositionMatrix, 0, localRotateMatrix, 0);
        }
    }

    public void getTransformedPosition(){
        if (valid == true) {
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
    }

    public void updateMatrices(Camera3D camera) {
        if (valid == true) {
            getTransformedPosition();

            //The mvMatrix is the one fed into the shader, and must be done WITHOUT SCALING the object.
            glUniformMatrix4fv(uMMatrixHandle, 1, false, modelMatrix, 0);
            Matrix.multiplyMM(unScaledModelViewMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
            glUniformMatrix4fv(uMVMatrixHandle, 1, false, unScaledModelViewMatrix, 0);

            //Now we can scale the object and project it
            Matrix.scaleM(modelMatrix, 0, scalar.x, scalar.y, scalar.z);

            Matrix.multiplyMM(scaledModelViewMatrix, 0, camera.viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(mvpMatrix, 0, camera.projectionMatrix, 0, scaledModelViewMatrix, 0);

            glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0);
            glUniformMatrix4fv(uVMatrixHandle, 1, false, camera.viewMatrix, 0);

            glUniform3f(uObjectPositionHandle, world_position.x, world_position.y, world_position.z);
            glUniform3f(uCameraPositionHandle, camera.world_position.x, camera.world_position.y, camera.world_position.z);
            glUniform3f(uCameraAngleHandle, camera.angle.x, camera.angle.y, camera.angle.z);
        }
    }

    public void draw(){
        if (valid == true) {

            glEnable(GL_DEPTH_TEST);

            if (vertexBufferEnabled) {
                checkReverseReflection();
                checkReflectiveCubeEnabled();
                glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
                enableVertexAttribArrays();
                bindData();
                glDrawArrays(GL_TRIANGLES, 0, numberOfFaces * NUMBER_OF_SIDES_PER_FACE);
                disableVertexAttribArrays();
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            } else {
                checkReverseReflection();
                checkReflectiveCubeEnabled();
                enableVertexAttribArrays();
                bindData();
                glDrawArrays(GL_TRIANGLES, 0, numberOfFaces * NUMBER_OF_SIDES_PER_FACE);
                disableVertexAttribArrays();
            }

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
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
        if (valid == true) {
            if (vertexBufferEnabled) {
                int offset = 0;
                glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);
                glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_3D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_3D, offset);
                offset += POSITION_COMPONENT_COUNT_3D;
                glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, numberOfVertices * offset * BYTES_PER_FLOAT);
                offset += COLOR_COMPONENT_COUNT;
                glVertexAttribPointer(aTextureCoordinateHandle, TEXTURE_COORDINATES_COMPONENT_COUNT, GL_FLOAT, false, TEXTURE_COORDINATE_COMPONENT_STRIDE, numberOfVertices * offset * BYTES_PER_FLOAT);
                offset += TEXTURE_COORDINATES_COMPONENT_COUNT;
                glVertexAttribPointer(aNormalHandle, NORMAL_COMPONENT_COUNT, GL_FLOAT, false, NORMAL_COMPONENT_STRIDE, numberOfVertices * offset * BYTES_PER_FLOAT);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            } else {
                vertexBuffer.position(0);
                glVertexAttribPointer(aPositionHandle, POSITION_COMPONENT_COUNT_3D, GL_FLOAT, false, POSITION_COMPONENT_STRIDE_3D, vertexBuffer);

                colorBuffer.position(0);
                glVertexAttribPointer(aColorHandle, COLOR_COMPONENT_COUNT, GL_FLOAT, false, COLOR_COMPONENT_STRIDE, colorBuffer);

                textureCoordBuffer.position(0);
                glVertexAttribPointer(aTextureCoordinateHandle, TEXTURE_COORDINATES_COMPONENT_COUNT, GL_FLOAT, false, TEXTURE_COORDINATE_COMPONENT_STRIDE, textureCoordBuffer);

                normalBuffer.position(0);
                glVertexAttribPointer(aNormalHandle, NORMAL_COMPONENT_COUNT, GL_FLOAT, false, NORMAL_COMPONENT_STRIDE, normalBuffer);
            }
        }
    }

    public void createVertexBuffer(){
        if (valid == true) {
            glGenBuffers(1, vertexBufferObject, 0);
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject[0]);

            int vertexLine = POSITION_COMPONENT_COUNT_3D + COLOR_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT;
            glBufferData(GL_ARRAY_BUFFER, numberOfVertices * vertexLine * BYTES_PER_FLOAT, null, GL_STATIC_DRAW);
            int offset = 0;
            glBufferSubData(GL_ARRAY_BUFFER, offset,
                    numberOfVertices * POSITION_COMPONENT_COUNT_3D * BYTES_PER_FLOAT, vertexBuffer);
            offset += POSITION_COMPONENT_COUNT_3D;
            glBufferSubData(GL_ARRAY_BUFFER, numberOfVertices * offset * BYTES_PER_FLOAT,
                    numberOfVertices * COLOR_COMPONENT_COUNT * BYTES_PER_FLOAT, colorBuffer);

            offset += COLOR_COMPONENT_COUNT;
            glBufferSubData(GL_ARRAY_BUFFER, numberOfVertices * offset * BYTES_PER_FLOAT,
                    numberOfVertices * TEXTURE_COORDINATES_COMPONENT_COUNT * BYTES_PER_FLOAT, textureCoordBuffer);

            offset += TEXTURE_COORDINATES_COMPONENT_COUNT;
            glBufferSubData(GL_ARRAY_BUFFER, numberOfVertices * offset * BYTES_PER_FLOAT,
                    numberOfVertices * NORMAL_COMPONENT_COUNT * BYTES_PER_FLOAT, normalBuffer);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void release(){
        disableVertexAttribArrays();

        vertexList = null;
        indexList = null;
        colorList = null;
        textureCoordList = null;
        textureCoordList = null;
        normalList = null;
        modelMatrix = null;
        unScaledModelViewMatrix = null;
        scaledModelViewMatrix = null;
        mvpMatrix = null;
        vertexBufferObject = null;
        //indexBufferObject = null;

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

        if (local_position != null)
            local_position = null;

        if (local_angle != null)
            local_angle = null;
    }
}