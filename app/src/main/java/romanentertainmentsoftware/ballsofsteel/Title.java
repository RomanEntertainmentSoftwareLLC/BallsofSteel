package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;
import math3D.Vector3D;
import math3D.Vertex3D;
import object3D.Object3D;
import primitive2D.Quad2D;
import primitive3D.Quad3D;
import static android.opengl.GLES30.*;

/**
 * Created by Roman Entertainment Software LLC on 4/24/2018.
 */

public class Title {
    private Context context;

    public long titleMilliseconds;
    private double titleTime;
    private float titleSpeed;
    public boolean titleEnabled;
    private float fadeSpeed;

    public float[] alpha = new float[4];
    private float[] getAlpha = new float[4];
    private int[] texture = new int[4];
    private Quad2D bricksAndShip;

    private Object3D cube;

    private Quad3D title;
    private Quad2D tapToStart;

    private boolean flipflop;

    public boolean tapped;

    private Vertex3D cubePos = new Vertex3D();
    private float cubeAngle;

    private float bricksAndShipAngle;

    private Vertex3D titlePos = new Vertex3D();
    private float titleAngle;

    private float tapToStartAngle;

    public Title(Context context){
        this.context = context;
        alpha[0] = 0f;
        alpha[1] = 1f;
        alpha[2] = 1f;
        alpha[3] = 1f;
        titleSpeed = timeConvert(3f);
        fadeSpeed = timeConvert(3f);
    }

    public void setupTextures(){
        texture[0] = Texture.loadTexture(context, R.drawable.titlebricksandship);
        texture[1] = Texture.loadTexture(context, R.drawable.background);
        texture[2] = Texture.loadTexture(context, R.drawable.title);
        texture[3] = Texture.loadTexture(context, R.drawable.taptostart);
    }

    public void setup(){
        bricksAndShip = new Quad2D(context, "QUAD2D", Render.programTextured,
                0f, 0f,
                Render.camera[0].aspectRatio, 0f,
                0f,1f,
                Render.camera[0].aspectRatio,1f,
                1f, 1f, 1f, 1f,
                1f,1f);

        title = new Quad3D(context, Render.programTexturedDirectionalSpecularLit,
                -100f, 8.6f, 0f, 1f,
                100f , 8.6f, 0f, 1f,
                -100f , -8.6f, 0f, 1f,
                100f , -8.6f, 0f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        tapToStart = new Quad2D(context, "QUAD2D", Render.programTextured,
                Render.camera[0].aspectRatio * -0.25f, -0.024f,
                Render.camera[0].aspectRatio * 0.25f, -0.024f,
                Render.camera[0].aspectRatio * -0.25f,0.024f,
                Render.camera[0].aspectRatio * 0.25f,0.024f,
                1f, 1f, 1f, 1f,
                1f,1f);

        cube = new Object3D(context, "OBJECT3D", Render.programTexturedDirectionalSpecularLit, R.raw.cube, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), cubePos, new Vector3D(0f, 0f, 0f), new Vector3D(500f, 500f, 500f), 1f, 1f, 1f, 1f, 1f, true);
        cube.loadFile();
        cube.setup();

        setupTextures();
    }

    private float timeConvert(float seconds)
    {
        if (seconds != 0f)
            return (1000 / seconds) * 0.001f;
        else
            return 0f;
    }

    public void renderCube(){
        glDisable(GL_CULL_FACE);
        glFrontFace(GL_CW);
        cube.world_position.x = 0f;
        cube.world_position.y = 0f;
        cube.world_position.z = 0f;
        cube.local_angle.x = cubeAngle;
        cube.local_angle.y = cubeAngle;
        cube.local_angle.z = 0f;
        cube.vertexBufferEnabled = true;
        cube.texture_enabled = true;
        cube.updateMatrices(Render.camera[0]);
        cube.setTexture(texture[1]);
        cube.rgba(1f, 1f, 1f, alpha[1]);
        cube.draw();

        cubeAngle += 2f;
        cubeAngle %= 360f;

        if (tapped){
            titleTime = ((double) System.currentTimeMillis() - (double)titleMilliseconds) * 0.001;
            alpha[1] = getAlpha[1] - (fadeSpeed * (float)titleTime);
            if (alpha[1] <= 0f) {
                alpha[1] = 0f;
            }
        }
    }

    public void renderBricksAndShip(){
        bricksAndShip.position.x = 0f;
        bricksAndShip.position.y = 0f;
        bricksAndShipAngle = 0f;
        bricksAndShip.bindData();
        bricksAndShip.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, bricksAndShipAngle);
        bricksAndShip.setTexture(texture[0]);
        bricksAndShip.rgba(1f,1f,1f, alpha[0]);
        bricksAndShip.draw();

        if (tapped){
            titleTime = ((double) System.currentTimeMillis() - (double)titleMilliseconds) * 0.001;
            alpha[0] = getAlpha[0] - (fadeSpeed * (float)titleTime);
            if (alpha[0] <= 0f) {
                alpha[0] = 0f;
            }
        }
        else {
            if (titleEnabled) {
                titleTime = ((double) System.currentTimeMillis() - (double) titleMilliseconds) * 0.001;
                alpha[0] = titleSpeed * (float) titleTime;
                if (alpha[0] >= 1f) {
                    alpha[0] = 1f;
                    titleEnabled = false;
                }
            }
        }
    }

    public void renderTitle(){
        float pos_speed = 0.55f;
        float angle_speed = 4.5f;
        float max_pos = -((360f / angle_speed) * pos_speed) * 4f;

        titlePos.x = 0f;
        titlePos.y = 35f;
        //titlePos.z = 100f;

        if (titlePos.z <= max_pos)
        {
            titlePos.z = max_pos;
            titleAngle = 0f;
        }
        else {
            titlePos.z -= pos_speed;
            titleAngle -= angle_speed;
            titleAngle %= 360f;
        }

        title.world_position.x = titlePos.x;
        title.world_position.y = titlePos.y;
        title.world_position.z = titlePos.z;
        title.local_angle.x = titleAngle;
        title.local_angle.y = 0f;
        title.local_angle.z = 0f;
        title.twoSided = true;
        title.texture_enabled = true;
        title.updateMatrices(Render.camera[0]);
        title.setTexture(texture[2]);
        title.rgba(1f, 1f, 1f, alpha[2]);
        title.draw();

        if (tapped == true){
            titleTime = ((double)System.currentTimeMillis() - (double)titleMilliseconds) * 0.001;
            alpha[2] = getAlpha[2] - (fadeSpeed * (float)titleTime);
            if (alpha[2] <= 0f) {
                alpha[2] = 0f;
            }
        }
    }

    public void renderTapToStart(){
        tapToStart.position.x = 0.5f;
        tapToStart.position.y = 0.5f;
        tapToStart.bindData();
        tapToStart.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        tapToStart.setTexture(texture[3]);
        tapToStart.rgba(1f,0f,0f, alpha[3]);
        tapToStart.draw();

        if (tapped == true){
            titleTime = ((double) System.currentTimeMillis() - (double)titleMilliseconds) * 0.001;
            alpha[3] = getAlpha[3] - (fadeSpeed * (float)titleTime);
            if (alpha[3] <= 0f) {
                alpha[3] = 0f;
            }
        }
        else {
            if (flipflop == false) {
                alpha[3] -= 0.03f;
                if (alpha[3] <= 0f) //alpha <= 0f
                    flipflop = true;
            } else {
                alpha[3] += 0.03f;
                if (alpha[3] >= 1f) //alpha >= 1f
                    flipflop = false;
            }
        }
    }

    public void obtainAlphas(){
        for(int i = 0; i < 4; i++)
            getAlpha[i] = alpha[i];
    }

    public void release(){
        // Release arrays
        alpha = null;
        getAlpha = null;
        texture = null;

        // Release Objects
        if (bricksAndShip != null){
            bricksAndShip.release();
            bricksAndShip = null;
        }

        if (cube != null){
            cube.release();
            cube = null;
        }

        if (title != null){
            title.release();
            title = null;
        }

        if (tapToStart != null){
            tapToStart.release();
            tapToStart = null;
        }

        cubePos = null;
        titlePos = null;
    }
}