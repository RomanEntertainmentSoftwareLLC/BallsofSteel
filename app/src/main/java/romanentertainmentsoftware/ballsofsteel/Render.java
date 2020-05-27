package romanentertainmentsoftware.ballsofsteel;

/**
 * Created by Roman Entertainment Software LLC on 4/14/2018.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.*;

import android.opengl.Matrix;
import android.util.Log;


import math2D.Vector2D;
import math3D.Matrix4x4;
import math3D.Plane3D;
import math3D.Vector3D;
import math3D.Vertex3D;
import object3D.Object3D;
import object3D.WireframeObject3D;
import primitive2D.Quad2D;
import primitive3D.Ellipsoid3D;
import primitive3D.Line3D;
import primitive3D.Particle3D;
import primitive3D.Quad3D;

public class Render implements GLSurfaceView.Renderer {

    // 6/17/2018
    // TODO Object culling
    // TODO Fly Camera
    // TODO Get object plane parallel / relative to the near view plane
    // TODO     Move object along this plane no matter what angle


    public enum GameState {
        LOGO,
        TITLE,
        LOAD_SCREEN,
        GAME
    }

    private final String TAG = "RENDER";

    private final int SCALEDOWN_FACTOR = 8;

    // Context
    public static Context context;

    // Needs to be public static, because it is being used by Logo / Title
    public static float[] projectionMatrix2D = new float[16];

    // Game State Holder
    public static GameState gameState;

    // Game Necessities
    // Needs to be public static because it is being accessed via MainActivity
    public static Logo logo;
    public static Title title;

    // Cameras
    public static Camera3D[] camera = new Camera3D[2];
    public static Camera3D viewableCamera;
    public static Camera3D otherCamera;
    public static Camera3D loadScreenCamera;
    public static Camera3D cubeCamera;
    public int cameraNumber = 0;

    // Sounds
    public static Sound sound;

    // Shader Programs
    // Some need to be public static, because it is being used by Logo / Title
    public static int programParticle; //Not like "simple" because it has "size"
    public static int programSimple;
    public static int programTextured;
    public static int programTexturedDirectionalSpecularLit;
    public static int programDepth;
    public static int programExposureToneMapping;
    public static int programBrightFilter;
    public static int programHorizontalBlur;
    public static int programVerticalBlur;
    public static int programContrastBoost;
    public static int programMultiTexture;

    // Lights
    public static Light light;
    public static Light simpleLight;

    // Lines
    public static Line3D line;

    // 3D Objects
    public static Object3D filmCamera;
    public static Object3D ball;
    public static Object3D ball2;
    public static Object3D cube;
    public static Object3D cubeReflection;
    //public static Object3D brick
    public static Object3D[][][] brick = new Object3D[10][10][2];
    public static Object3D wormhole;
    public static Object3D[] ship = new Object3D[2];
    public static Object3D mario;
    public static Object3D tesseract;
    public static Object3D simpleCube;

    // Wireframe Objects
    private WireframeObject3D[] ship2 = new WireframeObject3D[2];

    // Particles
    public static Particle3D particle;
    public static Particle3D particlePointLight;

    // Textures
    private int cubeTexture;
    private int stoneTexture;
    private int wormholeTexture;
    public static int shipTexture;
    private int shipTexture2;
    private int marioTexture;
    private int vrMaskTexture;
    private int metalBallTexture;
    private int[] skyboxTexture = new int[8];
    private int[] colorTexture = new int[8];

    public float angle = 0f;


    private boolean touchInRange;
    private boolean touchOverLap;

    public static AnalogController[] controller = new AnalogController[2];

    private boolean tapFlag;

    private float[] ballRadius = new float[3];

    private float[] rayDirection = new float[4];
    private float[] pickOrigin = new float[4];
    private float[] pickPos = new float[4];
    private float raySize = 100f;
    private float[] rayStart = new float[4];
    private float[] rayEnd = new float[4];

    private FrameBuffer frameBufferPanel = new FrameBuffer();

    private FrameBuffer frameBufferFront = new FrameBuffer();
    private FrameBuffer frameBufferBack = new FrameBuffer();
    private FrameBuffer frameBufferLeft = new FrameBuffer();
    private FrameBuffer frameBufferRight = new FrameBuffer();
    private FrameBuffer frameBufferUp = new FrameBuffer();
    private FrameBuffer frameBufferDown = new FrameBuffer();

    public static FrameBuffer frameBuffer = new FrameBuffer();
    public static FrameBuffer depthBuffer = new FrameBuffer();
    private FrameBuffer frameBufferHalf = new FrameBuffer();

    private FrameBuffer frameBufferLeftEye = new FrameBuffer();
    private FrameBuffer frameBufferRightEye = new FrameBuffer();

    public static FrameBuffer exposureToneMappingFrameBuffer = new FrameBuffer();
    public static FrameBuffer brightFilterFrameBuffer = new FrameBuffer();
    public static FrameBuffer horizontalBlurFrameBuffer = new FrameBuffer();
    public static FrameBuffer verticalBlurFrameBuffer = new FrameBuffer();
    public static FrameBuffer contrastBoostFrameBuffer = new FrameBuffer();
    public static FrameBuffer bloomFrameBuffer = new FrameBuffer();

    private Quad2D frameBufferPolygon;
    private Quad2D frameBufferPolygon2;
    private Quad2D vrMaskPolygon;
    public static Quad2D fullscreen_rendered_polygon;
    private Quad2D halfscreen_rendered_polygon;

    public static Quad2D exposure_tone_mapped_polygon;
    public static Quad2D bright_filter_polygon;
    public static Quad2D horizontal_blur_buffer_polygon;
    public static Quad2D vertical_blur_buffer_polygon;
    public static Quad2D contrast_boost_polygon;

    private Quad2D cubemap_polygon_front;
    private Quad2D cubemap_polygon_back;
    private Quad2D cubemap_polygon_left;
    private Quad2D cubemap_polygon_right;
    private Quad2D cubemap_polygon_up;
    private Quad2D cubemap_polygon_down;

    private Quad2D reflection_cubemap_polygon_front;
    private Quad2D reflection_cubemap_polygon_back;
    private Quad2D reflection_cubemap_polygon_left;
    private Quad2D reflection_cubemap_polygon_right;
    private Quad2D reflection_cubemap_polygon_up;
    private Quad2D reflection_cubemap_polygon_down;

    public static MultiTexture2D fullscreen_rendered_multitextured_polygon;

    float[] nearPos = new float[4];
    float[] farPos = new float[4];

    public static OpenGLThread openGLThread;
    private Thread thread;

    private float initial_azimuth;
    private float initial_pitch;
    private float initial_roll;

    public static ExposureToneMappingBuffer exposureToneMappingBuffer;
    public static BrightFilterBuffer brightFilterBuffer;
    public static BlurBuffer blurBuffer;
    public static ContrastBoostBuffer contrastBoostBuffer;

    public static Plane3D[] vectorSpaceSide = new Plane3D[6];

    public static Ellipsoid3D ellipsoid;

    Vertex3D[] collidablePoint = new Vertex3D[6];

    boolean[] collisionResult = new boolean[6];

    Vertex3D[] vector_space_pa = new Vertex3D[6];
    Vertex3D[] vector_space_pb = new Vertex3D[6];
    Vertex3D[] vector_space_pc = new Vertex3D[6];
    Vertex3D[] vector_space_pd = new Vertex3D[6];

    public static Particle3D[] collidablePointParticle = new Particle3D[6];

    public static Particle3D[] particleCam = new Particle3D[10];

    public static Quad3D[] viewFrustumPolygon = new Quad3D[6];

    static final int numberOfFlareTextures = 6;
    static final int numberOfFlares = 6;
    public static Quad2D[] lensFlare = new Quad2D[numberOfFlares];
    public static float[] lensFlareSize = new float[numberOfFlares];
    public static int[] lensFlareTexture = new int[numberOfFlareTextures];

    Plane3D plane;

    Render(Context context) {
        Log.d(TAG, "Render Class Created!");
        this.context = context;
        gameState = GameState.LOGO;
        logo = new Logo(context);
        title = new Title(context);
        sound = new Sound(context);
        openGLThread = new OpenGLThread(context);
        thread = new Thread(openGLThread);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated()");
        //Note: Fires every time you minimize to maximize
        //ie: pressing home and re-entering.
        for (int i = 0; i < 2; i++){
            MainActivity.moved[i] = false;
            MainActivity.heldDown[i] = false;
        }

        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthFunc(GL_LESS);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        Log.d(TAG, "onSurfacedChanged()");
        //Note: Fires every time you minimize to maximize
        //ie: pressing home and re-entering.

        glViewport(0, 0, width, height);

        loadShaders();
        setupSounds();
        setupTextures();

        camera[0] = new Camera3D(new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f));
        camera[0].fov = 45f;
        camera[0].farZ = 2000f;
        camera[0].nearZ = 1f;
        camera[0].screenWidth = width;
        camera[0].screenHeight = height;
        camera[0].aspectRatio = ((float) width / (float) height);

        camera[1] = new Camera3D(new Vertex3D(0f, 0f, 30f), new Vector3D(0f, 0f, 0f));
        camera[1].fov = 45f;
        camera[1].farZ = 2000f;
        camera[1].nearZ = 1f;
        camera[1].screenWidth = width;
        camera[1].screenHeight = height;
        camera[1].aspectRatio = ((float) width / (float) height);

        loadScreenCamera = new Camera3D(new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f));
        loadScreenCamera.fov = 45f;
        loadScreenCamera.farZ = 2000f;
        loadScreenCamera.nearZ = 1f;
        loadScreenCamera.screenWidth = width;
        loadScreenCamera.screenHeight = height;
        loadScreenCamera.aspectRatio = ((float) width / (float) height);

        cubeCamera = new Camera3D(new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f));
        cubeCamera.fov = 45f;
        cubeCamera.farZ = 2000f;
        cubeCamera.nearZ = 1.0f;
        cubeCamera.offset_position.z = -50f;
        cubeCamera.screenWidth = width;
        cubeCamera.screenHeight = height;
        cubeCamera.aspectRatio = ((float) width / (float) height);

        //Landscape
        Matrix.orthoM(projectionMatrix2D, 0, 0f, camera[0].aspectRatio, 1f, 0f, -1f, 1f);

        //  Camera Projection Matrix
        float size = camera[0].nearZ * (float)Math.tan((double)camera[0].fov / 2.0 * (Math.PI / 180.0));

        Matrix.frustumM(camera[0].projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, camera[0].nearZ, camera[0].farZ);
        Matrix.frustumM(loadScreenCamera.projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, loadScreenCamera.nearZ, loadScreenCamera.farZ);
        Matrix.frustumM(camera[1].projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, camera[1].nearZ, camera[1].farZ);
        Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);

        //Matrix4x4.printArray(camera[0].projectionMatrix);

        camera[0].update();

        viewFrustumPolygon[0] = new Quad3D(context, programSimple,
                camera[0].ftl.x, camera[0].ftl.y, camera[0].ftl.z, 1f,
                camera[0].ftr.x, camera[0].ftr.y, camera[0].ftr.z, 1f,
                camera[0].fbl.x, camera[0].fbl.y, camera[0].fbl.z, 1f,
                camera[0].fbr.x, camera[0].fbr.y, camera[0].fbr.z, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        viewFrustumPolygon[1] = new Quad3D(context, programSimple,
                camera[0].ftr.x, camera[0].ftr.y, camera[0].ftr.z, 1f,
                camera[0].ntr.x, camera[0].ntr.y, camera[0].ntr.z, 1f,
                camera[0].fbr.x, camera[0].fbr.y, camera[0].fbr.z, 1f,
                camera[0].nbr.x, camera[0].nbr.y, camera[0].nbr.z, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        viewFrustumPolygon[2] = new Quad3D(context, programSimple,
                camera[0].ntr.x, camera[0].ntr.y, camera[0].ntr.z, 1f,
                camera[0].ntl.x, camera[0].ntl.y, camera[0].ntl.z, 1f,
                camera[0].nbr.x, camera[0].nbr.y, camera[0].nbr.z, 1f,
                camera[0].nbl.x, camera[0].nbl.y, camera[0].nbl.z, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        viewFrustumPolygon[3] = new Quad3D(context, programSimple,
                camera[0].ntl.x, camera[0].ntl.y, camera[0].ntl.z, 1f,
                camera[0].ftl.x, camera[0].ftl.y, camera[0].ftl.z, 1f,
                camera[0].nbl.x, camera[0].nbl.y, camera[0].nbl.z, 1f,
                camera[0].fbl.x, camera[0].fbl.y, camera[0].fbl.z, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        viewFrustumPolygon[4] = new Quad3D(context, programSimple,
                camera[0].ntl.x, camera[0].ntl.y, camera[0].ntl.z, 1f,
                camera[0].ntr.x, camera[0].ntr.y, camera[0].ntr.z, 1f,
                camera[0].ftl.x, camera[0].ftl.y, camera[0].ftl.z, 1f,
                camera[0].ftr.x, camera[0].ftr.y, camera[0].ftr.z, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        viewFrustumPolygon[5] = new Quad3D(context, programSimple,
                camera[0].fbl.x, camera[0].fbl.y, camera[0].fbl.z, 1f,
                camera[0].fbr.x, camera[0].fbr.y, camera[0].fbr.z, 1f,
                camera[0].nbl.x, camera[0].nbl.y, camera[0].nbl.z, 1f,
                camera[0].nbr.x, camera[0].nbr.y, camera[0].nbr.z, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        viewableCamera = camera[0];
        otherCamera = camera[1];

        light = new Light(programTexturedDirectionalSpecularLit);

        light.ambient(0.1f, 0.1f, 0.1f);
        light.diffuse(1f, 1f, 1f);
        light.specular(1f, 1f, 1f);
        light.direction(0f, 0f, 1f);
        //light.direction(-1f, 0.89f, 0.69f);
        light.ambientEnable(true);
        light.diffuseEnable(true);
        light.specularEnable(true);
        light.specularIntensity(20f);

        simpleLight = new Light(programSimple);

        simpleLight.ambient(0.1f, 0.1f, 0.1f);
        simpleLight.diffuse(1f, 1f, 1f);
        simpleLight.specular(1f, 1f, 1f);
        simpleLight.direction(0f, 0f, 1f);
        //simpleLight.direction(-1f, 0.89f, 0.69f);
        simpleLight.ambientEnable(true);
        simpleLight.diffuseEnable(true);
        simpleLight.specularEnable(true);
        simpleLight.specularIntensity(20f);

        lensFlareSize[0] = 0.50f;
        lensFlareSize[1] = 0.10f;
        lensFlareSize[2] = 0.20f;
        lensFlareSize[3] = 0.10f;
        lensFlareSize[4] = 0.20f;
        lensFlareSize[5] = 0.30f;

        for (int i = 0; i < numberOfFlares; i++){

            lensFlare[i] = new Quad2D(context, "Quad2D: Lens Flare", programTextured,
                    -lensFlareSize[i] * camera[0].aspectRatio, lensFlareSize[i],
                    lensFlareSize[i] * camera[0].aspectRatio,  lensFlareSize[i],
                    -lensFlareSize[i] * camera[0].aspectRatio, -lensFlareSize[i],
                    lensFlareSize[i] * camera[0].aspectRatio, -lensFlareSize[i],
                    1f, 1f, 1f, 1f,
                    1f, -1f);
        }

        switch (gameState){
            case LOGO:{
                logo.setup();
                break;
            }
            case TITLE:{
                title.setup();
                break;
            }
            case LOAD_SCREEN:{
                loadObjects();

                break;
            }
            case GAME: {
                gameState = GameState.LOAD_SCREEN;
                loadObjects();
                break;
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        switch(gameState){
            case LOGO:{
                if (logo != null){
                    logo.run();
                    glUseProgram(programTextured);
                    logo.renderLogo();
                    logo.renderLegal();

                    sound.playOnce(0);

                    if (logo.finito == true){
                        gameState = GameState.TITLE;
                        logo.release();
                        title.setup();
                        title.titleEnabled = true;
                        title.titleMilliseconds = System.currentTimeMillis();
                    }
                }

                break;
            }
            case TITLE:{
                if (title != null){
                    if (MainActivity.heldDown[0] == true) {
                        if (title.tapped == false) {
                            title.tapped = true;
                            title.titleMilliseconds = System.currentTimeMillis();
                            title.obtainAlphas();
                        }
                    }

                    if (title.tapped == true) {
                        if (title.alpha[1] <= 0f) {
                            gameState = GameState.LOAD_SCREEN;
                            sound.stop(1);
                            title.release();

                            loadObjects();

                            break;
                        }
                    }

                    if (gameState != GameState.GAME){
                        // Render the background cube
                        glUseProgram(programTexturedDirectionalSpecularLit);

                        light.enable(false);

                        camera[0].world_position.x = 0f;
                        camera[0].world_position.y = 0f;
                        camera[0].world_position.z = 0f;

                        camera[0].offset_position.x = 0f;
                        camera[0].offset_position.y = 0f;
                        camera[0].offset_position.z = 0f;

                        camera[0].update();

                        glEnable(GL_DEPTH_TEST);
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_FRONT);
                        glFrontFace(GL_CCW);

                        title.renderCube();

                        // Render the 2D bricks and ship as well as tap to start
                        glDisable(GL_DEPTH_TEST);
                        glDisable(GL_CULL_FACE);
                        glUseProgram(programTextured);
                        title.renderBricksAndShip();
                        title.renderTapToStart();

                        // Render the 3D spinning title next:
                        glUseProgram(programTexturedDirectionalSpecularLit);

                        light.enable(true);
                        light.specularEnable(true);
                        light.setLightType(Light.TYPE.directionalLight);

                        glEnable(GL_DEPTH_TEST);
                        glDisable(GL_CULL_FACE);
                        glFrontFace(GL_CW);

                        title.renderTitle();

                        // Play the title screen music
                        sound.play(1);
                    }
                }
                break;
            }
            case LOAD_SCREEN:{
                glEnable(GL_DEPTH_TEST);

                if (loadScreenCamera != null) {
                    loadScreenCamera.update();

                    glUseProgram(programTexturedDirectionalSpecularLit);

                    if (tesseract != null){

                        light.enable(true);
                        light.specularEnable(true);
                        light.setLightType(Light.TYPE.directionalLight);
                        light.direction(-1f, 0.89f, 0.69f);

                        glEnable(GL_DEPTH_TEST);
                        glEnable(GL_CULL_FACE);
                        glCullFace(GL_BACK);
                        glFrontFace(GL_CCW);

                        tesseract.texture_enabled = true;
                        tesseract.cubemap_enabled = false;
                        tesseract.reverseReflection = true;
                        tesseract.vertexBufferEnabled = true;
                        tesseract.local_position.x = 0f;
                        tesseract.local_position.y = 0f;
                        tesseract.local_position.z = 0f;
                        tesseract.world_position.x = 0f;
                        tesseract.world_position.y = 0f;
                        tesseract.world_position.z = -100f;
                        tesseract.local_angle.y = angle;
                        tesseract.updateMatrices(loadScreenCamera);
                        tesseract.setTexture(wormholeTexture);
                        //tesseract.setCubeMapTexture(0);
                        tesseract.rgba(1f, 1f, 1f, 1f);
                        tesseract.draw();
                    }

                }

                angle += 1f;
                angle %= 360f;

                if (openGLThread.loaded == true){
                    if (filmCamera != null) filmCamera.setup();
                    if (mario != null) mario.setup();
                    if (cube != null) cube.setup();
                    if (cubeReflection != null) cubeReflection.setup();
                    if (wormhole != null) wormhole.setup();
                    if (ship[0] != null) ship[0].setup();
                    if (ship[1] != null) ship[1].setup();
                    if (ball != null) ball.setup();
                    if (ball2 != null) ball2.setup();
                    if (particle != null) particle.setup();
                    if (particlePointLight != null) particlePointLight.setup();
                    if (simpleCube != null) simpleCube.setup();

                    if (collidablePointParticle[0] != null) collidablePointParticle[0].setup();
                    if (collidablePointParticle[1] != null) collidablePointParticle[1].setup();
                    if (collidablePointParticle[2] != null) collidablePointParticle[2].setup();
                    if (collidablePointParticle[3] != null) collidablePointParticle[3].setup();
                    if (collidablePointParticle[4] != null) collidablePointParticle[4].setup();
                    if (collidablePointParticle[5] != null) collidablePointParticle[5].setup();

                    if (particleCam[0] != null) particleCam[0].setup();
                    if (particleCam[1] != null) particleCam[1].setup();
                    if (particleCam[2] != null) particleCam[2].setup();
                    if (particleCam[3] != null) particleCam[3].setup();
                    if (particleCam[4] != null) particleCam[4].setup();
                    if (particleCam[5] != null) particleCam[5].setup();
                    if (particleCam[6] != null) particleCam[6].setup();
                    if (particleCam[7] != null) particleCam[7].setup();
                    if (particleCam[8] != null) particleCam[8].setup();
                    if (particleCam[9] != null) particleCam[9].setup();

                    //if (brick!= null) brick.setup();


                    for (int z = 0; z < 2; z++) {
                        for (int y = 0; y < 10; y++) {
                            for (int x = 0; x < 2; x++) {
                                if (brick[x][y][z] != null) brick[x][y][z].setup();
                            }
                        }
                    }


                    ////////////////////////////////////////////////////////////////////////////////
                    //Bloom Effect
                    ////////////////////////////////////////////////////////////////////////////////
                    exposureToneMappingBuffer = new ExposureToneMappingBuffer(programExposureToneMapping);
                    brightFilterBuffer = new BrightFilterBuffer(programBrightFilter);
                    blurBuffer = new BlurBuffer(Render.programHorizontalBlur, programVerticalBlur);
                    contrastBoostBuffer = new ContrastBoostBuffer(programContrastBoost);

                    if (exposureToneMappingBuffer != null) exposureToneMappingBuffer.setup();
                    if (brightFilterBuffer != null) brightFilterBuffer.setup();
                    if (blurBuffer != null) blurBuffer.setup();
                    if (contrastBoostBuffer != null) contrastBoostBuffer.setup();

                    exposureToneMappingBuffer.exposure = 1f;
                    exposureToneMappingBuffer.gamma = 1f;

                    brightFilterBuffer.bright_filter_range = 1f;

                    blurBuffer.sigma = 1.0f;
                    blurBuffer.kernel_diameter = 1;
                    blurBuffer.screen_width = MainActivity.width;
                    blurBuffer.number_of_times_blurred = 1;

                    Bloom.createGaussianWeights(blurBuffer);

                    //for (int i = 0; i < blurBuffer.kernel_diameter; i++) {
                        //Log.d("blur buffer", String.valueOf(blurBuffer.weights[i]));
                    //}

                    contrastBoostBuffer.red = 1.0f;
                    contrastBoostBuffer.green = 1.0f;
                    contrastBoostBuffer.blue = 1.0f;
                    ///////////////////////////////////////////////////////////////////////////////

                    ball.world_position.x = 0f;
                    ball.world_position.y = 0f;
                    ball.world_position.z = 0f;

                    ball.velocity.x = 10f;
                    ball.velocity.y = 3f;
                    ball.velocity.z = 4f;

                    //ball.velocity.x = 100f;
                    //ball.velocity.y = 30f;
                    //ball.velocity.z = 40f;

                    ellipsoid.radius = new Vertex3D(25f, 25f, 25f);
                    ellipsoid.gap = new Vertex3D(0f, 0f, 0f);

                    createCollidableQuads();

                    renderCubeMapTextures(cube, skyboxTexture[0], skyboxTexture[1], skyboxTexture[2], skyboxTexture[3], skyboxTexture[4], skyboxTexture[5]);

                    gameState = GameState.GAME;
                    //Log.d("LOADED", "Weve succeeded in loading 3d models!");
                }

                break;
            }

            case GAME:{
                float size = camera[0].nearZ * (float)Math.tan((double)camera[0].fov / 2.0 * (Math.PI / 180.0));

                Matrix.frustumM(camera[0].projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, camera[0].nearZ, camera[0].farZ);
                Matrix.frustumM(loadScreenCamera.projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, loadScreenCamera.nearZ, loadScreenCamera.farZ);
                Matrix.frustumM(camera[1].projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, camera[1].nearZ, camera[1].farZ);
                Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);

                /*
                ball.old_world_position.x = ball.world_position.x;
                ball.old_world_position.y = ball.world_position.y;
                ball.old_world_position.z = ball.world_position.z;

                ellipsoid.vector_space_old_position.x = ball.old_world_position.x / (ellipsoid.radius.x + ellipsoid.gap.x);
                ellipsoid.vector_space_old_position.y = ball.old_world_position.y / (ellipsoid.radius.y + ellipsoid.gap.y);
                ellipsoid.vector_space_old_position.z = ball.old_world_position.z / (ellipsoid.radius.z + ellipsoid.gap.z);
                 */
                if (viewableCamera != null) {
                    changeCameraButtonEvent();
                    controls(viewableCamera);
                }

                //Log.d("camera[0] lookAtVector", String.valueOf(camera[0].lookAtVector.x) + ", " + String.valueOf(camera[0].lookAtVector.y) + ", " + String.valueOf(camera[0].lookAtVector.z));

                //Matrix4x4.printArray(camera[0].modelMatrix);

                /*
                if(MainActivity.heldDown[0]){
                    rayDirection = Picking.calculateMouseRay(camera[0]);

                    // 6/2/2018 Note to self:
                    // - invertedViewMatrix of the camera is dead on balls to the wall accurate when it comes to picking!
                    // - The ray is so accurate, you won't see the 3D line! Shoots through the mouse cursor.

                    // DONE - Wrap the sphere picking into vector space
                    // TODO - Add the ability to pick polygons and planes
                    // DONE - Pick from a certain distance, not all distances
                    // TODO - Make it to where you move the ship by:
                    // TODO     Converting the mouse coordinates to a position in world space in 3D

                    //RayCasting Notes 5/25/2018
                    //---------------------------
                    //  (+) camera[0].invertedViewMatrix[] works great straight, pitiful at angles
                    //  (-) camera[0].viewMatrix[] doesn't seem to work at all and is all over the place
                    //  (-) camera[0].modelMatrix[] works great straight, pitiful at angles
                    //  (+) camera[0].world_position works great straight, pitiful at angles

                    if (MainActivity.touchScaled[0].x >= 0.1f && MainActivity.touchScaled[0].y >= 0.1f) {
                        if (!controller[0].touched[0] && !controller[1].touched[0]) {
                            if (cameraNumber == 0) {
                                rayStart[0] = camera[0].world_position.x;
                                rayStart[1] = camera[0].world_position.y;
                                rayStart[2] = camera[0].world_position.z;

                                rayEnd[0] = rayStart[0] + (rayDirection[0] * raySize);
                                rayEnd[1] = rayStart[1] + (rayDirection[1] * raySize);
                                rayEnd[2] = rayStart[2] + (rayDirection[2] * raySize);
                            }
                        }
                    }
                }
                */

                // 3D Stuff Here
                glEnable(GL_DEPTH_TEST);

                //render(viewableCamera);

                //renderToPanelFrameBuffer(ship[0]);

                //TODO Dynamic Reflection (aka Environment Mapping)
                /*
                renderToFrameBufferFront(ball);
                renderToFrameBufferBack(ball);
                renderToFrameBufferLeft(ball);
                renderToFrameBufferRight(ball);
                renderToFrameBufferUp(ball);
                renderToFrameBufferDown(ball);
                */

                renderToFrameBuffer();
                renderToDepthBuffer();
                Bloom.bloomEffect();

                //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
                //glUseProgram(programTexturedDirectionalSpecularLit);
                //render(viewableCamera);


                // 2D Stuff Here
                glDisable(GL_DEPTH_TEST);
                glDisable(GL_CULL_FACE);

                glUseProgram(programTextured);

                fullscreen_rendered_polygon.bindData();
                fullscreen_rendered_polygon.position.x = 0.0f;
                fullscreen_rendered_polygon.position.y = 0.0f;
                fullscreen_rendered_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
                glBindTexture(GL_TEXTURE_2D, bloomFrameBuffer.texture); //
                fullscreen_rendered_polygon.rgba(1f,1f,1f, 1f);
                fullscreen_rendered_polygon.draw();

                glUseProgram(programTextured);

                //Log.d("Reflect", String.valueOf(reflect.x) + ", " + String.valueOf(reflect.y) + ", " + String.valueOf(reflect.z));

                //renderFrameBufferSide(frameBufferBack.texture);

                //halfscreen_rendered_polygon.bindData();
                //halfscreen_rendered_polygon.position.x = 0.0f;
                //halfscreen_rendered_polygon.position.y = 0.0f;
                //halfscreen_rendered_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
                //glBindTexture(GL_TEXTURE_2D, frameBufferHalf.texture);
                //halfscreen_rendered_polygon.rgba(1f,1f,1f, 1f);
                //halfscreen_rendered_polygon.draw();

                //frameBufferPolygon.bindData();
                //frameBufferPolygon.position.x = 0.0f;
                //frameBufferPolygon.position.y = 0.0f;
                //frameBufferPolygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
                //frameBufferPolygon.setTexture(frameBuffer.texture);
                //frameBufferPolygon.rgba(1f,1f,1f, 1f);
                ////frameBufferPolygon.draw();

                //frameBufferPolygon2.bindData();
                //frameBufferPolygon2.position.x = 0.5f;
                //frameBufferPolygon2.position.y = 0.0f;
                //frameBufferPolygon2.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
                //frameBufferPolygon2.setTexture(frameBufferHalf.texture);
                //frameBufferPolygon2.rgba(1f,1f,1f, 1f);
                ////frameBufferPolygon2.draw();

                //vrMaskPolygon.bindData();
                //vrMaskPolygon.position.x = 0.0f;
                //vrMaskPolygon.position.y = 0.0f;
                //vrMaskPolygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
                //vrMaskPolygon.setTexture(vrMaskTexture);
                //vrMaskPolygon.rgba(1f,1f,1f, 1f);
                //vrMaskPolygon.draw();

                for (int i = 0; i < Constants.MAX_FINGERS; i++) {
                    controller[0].checkForTouch(i);
                    controller[1].checkForTouch(i);
                }

                for (int i = 0; i < Constants.MAX_FINGERS; i++){
                    controller[0].runAnalogStick(i);
                    controller[1].runAnalogStick(i);
                }

                controller[0].draw();
                controller[1].draw();

                angle += 1f;
                angle %= 360f;

                sound.play(2);

                break;
            }
        }
    }

    private void loadObjects(){
        tesseract = null;
        tesseract = new Object3D(context, "Object3D: Tesseract", programTexturedDirectionalSpecularLit, R.raw.tesseract, new Vertex3D(0f, 0f, 0f), new Vector3D(0f, 0f, 0f), new Vertex3D(0f, 0f, -30f), new Vector3D(0f, 0f, 0f), new Vector3D(2f, 2f, 2f), 1f, 1f, 1f, 1f, 1f, false);
        tesseract.loadFile();
        tesseract.setup();

        int reflectionScaledDownQuality = 1;

        frameBufferPanel.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);

        frameBufferFront.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);
        frameBufferBack.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);
        frameBufferLeft.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);
        frameBufferRight.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);
        frameBufferUp.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);
        frameBufferDown.createFrameBuffer((int)camera[0].screenWidth / reflectionScaledDownQuality, (int)camera[0].screenHeight / reflectionScaledDownQuality);

        frameBuffer.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);
        depthBuffer.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);
        frameBufferHalf.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);

        frameBufferLeftEye.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);
        frameBufferRightEye.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);

        exposureToneMappingFrameBuffer.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);
        brightFilterFrameBuffer.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);
        horizontalBlurFrameBuffer.createFrameBuffer((int)camera[0].screenWidth / SCALEDOWN_FACTOR, (int)camera[0].screenHeight / SCALEDOWN_FACTOR);
        verticalBlurFrameBuffer.createFrameBuffer((int)camera[0].screenWidth / SCALEDOWN_FACTOR, (int)camera[0].screenHeight / SCALEDOWN_FACTOR);
        contrastBoostFrameBuffer.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);
        bloomFrameBuffer.createFrameBuffer((int)camera[0].screenWidth, (int)camera[0].screenHeight);

        final float WIDTH = 0.5f;
        final float HEIGHT = 1.0f;

        frameBufferPolygon = new Quad2D(context, "Quad2D: Framebuffer Polygon", programTextured,
                0f, 0f,
                WIDTH * camera[0].aspectRatio,  0f,
                0f, HEIGHT,
                WIDTH * camera[0].aspectRatio, HEIGHT,
                1f, 1f, 1f, 1f,
                1f, -1f);

        frameBufferPolygon2 = new Quad2D(context, "Quad2D: Framebuffer Polygon 2", programTextured,
                0f, 0f,
                WIDTH * camera[0].aspectRatio,  0f,
                0f, HEIGHT,
                WIDTH * camera[0].aspectRatio, HEIGHT,
                1f, 1f, 1f, 1f,
                1f, -1f);

        vrMaskPolygon = new Quad2D(context, "Quad2D: VR Mask Polygon", programTextured,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, 1f);

        fullscreen_rendered_polygon = new Quad2D(context, "Quad2D: Fullscreen Rendered Polygon", programTextured,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        exposure_tone_mapped_polygon = new Quad2D(context, "Quad2D: Exposure Tone Mapped Polygon", programExposureToneMapping,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        bright_filter_polygon = new Quad2D(context, "Quad2D: Bright Filter Polygon", programBrightFilter,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        horizontal_blur_buffer_polygon = new Quad2D(context, "Quad2D: Horizontal Blur Buffer Polygon", programHorizontalBlur,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        vertical_blur_buffer_polygon = new Quad2D(context, "Quad2D: Vertical Blur Buffer Polygon", programVerticalBlur,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        contrast_boost_polygon = new Quad2D(context, "Quad2D: Contrast Boost Polygon", programContrastBoost,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        halfscreen_rendered_polygon = new Quad2D(context, "Quad2D: Fullscreen Rendered Polygon", programTextured,
                0f, 0f,
                0.5f * camera[0].aspectRatio,  0f,
                0f, 0.5f,
                0.5f * camera[0].aspectRatio, 0.5f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        fullscreen_rendered_multitextured_polygon = new MultiTexture2D(context, programMultiTexture,
                0f, 0f,
                1.0f * camera[0].aspectRatio,  0f,
                0f, 1.0f,
                1.0f * camera[0].aspectRatio, 1.0f,
                1f, 1f, 1f, 1f,
                1f, -1f);

        // Cubemap Framebuffer Polygons x6 (Could be shrunk down to one if the T's and V's match ;)

        cubemap_polygon_front = new Quad2D(context, "Quad2D: Cubemap Polygon Front", programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        cubemap_polygon_back = new Quad2D(context, "Quad2D: Cubemap Polygon Back",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        cubemap_polygon_left = new Quad2D(context, "Quad2D: Cubemap Polygon Left",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        cubemap_polygon_right = new Quad2D(context, "Quad2D: Cubemap Polygon Right",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        cubemap_polygon_up = new Quad2D(context, "Quad2D: Cubemap Polygon Up",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        cubemap_polygon_down = new Quad2D(context, "Quad2D: Cubemap Polygon Down", programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        // Cubemap Framebuffer Polygons x6 (Could be shrunk down to one if the T's and V's match ;)

        reflection_cubemap_polygon_front = new Quad2D(context, "Quad2D: Cubemap Polygon Front", programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        reflection_cubemap_polygon_back = new Quad2D(context, "Quad2D: Cubemap Polygon Back",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        reflection_cubemap_polygon_left = new Quad2D(context, "Quad2D: Cubemap Polygon Left",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        reflection_cubemap_polygon_right = new Quad2D(context, "Quad2D: Cubemap Polygon Right",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        reflection_cubemap_polygon_up = new Quad2D(context, "Quad2D: Cubemap Polygon Up",programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        reflection_cubemap_polygon_down = new Quad2D(context, "Quad2D: Cubemap Polygon Down", programTextured,
                0f, 0f,
                1.0f,  0f,
                0f, 1.0f,
                1.0f, 1.0f,
                1f, 1f, 1f, 1f,
                -1f, -1f);

        openGLThread.threadID = thread.getId();
        openGLThread.loaded = false;
        //Log.d(TAG, "Thread ID: " + String.valueOf(openGLThread.threadID));

        if (thread.getState() == Thread.State.NEW){
            thread.start();
        }

        controller[0] = new AnalogController(context, (0.25f * 0.75f) / camera[0].aspectRatio, 1f - (0.25f * 0.75f), (0.25f * 0.75f), (0.125f * 0.75f));
        controller[1] = new AnalogController(context, 1f - (0.25f * 0.75f) / camera[0].aspectRatio, 1f - (0.25f * 0.75f), (0.25f * 0.75f), (0.125f * 0.75f));
        controller[0].setupTextures();
        controller[1].setupTextures();
    }

    private boolean Point_In_View_Frustum(Vertex3D position)
    {
        position.w = 1f;
        Plane3D[] p = new Plane3D[6];
        p[0] = new Plane3D(camera[0].ftl, camera[0].ftr, camera[0].fbl); //front
        p[1] = new Plane3D(camera[0].ftr, camera[0].ntr, camera[0].fbr); //right
        p[2] = new Plane3D(camera[0].ntr, camera[0].ntl, camera[0].nbr); //back
        p[3] = new Plane3D(camera[0].ntl, camera[0].ftl, camera[0].nbl); //left
        p[4] = new Plane3D(camera[0].ntl, camera[0].ntr, camera[0].ftl); //top
        p[5] = new Plane3D(camera[0].fbl, camera[0].fbr, camera[0].nbl); //bottom

        for (int i = 0; i < 6; i++)
        {
            if ((p[i].a * position.x + p[i].b * position.y + p[i].c * position.z + p[i].d * position.w) < 0.0f)
            {
                //Outside the frustum
                return false;
            }
        }

        return true;
    }

    private float Line_Up_Lens_Flare(float  Light, float Lens_Flare_Direction, int Lens_Flare_Number, int Number_of_Flares)
    {
        return (Light + ((1.0f / (Number_of_Flares - 1) * Lens_Flare_Number) * ((0.5f + Lens_Flare_Direction) - Light)));
    }

    private void render(Camera3D cam){
        if (camera != null) {
            glUseProgram(programTexturedDirectionalSpecularLit);

            //TODO: Go through the code with a fine tooth comb.
            //TODO: Remove any unnecessary comments
            //TODO: Remove any unnecessary code
            //TODO: Convert all math code to your math engine, matrices too
            //TODO: Once that is done, optimize everything, including shaders
            //TODO: Add a workable view frustum
            //TODO: Add view frustum culling
            //TODO: Add shadow mapping
            //TODO: Add collision code PLEASE!!! Necessary for game play.
            //TODO: Focus on game play. No game play means no game. You have a cluster fuck of experiments.
            //TODO: Find out how to draw multiple objects with one draw call
            //TODO: Is mipmapping working?
            //TODO: Fix lens flares. Compare to C++'s values and convert
            //TODO: Free up all sorts of memory from arrays and arraylists
            //TODO: Advanced: Depth of Field effect (aka aperture)
            //TODO: Advanced: Light Scattering (God rays)
            //TODO: Advanced: Dynamic Environment Mapping (broken)
            //TODO: Load objects using materials
            //TODO: Load objects using multiple textures
            //TODO: Support drawing text
            //TODO: Separate drawing each object in its own class so your render method doesn't have a million lines of code
            //TODO: Render class should only contain rendering that is primarily focused on your game, not experiments. Separate unused special effects please! (ie VR, split screen, panel, etc)
            //TODO: Each level should be its own file in binary format that lays out the bricks.
            //TODO: Knock off as much of this list as possible everyday. It will not be one shot!

            final float X_GAP = 1f;
            final float Y_GAP = 1f;
            final float Z_GAP = 1f;

            final float X_SIZE = 8f;
            final float Y_SIZE = 4f;
            final float Z_SIZE = 4f;

            light.enable(true);
            light.setPosition(-15f, 0f, 15f);
            light.ambient(0.5f, 0.5f, 0.5f);
            light.diffuse(1f, 1f, 1f);
            light.specular(1f, 1f, 1f);
            light.ambientEnable(true);
            light.diffuseEnable(true);
            light.specularEnable(true);
            light.specularIntensity(50f);
            light.setLightType(Light.TYPE.pointLight);

            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glFrontFace(GL_CCW);

            for (int z = 0; z < 1; z++) {
                for (int y = 0; y < 10; y++) {
                    for (int x = 0; x < 10; x++) {
                        if (brick[x][y][z] != null) {
                            if (y >= 0 && y < 3) {
                                brick[x][y][z].rgba(1.0f, 0.0f, 0.0f, 1.0f);
                            }
                            else if (y >= 3 && y < 6) {
                                brick[x][y][z].rgba(0.0f, 1.0f, 0.0f, 1.0f);
                            }
                            else if (y >= 6 && y < 10) {
                                brick[x][y][z].rgba(0.0f, 0.0f, 1.0f, 1.0f);
                            }

                            brick[x][y][z].reverseReflection = false;
                            brick[x][y][z].reflectiveCubeEnabled = true;
                            brick[x][y][z].setReflectiveness(1f);
                            brick[x][y][z].texture_enabled = false;
                            brick[x][y][z].cubemap_enabled = true;
                            brick[x][y][z].vertexBufferEnabled = true;
                            brick[x][y][z].local_position.x = ((X_SIZE + X_GAP) * x) - ((X_SIZE + X_GAP) * 10f * 0.5f);
                            brick[x][y][z].local_position.y = -((Y_SIZE + Y_GAP) * y) + ((Y_SIZE + Y_GAP) * 10f * 0.5f);
                            brick[x][y][z].local_position.z = -100 + ((Z_SIZE + Z_GAP) * z) - ((Z_SIZE + Z_GAP) * 2f * 0.5f);

                            brick[x][y][z].local_angle.x = 0f;
                            brick[x][y][z].local_angle.y = 0f;
                            brick[x][y][z].local_angle.z = 0f;
                            brick[x][y][z].world_position.x = 0f; //camera[0].world_position.x;
                            brick[x][y][z].world_position.y = 0f; //camera[0].world_position.y;
                            brick[x][y][z].world_position.z = 0f; //camera[0].world_position.z;
                            brick[x][y][z].world_angle.x = 0f;
                            brick[x][y][z].world_angle.y = 0f; //-camera[0].angle.y;
                            brick[x][y][z].world_angle.z = 0f;
                            brick[x][y][z].updateMatrices(cam);
                            brick[x][y][z].setTexture(stoneTexture);
                            brick[x][y][z].setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
                            brick[x][y][z].draw();
                        }
                    }
                }
            }

/*
            if (mario != null) {
                light.enable(true);
                light.setPosition(-15f, 0f, 15f);
                light.ambient(0.01f, 0.01f, 0.01f);
                light.diffuse(0.85f, 0.85f, 0.85f);
                light.specular(1f, 1f, 1f);
                light.ambientEnable(true);
                light.diffuseEnable(true);
                light.specularEnable(true);
                light.specularIntensity(1f);
                light.setLightType(Light.TYPE.pointLight);

                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);
                glFrontFace(GL_CCW);
                mario.reverseReflection = false;
                mario.reflectiveCubeEnabled = true;
                mario.setReflectiveness(1f);
                mario.texture_enabled = true;
                mario.cubemap_enabled = true;
                mario.vertexBufferEnabled = true;
                mario.local_position.x = 0f;
                mario.local_position.y = -10f;
                mario.local_position.z = -50f;
                mario.local_angle.x = 0;
                mario.local_angle.y = angle;
                mario.local_angle.z = 0f;
                mario.world_position.x = 0f;
                mario.world_position.y = 0f;
                mario.world_position.z = 0f;
                mario.world_angle.x = 0f;
                mario.world_angle.y = 0f;
                mario.world_angle.z = 0f;
                mario.scalar.x = 2f;
                mario.scalar.y = 2;
                mario.scalar.z = 2;
                mario.updateMatrices(cam);
                mario.setTexture(colorTexture[0]);
                mario.setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
                mario.rgba(1f, 1f, 1f, 1f);
                mario.draw();
            }
*/

            if (filmCamera != null) {
                light.enable(true);
                light.setPosition(-15f, 0f, 15f);
                light.ambient(0.05f, 0.05f, 0.05f);
                light.diffuse(0.5f, 0.5f, 0.5f);
                light.specular(1f, 1f, 1f);
                light.ambientEnable(true);
                light.diffuseEnable(true);
                light.specularEnable(true);
                light.specularIntensity(100f);
                light.setLightType(Light.TYPE.pointLight);

                glEnable(GL_DEPTH_TEST);
                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);
                glFrontFace(GL_CCW);
                filmCamera.reverseReflection = false;
                filmCamera.reflectiveCubeEnabled = true;
                filmCamera.setReflectiveness(1f);
                filmCamera.texture_enabled = true;
                filmCamera.cubemap_enabled = true;
                filmCamera.vertexBufferEnabled = true;
                filmCamera.world_position.x = otherCamera.world_position.x;
                filmCamera.world_position.y = otherCamera.world_position.y;
                filmCamera.world_position.z = otherCamera.world_position.z;
                filmCamera.local_angle.y = -otherCamera.angle.y;
                filmCamera.updateMatrices(cam);
                filmCamera.setTexture(metalBallTexture);
                filmCamera.setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
                filmCamera.rgba(1f, 1f, 1f, 1f);
                filmCamera.draw();
            }

            //The cube room level
            if (cube != null) {

                light.enable(true);
                light.setPosition(-15f, 0f, 15f);
                light.ambient(1f, 1f, 1f);
                light.diffuse(0f, 0f, 0f);
                light.specular(0f, 0f, 0f);
                light.ambientEnable(true);
                light.diffuseEnable(false);
                light.specularEnable(false);
                light.specularIntensity(20f);
                light.setLightType(Light.TYPE.pointLight);

                glEnable(GL_DEPTH_TEST);
                glDisable(GL_CULL_FACE);
                glCullFace(GL_FRONT);
                glFrontFace(GL_CCW);

                cube.texture_enabled = false;
                cube.cubemap_enabled = true;
                cube.vertexBufferEnabled = true;
                cube.updateMatrices(cam);
                cube.setTexture(cubeTexture); //Tron texture
                cube.setCubeMapTexture(cube.dynamic_cubemap_texture[0]);
                cube.rgba(1f, 1f, 1f, 1f);
                cube.draw();
            }

                                            /*if (wormhole != null) {
                                                light.enable(true);
                                                light.setPosition(-15f, 0f, 15f);
                                                light.specularEnable(true);
                                                light.setLightType(Light.TYPE.pointLight);
                                                glDisable(GL_CULL_FACE);
                                                glCullFace(GL_FRONT);
                                                glFrontFace(GL_CCW); // CW when inside. for now, this is for outside
                                                wormhole.twoSided = false;
                                                wormhole.texture_enabled = true;
                                                wormhole.vertexBufferEnabled = true;
                                                wormhole.updateMatrices(cam);
                                                wormhole.setTexture(wormholeTexture);
                                                wormhole.setCubeMapTexture(0);
                                                wormhole.rgba(1f, 1f, 1f, 1f);
                                                wormhole.draw();
                                            }*/

                                            /*light.enable(true);
                                            light.specularEnable(true);
                                            light.setLightType(Light.TYPE.pointLight);

                                            glEnable(GL_CULL_FACE);
                                            glCullFace(GL_BACK);
                                            glFrontFace(GL_CCW);

                                            ball.twoSided = false;
                                            ball.reverseReflection = false;
                                            ball.texture_enabled = true;
                                            ball.cubemap_enabled = false;
                                            ball.vertexBufferEnabled = true;
                                            ball.local_position.x = 0f;
                                            ball.local_position.y = 0f;
                                            ball.local_position.z = -75;
                                            ball.local_angle.x = 0f;
                                            ball.local_angle.y = 90f;
                                            ball.local_angle.z = 0f;
                                            ball.world_position.x = 0f; //camera[0].world_position.x;
                                            ball.world_position.y = 0f; //camera[0].world_position.y;
                                            ball.world_position.z = 0f; //camera[0].world_position.z;
                                            ball.world_angle.x = 0f;
                                            ball.world_angle.y = 0f; //-camera[0].angle.y;
                                            ball.world_angle.z = 0f;
                                            ball.updateMatrices(cam);
                                            ball.setTexture(metalBallTexture);
                                            ball.setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
                                            ball.rgba(1f, 1f, 1f, 1f);
                                            ball.draw();*/

            //Cube reflection sprite
            /*
            if (cubeReflection != null) {
                light.enable(true);
                light.setPosition(-15f, 0f, 15f);
                light.ambient(0.1f, 0.1f, 0.1f);
                light.diffuse(1f, 1f, 1f);
                light.specular(1f, 1f, 1f);
                light.ambientEnable(true);
                light.diffuseEnable(true);
                light.specularEnable(true);
                light.specularIntensity(20f);
                light.setLightType(Light.TYPE.pointLight);
                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);
                glFrontFace(GL_CCW);
                cubeReflection.twoSided = false;
                cubeReflection.reverseReflection = false;
                cubeReflection.reflectiveCubeEnabled = true;
                cubeReflection.setReflectiveness(1f);
                cubeReflection.texture_enabled = false;
                cubeReflection.cubemap_enabled = true;
                cubeReflection.vertexBufferEnabled = true;
                cubeReflection.local_position.x = 0f;
                cubeReflection.local_position.y = 5f;
                cubeReflection.local_position.z = -20f;
                cubeReflection.local_angle.x = 0f;
                cubeReflection.local_angle.y = 0f;//angle;
                cubeReflection.local_angle.z = 0f;
                cubeReflection.world_position.x = camera[0].world_position.x;
                cubeReflection.world_position.y = camera[0].world_position.y;
                cubeReflection.world_position.z = camera[0].world_position.z;
                cubeReflection.world_angle.x = 0f;
                cubeReflection.world_angle.y = -camera[0].angle.y;
                cubeReflection.world_angle.z = 0f;
                cubeReflection.scalar.x = 5f;
                cubeReflection.scalar.y = 5f;
                cubeReflection.scalar.z = 5f;
                cubeReflection.updateMatrices(viewableCamera);
                cubeReflection.setTexture(wormholeTexture);
                cubeReflection.setCubeMapTexture(cubeReflection.dynamic_cubemap_texture[0]);
                cubeReflection.rgba(1f, 1f, 1f, 1f);
                cubeReflection.draw();
            }
             */

            light.enable(false);
            light.ambientEnable(false);
            light.diffuseEnable(false);
            light.specularEnable(false);
            light.setLightType(Light.TYPE.pointLight);

            //glUseProgram(programTextured);

            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);

            Vector3D light3D = new Vector3D();
            Vector2D screenLight = new Vector2D();
            light3D.x = light.position.x;
            light3D.y = light.position.y;
            light3D.z = light.position.z;
            light3D.w = 1f;

            float modelX = light.modelMatrix[0] * light3D.x + light.modelMatrix[4] * light3D.y + light.modelMatrix[8] * light3D.z + light.modelMatrix[12] * light3D.w;
            float modelY = light.modelMatrix[1] * light3D.x + light.modelMatrix[5] * light3D.y + light.modelMatrix[9] * light3D.z + light.modelMatrix[13] * light3D.w;
            float modelZ = light.modelMatrix[2] * light3D.x + light.modelMatrix[6] * light3D.y + light.modelMatrix[10] * light3D.z + light.modelMatrix[14] * light3D.w;
            float modelW = light.modelMatrix[3] * light3D.x + light.modelMatrix[7] * light3D.y + light.modelMatrix[11] * light3D.z + light.modelMatrix[15] * light3D.w;

            light3D.x = modelX;
            light3D.y = modelY;
            light3D.z = modelZ;
            light3D.w = modelW;

            float projectX = light.mvpMatrix[0] * light3D.x + light.mvpMatrix[4] * light3D.y + light.mvpMatrix[8] * light3D.z + light.mvpMatrix[12] * light3D.w;
            float projectY = light.mvpMatrix[1] * light3D.x + light.mvpMatrix[5] * light3D.y + light.mvpMatrix[9] * light3D.z + light.mvpMatrix[13] * light3D.w;
            float projectZ = light.mvpMatrix[2] * light3D.x + light.mvpMatrix[6] * light3D.y + light.mvpMatrix[10] * light3D.z + light.mvpMatrix[14] * light3D.w;
            float projectW = light.mvpMatrix[3] * light3D.x + light.mvpMatrix[7] * light3D.y + light.mvpMatrix[11] * light3D.z + light.mvpMatrix[15] * light3D.w;

            light3D.x = projectX;
            light3D.y = projectY;
            light3D.z = projectZ;
            light3D.w = projectW;

            light3D.x /= light3D.w;
            light3D.y /= light3D.w;
            light3D.z /= light3D.w;

            light3D.x *= 0.5f + 0.5f;
            light3D.y *= 0.5f + 0.5f;
            light3D.z *= 0.5f + 0.5f;

            screenLight.x = light3D.x;
            screenLight.y = light3D.y;

            //int[] viewport = new int[4];

            //glGetIntegerv (GL_VIEWPORT, viewport, 0);

            //screenLight.x *= (viewport[2] + viewport[0]);
            //screenLight.y *= (viewport[3] + viewport[1]);

            //screenLight.x /= MainActivity.width;
            //screenLight.y /= MainActivity.height;

            //Log.d("VIEWPORT", viewport[0] + ", " + viewport[1] + ", " + viewport[2] + ", " + viewport[3]);
            //Log.d("POSITION", screenLight.x + ", " + screenLight.y);
            //Log.d("FPS", "0");

            /*
            //if (Point_In_View_Frustum(new Vertex3D(projectX, projectY, projectZ))){
                for (int i = 0; i < numberOfFlares; i++) {
                    lensFlare[i].texture_enabled = true;
                    lensFlare[i].position.x = Line_Up_Lens_Flare(screenLight.x, 0.5f + -screenLight.x, i, 6);
                    lensFlare[i].position.y = Line_Up_Lens_Flare(-screenLight.y, 0.5f + screenLight.y, i, 6);
                    lensFlare[i].bindData();
                    lensFlare[i].updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
                    lensFlare[i].setTexture(lensFlareTexture[i]);
                    lensFlare[i].rgba(1f, 1f, 1f, 0.75f);
                    lensFlare[i].draw();
                }
            //}

             */


                                            /*glEnable(GL_CULL_FACE);
                                            glCullFace(GL_BACK);
                                            glFrontFace(GL_CCW);

                                            ship[1].twoSided = false;
                                            ship[1].reverseReflection = false;
                                            ship[1].reflectiveCubeEnabled = true;
                                            ship[1].setReflectiveness(1f);
                                            ship[1].texture_enabled = true;
                                            ship[1].cubemap_enabled = true;
                                            ship[1].vertexBufferEnabled = true;
                                            ship[1].local_position.x = 4.0f;
                                            ship[1].local_position.y = 0f;
                                            ship[1].local_position.z = -20;
                                            ship[1].local_angle.x = 0f;
                                            ship[1].local_angle.y = 0f;
                                            ship[1].local_angle.z = 0f;
                                            ship[1].world_position.x = camera[0].world_position.x;
                                            ship[1].world_position.y = camera[0].world_position.y;
                                            ship[1].world_position.z = camera[0].world_position.z;
                                            ship[1].world_angle.x = 0f;
                                            ship[1].world_angle.y = -camera[0].angle.y;
                                            ship[1].world_angle.z = 0f;
                                            ship[1].scalar.x = 1f;
                                            ship[1].scalar.y = 1.1f;
                                            ship[1].scalar.z = 1.1f;
                                            ship[1].updateMatrices(cam);
                                            ship[1].setTexture(shipTexture2);
                                            ship[1].setCubeMapTexture(cubeReflection.dynamic_cubemap_texture[0]);
                                            ship[1].rgba(1f, 1f, 1f, 1f);
                                            ship[1].draw();*/

                                            /*glEnable(GL_CULL_FACE);
                                            glFrontFace(GL_CCW);
                                            ball.twoSided = false;
                                            ball.texture_enabled = false;
                                            ball.vertexBufferEnabled = true;
                                            ballRadius[0] = 3f;
                                            ballRadius[1] = 1f;
                                            ballRadius[2] = 1f;
                                            ball.updateMatrices(viewableCamera, 0f, 0f, 0f, 0f, ballRadius[0], ballRadius[1], ballRadius[2]);
                                            ball.setTexture(0);
                                            ball.setCubeMapTexture(0);

                                            if (MainActivity.heldDown[0]) {
                                                if (!controller[0].touched[0] && !controller[1].touched[0]) {
                                                    pickOrigin[0] = camera[0].world_position.x;
                                                    pickOrigin[1] = camera[0].world_position.y;
                                                    pickOrigin[2] = camera[0].world_position.z;
                                                    pickPos[0] = ball.position.x;
                                                    pickPos[1] = ball.position.y;
                                                    pickPos[2] = ball.position.z;
                                                    touchOverLap = Picking.getRayEllipsoidIntersection(pickOrigin, pickPos, rayDirection, ballRadius);
                                                    touchInRange = Picking.intersectionInRangeEllipsoid(camera[0], 0f, raySize, rayDirection, pickPos, ballRadius);
                                                } else {
                                                    touchOverLap = false;
                                                    touchInRange = false;
                                                }
                                            }

                                            if (touchInRange) {
                                                if (touchOverLap)
                                                    ball.rgba(0f, 1f, 0f, 1f);
                                                else
                                                    ball.rgba(0f, 0f, 1f, 1f);
                                            } else
                                                ball.rgba(1f, 1f, 1f, 1f);

                                            touchOverLap = false;
                                            touchInRange = false;

                                            ball.draw();

                                            glEnable(GL_CULL_FACE);
                                            glFrontFace(GL_CCW);
                                            ball2.twoSided = false;
                                            ball2.texture_enabled = false;
                                            ball2.vertexBufferEnabled = true;
                                            ball2.updateMatrices(viewableCamera, 0f, 0f, 0f, 0f, 3f, 3f, 3f);
                                            ball2.setTexture(0);
                                            ball2.setCubeMapTexture(0);

                                            if (MainActivity.heldDown[0]) {
                                                if (!controller[0].touched[0] && !controller[1].touched[0]) {
                                                    pickOrigin[0] = camera[0].world_position.x;
                                                    pickOrigin[1] = camera[0].world_position.y;
                                                    pickOrigin[2] = camera[0].world_position.z;
                                                    pickPos[0] = ball2.position.x;
                                                    pickPos[1] = ball2.position.y;
                                                    pickPos[2] = ball2.position.z;
                                                    touchOverLap = Picking.getRaySphereIntersection(pickOrigin, pickPos, rayDirection, 3f);
                                                    touchInRange = Picking.intersectionInRangeSphere(camera[0], 0f, raySize, rayDirection, pickPos, 3f);
                                                } else {
                                                    touchOverLap = false;
                                                    touchInRange = false;
                                                }
                                            }

                                            if (touchInRange) {
                                                if (touchOverLap)
                                                    ball2.rgba(0f, 1f, 0f, 1f);
                                                else
                                                    ball2.rgba(0f, 0f, 1f, 1f);
                                            } else
                                                ball2.rgba(1f, 1f, 1f, 1f);

                                            touchOverLap = false;
                                            touchInRange = false;

                                            ball2.draw();*/


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                            /*glUseProgram(programSimple);

                                            if (simpleCube != null) {
                                                simpleLight.enable(true);
                                                simpleLight.ambientEnable(true);
                                                simpleLight.diffuseEnable(true);
                                                //light.specularEnable(false);
                                                simpleLight.ambient(1f, 1f, 1f);
                                                simpleLight.diffuse(1f,1f, 1f);
                                                //light.specular(1f, 1f, 1f);
                                                simpleLight.setLightType(Light.TYPE.pointLight);

                                                glDisable(GL_CULL_FACE);
                                                glCullFace(GL_FRONT);
                                                glFrontFace(GL_CCW);

                                                simpleCube.twoSided = false;
                                                simpleCube.texture_enabled = true;
                                                simpleCube.world_position.x = camera[0].world_position.x;
                                                simpleCube.world_position.y = camera[0].world_position.y;
                                                simpleCube.world_position.z = camera[0].world_position.z;
                                                simpleCube.local_position.x = 0f;
                                                simpleCube.local_position.y = 0f;
                                                simpleCube.local_position.z = -20f;
                                                simpleCube.world_angle.y = -camera[0].angle.y;
                                                simpleCube.updateMatrices(cam);
                                                simpleCube.setTexture(0);
                                                //simpleCube.setTexture(simpleCube.dynamic_cubemap_texture[0]);
                                                simpleCube.rgba(1f, 1f, 1f, 1f);
                                                simpleCube.draw();
                                                simpleLight.enable(false);
                                            }*/

                                            /*glDisable(GL_CULL_FACE);
                                            glCullFace(GL_FRONT);
                                            glFrontFace(GL_CCW);

                                            viewFrustumPolygon[0].twoSided = false;
                                            viewFrustumPolygon[0].texture_enabled = false;
                                            viewFrustumPolygon[0].world_position.x = camera[0].world_position.x;
                                            viewFrustumPolygon[0].world_position.y = camera[0].world_position.y;
                                            viewFrustumPolygon[0].world_position.z = camera[0].world_position.z;
                                            viewFrustumPolygon[0].world_angle.y = -camera[0].angle.y;
                                            viewFrustumPolygon[0].updateMatrices(cam);
                                            viewFrustumPolygon[0].setTexture(0);
                                            viewFrustumPolygon[0].rgba(1f, 0f, 0f, 0.15f);
                                            viewFrustumPolygon[0].draw();

                                            viewFrustumPolygon[1].twoSided = false;
                                            viewFrustumPolygon[1].texture_enabled = false;
                                            viewFrustumPolygon[1].world_position.x = camera[0].world_position.x;
                                            viewFrustumPolygon[1].world_position.y = camera[0].world_position.y;
                                            viewFrustumPolygon[1].world_position.z = camera[0].world_position.z;
                                            viewFrustumPolygon[1].world_angle.y = -camera[0].angle.y;
                                            viewFrustumPolygon[1].updateMatrices(cam);
                                            viewFrustumPolygon[1].setTexture(0);
                                            viewFrustumPolygon[1].rgba(0f, 1f, 0f, 0.15f);
                                            viewFrustumPolygon[1].draw();

                                            viewFrustumPolygon[2].twoSided = false;
                                            viewFrustumPolygon[2].texture_enabled = false;
                                            viewFrustumPolygon[2].world_position.x = camera[0].world_position.x;
                                            viewFrustumPolygon[2].world_position.y = camera[0].world_position.y;
                                            viewFrustumPolygon[2].world_position.z = camera[0].world_position.z;
                                            viewFrustumPolygon[2].world_angle.y = -camera[0].angle.y;
                                            viewFrustumPolygon[2].updateMatrices(cam);
                                            viewFrustumPolygon[2].setTexture(0);
                                            viewFrustumPolygon[2].rgba(0f, 0f, 1f, 0.15f);
                                            viewFrustumPolygon[2].draw();

                                            viewFrustumPolygon[3].twoSided = false;
                                            viewFrustumPolygon[3].texture_enabled = false;
                                            viewFrustumPolygon[3].world_position.x = camera[0].world_position.x;
                                            viewFrustumPolygon[3].world_position.y = camera[0].world_position.y;
                                            viewFrustumPolygon[3].world_position.z = camera[0].world_position.z;
                                            viewFrustumPolygon[3].world_angle.y = -camera[0].angle.y;
                                            viewFrustumPolygon[3].updateMatrices(cam);
                                            viewFrustumPolygon[3].setTexture(0);
                                            viewFrustumPolygon[3].rgba(1f, 0f, 1f, 0.15f);
                                            viewFrustumPolygon[3].draw();

                                            viewFrustumPolygon[4].twoSided = false;
                                            viewFrustumPolygon[4].texture_enabled = false;
                                            viewFrustumPolygon[4].world_position.x = camera[0].world_position.x;
                                            viewFrustumPolygon[4].world_position.y = camera[0].world_position.y;
                                            viewFrustumPolygon[4].world_position.z = camera[0].world_position.z;
                                            viewFrustumPolygon[4].world_angle.y = -camera[0].angle.y;
                                            viewFrustumPolygon[4].updateMatrices(cam);
                                            viewFrustumPolygon[4].setTexture(0);
                                            viewFrustumPolygon[4].rgba(1f, 1f, 0f, 0.15f);
                                            viewFrustumPolygon[4].draw();

                                            viewFrustumPolygon[5].twoSided = false;
                                            viewFrustumPolygon[5].texture_enabled = false;
                                            viewFrustumPolygon[5].world_position.x = camera[0].world_position.x;
                                            viewFrustumPolygon[5].world_position.y = camera[0].world_position.y;
                                            viewFrustumPolygon[5].world_position.z = camera[0].world_position.z;
                                            viewFrustumPolygon[5].world_angle.y = -camera[0].angle.y;
                                            viewFrustumPolygon[5].updateMatrices(cam);
                                            viewFrustumPolygon[5].setTexture(0);
                                            viewFrustumPolygon[5].rgba(0f, 1f, 1f, 0.15f);
                                            viewFrustumPolygon[5].draw();*/

                                            //Log.d("LINE A", String.valueOf(rayStart[0]) + ", " + String.valueOf(rayStart[1]) + ", " + String.valueOf(rayStart[2]));
                                            //Log.d("LINE B", String.valueOf(rayEnd[0]) + ", " + String.valueOf(rayEnd[1]) + ", " + String.valueOf(rayEnd[2]));

                                            /*line = new Line3D(context, programSimple, rayStart[0], rayStart[1], rayStart[2], 1f,
                                                    rayEnd[0], rayEnd[1],  rayEnd[2], 1f,
                                                    0f, 0f, 1f, 1f);
                                            line.updateMatrices(viewableCamera, 0f, 0f, 0f,
                                                    0, 0f, 0f, 0f,
                                                    1f, 1f, 1f);
                                            line.rgba(0f, 0f, 1f, 1f);
                                            line.draw(5);*/

            glUseProgram(programParticle);
                                            /*particle.updateMatrices(cam,
                                                    otherCamera.world_position.x, otherCamera.world_position.y, otherCamera.world_position.z,
                                                    0f, 0f, 0f, 0f);
                                            particle.rgba(1f, 0f, 0f, 1f);
                                            particle.draw();*/

            particle.world_position.x = camera[0].world_position.x;
            particle.world_position.y = camera[0].world_position.y;
            particle.world_position.z = camera[0].world_position.z;
            particle.updateMatrices(cam);
            particle.rgba(1f, 0f, 0f, 1f);
            particle.draw();

            particlePointLight.world_position.x = light.position.x;
            particlePointLight.world_position.y = light.position.y;
            particlePointLight.world_position.z = light.position.z;
            particlePointLight.updateMatrices(cam);
            particlePointLight.rgba(1f, 1f, 1f, 1f);
            particlePointLight.draw();

            /////////////////////////////////////////////////////////////////

                                            /*particleCam[0].world_position.x = camera[0].nc.x;
                                            particleCam[0].world_position.y = camera[0].nc.y;
                                            particleCam[0].world_position.z = camera[0].nc.z;
                                            particleCam[0].updateMatrices(cam);
                                            particleCam[0].rgba(0f, 1f, 0f, 1f);
                                            particleCam[0].draw();

                                            particleCam[1].world_position.x = camera[0].fc.x;
                                            particleCam[1].world_position.y = camera[0].fc.y;
                                            particleCam[1].world_position.z = camera[0].fc.z;
                                            particleCam[1].updateMatrices(cam);
                                            particleCam[1].rgba(1f, 0f, 1f, 1f);
                                            particleCam[1].draw();

                                            particleCam[2].world_position.x = camera[0].ftl.x;
                                            particleCam[2].world_position.y = camera[0].ftl.y;
                                            particleCam[2].world_position.z = camera[0].ftl.z;
                                            particleCam[2].updateMatrices(cam);
                                            particleCam[2].rgba(0f, 0f, 1f, 1f);
                                            particleCam[2].draw();

                                            particleCam[3].world_position.x = camera[0].ftr.x;
                                            particleCam[3].world_position.y = camera[0].ftr.y;
                                            particleCam[3].world_position.z = camera[0].ftr.z;
                                            particleCam[3].updateMatrices(cam);
                                            particleCam[3].rgba(0f, 0f, 1f, 1f);
                                            particleCam[3].draw();

                                            particleCam[4].world_position.x = camera[0].fbl.x;
                                            particleCam[4].world_position.y = camera[0].fbl.y;
                                            particleCam[4].world_position.z = camera[0].fbl.z;
                                            particleCam[4].updateMatrices(cam);
                                            particleCam[4].rgba(0f, 0f, 1f, 1f);
                                            particleCam[4].draw();

                                            particleCam[5].world_position.x = camera[0].fbr.x;
                                            particleCam[5].world_position.y = camera[0].fbr.y;
                                            particleCam[5].world_position.z = camera[0].fbr.z;
                                            particleCam[5].updateMatrices(cam);
                                            particleCam[5].rgba(0f, 0f, 1f, 1f);
                                            particleCam[5].draw();

                                            particleCam[6].world_position.x = camera[0].ntl.x;
                                            particleCam[6].world_position.y = camera[0].ntl.y;
                                            particleCam[6].world_position.z = camera[0].ntl.z;
                                            particleCam[6].updateMatrices(cam);
                                            particleCam[6].rgba(0f, 0f, 1f, 1f);
                                            particleCam[6].draw();

                                            particleCam[7].world_position.x = camera[0].ntr.x;
                                            particleCam[7].world_position.y = camera[0].ntr.y;
                                            particleCam[7].world_position.z = camera[0].ntr.z;
                                            particleCam[7].updateMatrices(cam);
                                            particleCam[7].rgba(0f, 0f, 1f, 1f);
                                            particleCam[7].draw();

                                            particleCam[8].world_position.x = camera[0].nbl.x;
                                            particleCam[8].world_position.y = camera[0].nbl.y;
                                            particleCam[8].world_position.z = camera[0].nbl.z;
                                            particleCam[8].updateMatrices(cam);
                                            particleCam[8].rgba(0f, 0f, 1f, 1f);
                                            particleCam[8].draw();

                                            particleCam[9].world_position.x = camera[0].nbr.x;
                                            particleCam[9].world_position.y = camera[0].nbr.y;
                                            particleCam[9].world_position.z = camera[0].nbr.z;
                                            particleCam[9].updateMatrices(cam);
                                            particleCam[9].rgba(0f, 0f, 1f, 1f);
                                            particleCam[9].draw();*/

            //Log.d("sign distance", String.valueOf(camera[0].viewFrustum[0].signedDistanceTo(light.position)));

            //Log.d("camera[0] world pos", String.valueOf(camera[0].world_position.x) + ", " + String.valueOf(camera[0].world_position.y) + ", " + String.valueOf(camera[0].world_position.z));
            //Log.d("NC", String.valueOf(camera[0].nc.x) + ", " + String.valueOf(camera[0].nc.y) + ", " + String.valueOf(camera[0].nc.z));
            //Log.d("FC", String.valueOf(camera[0].fc.x) + ", " + String.valueOf(camera[0].fc.y) + ", " + String.valueOf(camera[0].fc.z));

            /*
            for (int i = 0; i < 6; i++) {
                collidablePoint[i] = ellipsoid.getTriangleIntersectionPoint(vector_space_pa[i], vector_space_pb[i], vector_space_pc[i]);

                collidablePoint[i].x *= (ellipsoid.radius.x + ellipsoid.gap.x);
                collidablePoint[i].y *= (ellipsoid.radius.y + ellipsoid.gap.y);
                collidablePoint[i].z *= (ellipsoid.radius.z + ellipsoid.gap.z);
            }
             */
                //collidablePointParticle[i].updateMatrices(cam,
                //        collidablePoint[i].x, collidablePoint[i].y, collidablePoint[i].z,
                //        0f, 0f, 0f, 0f);
                //collidablePointParticle[i].rgba(1f, 1f, 1f, 1f);
                //collidablePointParticle[i].draw();
            //}

        }
    }

    private void renderSeperate(Camera3D cam){
        glUseProgram(programTexturedDirectionalSpecularLit);

        if(ship[0] != null){
            light.enable(true);
            light.setPosition(-15f, 0f, 15f);
            light.ambient(0.05f, 0.05f, 0.05f);
            light.diffuse(0.85f, 0.85f, 0.85f);
            light.specular(1f, 1f, 1f);
            light.ambientEnable(true);
            light.diffuseEnable(true);
            light.specularEnable(true);
            light.specularIntensity(1f);
            light.setLightType(Light.TYPE.pointLight);

            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glFrontFace(GL_CCW);

            ship[0].reverseReflection = false;
            ship[0].reflectiveCubeEnabled = true;
            ship[0].setReflectiveness(1f);
            ship[0].texture_enabled = true;
            ship[0].cubemap_enabled = true;
            ship[0].vertexBufferEnabled = true;
            ship[0].local_position.x = 0f;
            ship[0].local_position.y = 0f;
            ship[0].local_position.z = -20;
            ship[0].local_angle.x = 0f;
            ship[0].local_angle.y = 0f;
            ship[0].local_angle.z = 0f;
            ship[0].world_position.x = camera[0].world_position.x;
            ship[0].world_position.y = camera[0].world_position.y;
            ship[0].world_position.z = camera[0].world_position.z;
            ship[0].world_angle.x = 0f;
            ship[0].world_angle.y = -camera[0].angle.y;
            ship[0].world_angle.z = 0f;
            ship[0].scalar.x = 1f;
            ship[0].scalar.y = 1f;
            ship[0].scalar.z = 1f;
            ship[0].updateMatrices(cam);
            ship[0].setTexture(shipTexture);
            ship[0].setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
            ship[0].rgba(1f, 1f, 1f, 1f);
            ship[0].draw();
        }

        if(ship[1] != null){
            light.enable(true);
            light.setPosition(-15f, 0f, 15f);
            light.ambient(0.05f, 0.05f, 0.05f);
            light.diffuse(0.85f, 0.85f, 0.85f);
            light.specular(1f, 1f, 1f);
            light.ambientEnable(true);
            light.diffuseEnable(true);
            light.specularEnable(true);
            light.specularIntensity(1f);
            light.setLightType(Light.TYPE.pointLight);
            light.updateMatrices(cam);

            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glFrontFace(GL_CCW);

            ship[1].reverseReflection = false;
            ship[1].reflectiveCubeEnabled = true;
            ship[1].setReflectiveness(1f);
            ship[1].texture_enabled = true;
            ship[1].cubemap_enabled = true;
            ship[1].vertexBufferEnabled = true;
            ship[1].local_position.x = 0f;
            ship[1].local_position.y = 0f;
            ship[1].local_position.z = -20;
            ship[1].local_angle.x = 0f;
            ship[1].local_angle.y = 0f;
            ship[1].local_angle.z = 0f;
            ship[1].world_position.x = camera[0].world_position.x;
            ship[1].world_position.y = camera[0].world_position.y;
            ship[1].world_position.z = camera[0].world_position.z;
            ship[1].world_angle.x = 0f;
            ship[1].world_angle.y = -camera[0].angle.y;
            ship[1].world_angle.z = 0f;
            ship[1].scalar.x = 1f;
            ship[1].scalar.y = 1.1f;
            ship[1].scalar.z = 1.1f;
            ship[1].updateMatrices(cam);
            ship[1].setTexture(shipTexture2);
            ship[1].setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
            ship[1].rgba(1f, 1f, 1f, 1f);
            ship[1].draw();
        }

        /*
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //THE BIG BALL
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        light.enable(false);
        light.setPosition(-15f, 0f, 15f);
        light.ambient(0.05f, 0.05f, 0.05f);
        light.diffuse(0.85f, 0.85f, 0.85f);
        light.specular(1f, 1f, 1f);
        light.ambientEnable(true);
        light.diffuseEnable(true);
        light.specularEnable(true);
        light.specularIntensity(1f);
        light.setLightType(Light.TYPE.pointLight);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        ball.twoSided = false;
        ball.reverseReflection = false;
        ball.reflectiveCubeEnabled = true;
        ball.setReflectiveness(0.25f);
        ball.texture_enabled = true;
        ball.cubemap_enabled = true;
        ball.texture2_enabled = false;
        ball.invertNormals = false;
        ball.invertXNormal = false;
        ball.invertYNormal = false;
        ball.invertZNormal = false;
        ball.vertexBufferEnabled = true;
        ball.local_position.x = 0f;
        ball.local_position.y = -5f;
        ball.local_position.z = -20f;
        ball.local_angle.x = 0f;
        ball.local_angle.y = 0f;
        ball.local_angle.z = 0f;
        ball.world_position.x = camera[0].world_position.x; //+= ball.velocity.x;
        ball.world_position.y = camera[0].world_position.y; //+= ball.velocity.y;
        ball.world_position.z = camera[0].world_position.z; //+= ball.velocity.z;
        ball.world_angle.x = 0f;
        ball.world_angle.y = -camera[0].angle.y;
        ball.world_angle.z = 0f;
        ball.scalar.x = 5f;
        ball.scalar.y = 5f;
        ball.scalar.z = 5f;

        Log.d("Old Position", String.valueOf(ball.old_world_position.x) + ", " + String.valueOf(ball.old_world_position.y) + ", " + String.valueOf(ball.old_world_position.z));
        Log.d("Position", String.valueOf(ball.world_position.x) + ", " + String.valueOf(ball.world_position.y) + ", " + String.valueOf(ball.world_position.z));

        //ball.velocity.x = ball.world_position.x - ball.old_world_position.x;
        //ball.velocity.y = ball.world_position.y - ball.old_world_position.y;
        //ball.velocity.z = ball.world_position.z - ball.old_world_position.z;

        /////////////////////////////////////////////
        //COLLISION CODE
        /////////////////////////////////////////////
                    ball.getTransformedPosition();

                    ellipsoid.position = new Vertex3D(ball.transformed_position.x, ball.transformed_position.y, ball.transformed_position.z);
                    ellipsoid.create_vector_space();

                    vectorSpaceSide[0] = new Plane3D(vector_space_pa[0], vector_space_pb[0], vector_space_pc[0]);
                    vectorSpaceSide[1] = new Plane3D(vector_space_pa[1], vector_space_pb[1], vector_space_pc[1]);
                    vectorSpaceSide[2] = new Plane3D(vector_space_pa[2], vector_space_pb[2], vector_space_pc[2]);
                    vectorSpaceSide[3] = new Plane3D(vector_space_pa[3], vector_space_pb[3], vector_space_pc[3]);
                    vectorSpaceSide[4] = new Plane3D(vector_space_pa[4], vector_space_pb[4], vector_space_pc[4]);
                    vectorSpaceSide[5] = new Plane3D(vector_space_pa[5], vector_space_pb[5], vector_space_pc[5]);

                    collisionResult[0] = ellipsoid.toQuadCollision(vector_space_pa[0], vector_space_pb[0], vector_space_pc[0], vector_space_pd[0]);
                    collisionResult[1] = ellipsoid.toQuadCollision(vector_space_pa[1], vector_space_pb[1], vector_space_pc[1], vector_space_pd[1]);
                    collisionResult[2] = ellipsoid.toQuadCollision(vector_space_pa[2], vector_space_pb[2], vector_space_pc[2], vector_space_pd[2]);
                    collisionResult[3] = ellipsoid.toQuadCollision(vector_space_pa[3], vector_space_pb[3], vector_space_pc[3], vector_space_pd[3]);
                    collisionResult[4] = ellipsoid.toQuadCollision(vector_space_pa[4], vector_space_pb[4], vector_space_pc[4], vector_space_pd[4]);
                    collisionResult[5] = ellipsoid.toQuadCollision(vector_space_pa[5], vector_space_pb[5], vector_space_pc[5], vector_space_pd[5]);

                    for (int i = 0; i < 6; i++) {
                        //Log.d("Collided: " + String.valueOf(i), String.valueOf(collisionResult[i]));

                        if (collisionResult[i] == true) {
                            ball.world_position = ellipsoid.toQuadCollisionResponse(ellipsoid.vector_space_position, ellipsoid.vector_space_old_position, vector_space_pa[i], vector_space_pb[i], vector_space_pc[i], vector_space_pd[i]);
                            ball.world_position.x *= (ellipsoid.radius.x + ellipsoid.gap.x);
                            ball.world_position.y *= (ellipsoid.radius.y + ellipsoid.gap.y);
                            ball.world_position.z *= (ellipsoid.radius.z + ellipsoid.gap.z);

                            Log.d("New Position: " + String.valueOf(i), String.valueOf(ball.world_position.x) + ", " + String.valueOf(ball.world_position.y) + ", " + String.valueOf(ball.world_position.z));

                            Vector3D reflect = new Vector3D();
                            Vector3D I = new Vector3D(ball.velocity.x, ball.velocity.y, ball.velocity.z);
                            float dot = (vectorSpaceSide[i].normal.x * I.x + vectorSpaceSide[i].normal.y * I.y + vectorSpaceSide[i].normal.z * I.z);

                            //reflect(I, N) = I - 2.0 * dot(N, I) * N

                            Log.d("", "REFLECT!!!");

                            reflect.x = I.x - 2f * dot * vectorSpaceSide[i].normal.x;
                            reflect.y = I.y - 2f * dot * vectorSpaceSide[i].normal.y;
                            reflect.z = I.z - 2f * dot * vectorSpaceSide[i].normal.z;

                            Log.d("Reflect", String.valueOf(reflect.x) + ", " + String.valueOf(reflect.y) + ", " + String.valueOf(reflect.z));

                            //Log.d("Reflect: " + String.valueOf(i), String.valueOf(reflect.x) + ", " + String.valueOf(reflect.y) + ", " + String.valueOf(reflect.z));

                            ball.velocity.x = reflect.x;
                            ball.velocity.y = reflect.y;
                            ball.velocity.z = reflect.z;


                            //camera[0].world_position.x = ball.world_position.x + ball.local_position.x;
                            //camera[0].world_position.y = ball.world_position.y + ball.local_position.y;
                            //camera[0].world_position.z = ball.world_position.z + ball.local_position.z;
                        }
                    }

        /////////////////////////////////////////////

        ball.updateMatrices(cam);
        ball.setTexture(metalBallTexture);
        ball.setCubeMapTexture(ball.dynamic_cubemap_texture[0]);
        ball.rgba(1f, 1f, 1f, 1f);
        ball.draw();
        */
    }

    private void renderToFrameBufferLeftEye() {
        frameBufferLeftEye.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        // Don't change the angle.
        // See if you can change the field of view instead
        // Because this will not work in all angles!
        // FOV should theoretically work
        // ...
        // FUCK ME it didnt >.<

        ////camera[0].angle.y -= (((float)Math.tan(((double)camera[0].fov / 2.0) * Math.PI / 180.0)) * (180.0f / (float)Math.PI)) / 2f;//(45 * (float)Math.PI / 180.0f);
        //camera[0].fov -= camera[0].fov / 2f;
        //float size = camera[0].nearZ * (float)Math.tan((double)camera[0].fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(camera[0].projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, camera[0].nearZ, camera[0].farZ);

        viewableCamera.offset_position.x = -4.0f;
        viewableCamera.offset_position.y = 0f;
        viewableCamera.offset_position.z = 0f;
        viewableCamera.update();

        render(viewableCamera);
        frameBufferLeftEye.unbind();
    }

    private void renderToFrameBufferRightEye() {
        frameBufferRightEye.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        // Don't change the angle.
        // See if you can change the field of view instead
        // Because this will not work in all angles!
        // FOV should theoretically work
        // ...
        // FUCK ME it didnt >.<

        ////camera[0].angle.y += (((2.0f * (float)Math.tan(((double)camera[0].fov / 2.0) * Math.PI / 180.0))) * (180.0f / (float)Math.PI)) / 2f ;
        //camera[0].fov += 2f * camera[0].fov / 2f;
        //float size = camera[0].nearZ * (float)Math.tan((double)camera[0].fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(camera[0].projectionMatrix, 0, -size * camera[0].aspectRatio, size * camera[0].aspectRatio, -size, size, camera[0].nearZ, camera[0].farZ);

        viewableCamera.offset_position.x = 4.0f;
        viewableCamera.offset_position.y = 0f;
        viewableCamera.offset_position.z = 0f;
        viewableCamera.update();

        render(viewableCamera);
        frameBufferRightEye.unbind();
    }

    private void renderToFrameBufferFront(Object3D object) {
        frameBufferFront.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        //  Camera Projection Matrix
        //cubeCamera.fov = 45f;
        //cubeCamera.nearZ = 1f;
        //float size = cubeCamera.nearZ * (float)Math.tan((double)cubeCamera.fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * cubeCamera.aspectRatio, size * cubeCamera.aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);
        cubeCamera.world_position.x = object.world_position.x;
        cubeCamera.world_position.y = object.world_position.y;
        cubeCamera.world_position.z = object.world_position.z;
        cubeCamera.angle.x = -object.world_angle.x + -object.local_angle.x;
        cubeCamera.angle.y = -object.world_angle.y + -object.local_angle.y;
        cubeCamera.angle.z = -object.world_angle.z + -object.local_angle.z;
        cubeCamera.offset_angle.x = 0.0f;
        cubeCamera.offset_angle.y = 180.0f;
        cubeCamera.offset_angle.z = 0.0f;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferFront.unbind();
    }

    private void renderToFrameBufferBack(Object3D object) {
        frameBufferBack.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        //cubeCamera.fov = 45f;
        //cubeCamera.nearZ = 1f;
        //float size = cubeCamera.nearZ * (float)Math.tan((double)cubeCamera.fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * cubeCamera.aspectRatio, size * cubeCamera.aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);
        cubeCamera.world_position.x = object.world_position.x;
        cubeCamera.world_position.y = object.world_position.y;
        cubeCamera.world_position.z = object.world_position.z;
        cubeCamera.angle.x = -object.world_angle.x + -object.local_angle.x;
        cubeCamera.angle.y = -object.world_angle.y + -object.local_angle.y;
        cubeCamera.angle.z = -object.world_angle.z + -object.local_angle.z;
        cubeCamera.offset_angle.x = 0.0f;
        cubeCamera.offset_angle.y = 0.0f;
        cubeCamera.offset_angle.z = 0.0f;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferBack.unbind();
    }

    private void renderToFrameBufferLeft(Object3D object) {
        frameBufferLeft.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        //cubeCamera.fov = 45f;
        //cubeCamera.nearZ = 1f;
        //float size = cubeCamera.nearZ * (float)Math.tan((double)cubeCamera.fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * cubeCamera.aspectRatio, size * cubeCamera.aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);
        cubeCamera.world_position.x = object.world_position.x;
        cubeCamera.world_position.y = object.world_position.y;
        cubeCamera.world_position.z = object.world_position.z;
        cubeCamera.angle.x = -object.world_angle.x + -object.local_angle.x;
        cubeCamera.angle.y = -object.world_angle.y + -object.local_angle.y;
        cubeCamera.angle.z = -object.world_angle.z + -object.local_angle.z;
        cubeCamera.offset_angle.x = 0.0f;
        cubeCamera.offset_angle.y = 90.0f;
        cubeCamera.offset_angle.z = 0.0f;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferLeft.unbind();
    }

    private void renderToFrameBufferRight(Object3D object) {
        frameBufferRight.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        //cubeCamera.fov = 45f;
        //cubeCamera.nearZ = 1f;
        //float size = cubeCamera.nearZ * (float)Math.tan((double)cubeCamera.fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * cubeCamera.aspectRatio, size * cubeCamera.aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);
        cubeCamera.world_position.x = object.world_position.x;
        cubeCamera.world_position.y = object.world_position.y;
        cubeCamera.world_position.z = object.world_position.z;
        cubeCamera.angle.x = -object.world_angle.x + -object.local_angle.x;
        cubeCamera.angle.y = -object.world_angle.y + -object.local_angle.y;
        cubeCamera.angle.z = -object.world_angle.z + -object.local_angle.z;
        cubeCamera.offset_angle.x = 0.0f;
        cubeCamera.offset_angle.y = -90.0f;
        cubeCamera.offset_angle.z = 0.0f;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferRight.unbind();
    }

    private void renderToFrameBufferUp(Object3D object) {
        frameBufferUp.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        //cubeCamera.fov = 45f;
        //cubeCamera.nearZ = 1f;
        //float size = cubeCamera.nearZ * (float)Math.tan((double)cubeCamera.fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * cubeCamera.aspectRatio, size * cubeCamera.aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);
        cubeCamera.world_position.x = object.world_position.x;
        cubeCamera.world_position.y = object.world_position.y;
        cubeCamera.world_position.z = object.world_position.z;
        cubeCamera.angle.x = -object.world_angle.x + -object.local_angle.x;
        cubeCamera.angle.y = -object.world_angle.y + -object.local_angle.y;
        cubeCamera.angle.z = -object.world_angle.z + -object.local_angle.z;
        cubeCamera.offset_angle.x = -90.0f;
        cubeCamera.offset_angle.y = 0.0f;
        cubeCamera.offset_angle.z = 0.0f;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferUp.unbind();
    }

    private void renderToFrameBufferDown(Object3D object) {
        frameBufferDown.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        //cubeCamera.fov = 45f;
        //cubeCamera.nearZ = 1f;
        //float size = cubeCamera.nearZ * (float)Math.tan((double)cubeCamera.fov / 2.0 * (Math.PI / 180.0));
        //Matrix.frustumM(cubeCamera.projectionMatrix, 0, -size * cubeCamera.aspectRatio, size * cubeCamera.aspectRatio, -size, size, cubeCamera.nearZ, cubeCamera.farZ);
        cubeCamera.world_position.x = object.world_position.x;
        cubeCamera.world_position.y = object.world_position.y;
        cubeCamera.world_position.z = object.world_position.z;
        cubeCamera.angle.x = -object.world_angle.x + -object.local_angle.x;
        cubeCamera.angle.y = -object.world_angle.y + -object.local_angle.y;
        cubeCamera.angle.z = -object.world_angle.z + -object.local_angle.z;
        cubeCamera.offset_angle.x = 90.0f;
        cubeCamera.offset_angle.y = 0.0f;
        cubeCamera.offset_angle.z = 0.0f;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferDown.unbind();
    }

    private void renderToFrameBuffer() {
        // This only reflects the skybox onto the surface.
        experimentalRenderCubeMapFrameBuffers(ball);

        // This here is dynamic environment mapping, but requires the scene to be redrawn in 6 directions
        // onto 6 framebuffers in a different function:
        //renderCubeMapFrameBuffers(ball);

        frameBuffer.bind();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
            glUseProgram(programTexturedDirectionalSpecularLit);
            render(viewableCamera);
            renderSeperate(viewableCamera);
        frameBuffer.unbind();
    }

    private void renderToDepthBuffer() {
        depthBuffer.bind();
        glUseProgram(programDepth);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        render(viewableCamera);
        depthBuffer.unbind();
    }

    private void renderToPanelFrameBuffer(Object3D object) {
        // Needs to be here because it is using a different program program
        /*
        plane = new Plane3D(new Vertex3D(ship[0].vertexArrayList.get(0).x, ship[0].vertexArrayList.get(0).y, ship[0].vertexArrayList.get(0).z),
                new Vertex3D(ship[0].vertexArrayList.get(1).x, ship[0].vertexArrayList.get(1).y, ship[0].vertexArrayList.get(1).z),
                new Vertex3D(ship[0].vertexArrayList.get(2).x, ship[0].vertexArrayList.get(2).y, ship[0].vertexArrayList.get(2).z));
        ship[0].reflectMatrixPlane(plane.a, plane.b, plane.c, plane.d);

        frameBufferPanel.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glUseProgram(programTexturedDirectionalSpecularLit);
        cubeCamera.world_position.x = object.world_position.x + object.local_position.x;
        cubeCamera.world_position.y = object.world_position.y + object.local_position.y;
        cubeCamera.world_position.z = -object.world_position.z + object.local_position.z;
        cubeCamera.offset_position.x = ship[0].modelViewReflectionMatrix[12];
        cubeCamera.offset_position.y = ship[0].modelViewReflectionMatrix[13];
        cubeCamera.offset_position.z = ship[0].modelViewReflectionMatrix[14];
        cubeCamera.angle.x = object.world_angle.x + object.local_angle.x;
        cubeCamera.angle.y = object.world_angle.y + object.local_angle.y;
        cubeCamera.angle.z = object.world_angle.z + object.local_angle.z;
        cubeCamera.offset_angle.x = ship[0].modelViewReflectionMatrix[0] * plane.a;
        cubeCamera.offset_angle.y = ship[0].modelViewReflectionMatrix[5] * plane.b;
        cubeCamera.offset_angle.z = ship[0].modelViewReflectionMatrix[10] * plane.c;
        cubeCamera.update();
        render(cubeCamera);
        frameBufferPanel.unbind();

         */
    }

    private void renderCubeMapTextures(Object3D object, int textureFront, int textureBack, int textureLeft, int textureRight, int textureTop, int textureBottom) {
        object.bind();

        glUseProgram(programTextured);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        //////////////////////////////////////////////////////////////
        // SIDE: 1
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, object.dynamic_cubemap_texture[0], 0);

        cubemap_polygon_left.bindData();
        cubemap_polygon_left.position.x = 0.0f;
        cubemap_polygon_left.position.y = 0.0f;
        cubemap_polygon_left.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        cubemap_polygon_left.setTexture(textureLeft); // Framebuffer
        cubemap_polygon_left.rgba(1f,1f,1f, 1f);
        cubemap_polygon_left.draw();
        //////////////////////////////////////////////////////////////
        // SIDE: 2
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X, object.dynamic_cubemap_texture[0], 0);

        cubemap_polygon_right.bindData();
        cubemap_polygon_right.position.x = 0.0f;
        cubemap_polygon_right.position.y = 0.0f;
        cubemap_polygon_right.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        cubemap_polygon_right.setTexture(textureRight); // Framebuffer2
        cubemap_polygon_right.rgba(1f,1f,1f, 1f);
        cubemap_polygon_right.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 3
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, object.dynamic_cubemap_texture[0], 0);

        cubemap_polygon_up.bindData();
        cubemap_polygon_up.position.x = 0.0f;
        cubemap_polygon_up.position.y = 0.0f;
        cubemap_polygon_up.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        cubemap_polygon_up.setTexture(textureTop); // Framebuffer3
        cubemap_polygon_up.rgba(1f,1f,1f, 1f);
        cubemap_polygon_up.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 4
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, object.dynamic_cubemap_texture[0], 0);

        cubemap_polygon_down.bindData();
        cubemap_polygon_down.position.x = 0.0f;
        cubemap_polygon_down.position.y = 0.0f;
        cubemap_polygon_down.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        cubemap_polygon_down.setTexture(textureBottom); // Framebuffer4
        cubemap_polygon_down.rgba(1f,1f,1f, 1f);
        cubemap_polygon_down.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 5
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, object.dynamic_cubemap_texture[0], 0);

        cubemap_polygon_front.bindData();
        cubemap_polygon_front.position.x = 0.0f;
        cubemap_polygon_front.position.y = 0.0f;
        cubemap_polygon_front.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        cubemap_polygon_front.setTexture(textureFront); // Framebuffer5
        cubemap_polygon_front.rgba(1f,1f,1f, 1f);
        cubemap_polygon_front.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 6
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, object.dynamic_cubemap_texture[0], 0);

        cubemap_polygon_back.bindData();
        cubemap_polygon_back.position.x = 0.0f;
        cubemap_polygon_back.position.y = 0.0f;
        cubemap_polygon_back.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        cubemap_polygon_back.setTexture(textureBack); // Framebuffer6
        cubemap_polygon_back.rgba(1f,1f,1f, 1f);
        cubemap_polygon_back.draw();

        object.unbind();
    }

    private void experimentalRenderCubeMapFrameBuffers(Object3D object) {
        //NOTE:
        //PERFECT WITH LEFT, RIGHT, UP, DOWN, FRONT BACK

        object.bind();

        glUseProgram(programTextured);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        //////////////////////////////////////////////////////////////
        // SIDE: 1
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_left.bindData();
        reflection_cubemap_polygon_left.position.x = 0.0f;
        reflection_cubemap_polygon_left.position.y = 0.0f;
        reflection_cubemap_polygon_left.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_left.setTexture(skyboxTexture[2]); // Framebuffer2
        reflection_cubemap_polygon_left.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_left.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 2
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_right.bindData();
        reflection_cubemap_polygon_right.position.x = 0.0f;
        reflection_cubemap_polygon_right.position.y = 0.0f;
        reflection_cubemap_polygon_right.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_right.setTexture(skyboxTexture[3]); // Framebuffer
        reflection_cubemap_polygon_right.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_right.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 3
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_up.bindData();
        reflection_cubemap_polygon_up.position.x = 0.0f;
        reflection_cubemap_polygon_up.position.y = 0.0f;
        reflection_cubemap_polygon_up.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_up.setTexture(skyboxTexture[4]); // Framebuffer3
        reflection_cubemap_polygon_up.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_up.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 4
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_down.bindData();
        reflection_cubemap_polygon_down.position.x = 0.0f;
        reflection_cubemap_polygon_down.position.y = 0.0f;
        reflection_cubemap_polygon_down.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_down.setTexture(skyboxTexture[5]); // Framebuffer4
        reflection_cubemap_polygon_down.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_down.draw();

        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_POSITIVE_Z
        // SIDE: 5
        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_front.bindData();
        reflection_cubemap_polygon_front.position.x = 0.0f;
        reflection_cubemap_polygon_front.position.y = 0.0f;
        reflection_cubemap_polygon_front.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_front.setTexture(skyboxTexture[1]); // Framebuffer6
        reflection_cubemap_polygon_front.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_front.draw();

        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        // SIDE: 6
        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_POSITIVE_Z
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_back.bindData();
        reflection_cubemap_polygon_back.position.x = 0.0f;
        reflection_cubemap_polygon_back.position.y = 0.0f;
        reflection_cubemap_polygon_back.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_back.setTexture(skyboxTexture[0]); // Framebuffer5
        reflection_cubemap_polygon_back.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_back.draw();

        object.unbind();


        //colorTexture[0] = Texture.loadTexture(context, R.drawable.red); front
        //colorTexture[1] = Texture.loadTexture(context, R.drawable.green); back
        //colorTexture[2] = Texture.loadTexture(context, R.drawable.blue); left
        //colorTexture[3] = Texture.loadTexture(context, R.drawable.purple); right
        //colorTexture[4] = Texture.loadTexture(context, R.drawable.yellow); top
        //colorTexture[5] = Texture.loadTexture(context, R.drawable.skyblue); bottom

        //skyboxTexture[0] = Texture.loadTexture(context, R.drawable.lposz); 1
        //skyboxTexture[1] = Texture.loadTexture(context, R.drawable.lnegz); 0
        //skyboxTexture[2] = Texture.loadTexture(context, R.drawable.lposx); 2
        //skyboxTexture[3] = Texture.loadTexture(context, R.drawable.lnegx); 3
        //skyboxTexture[4] = Texture.loadTexture(context, R.drawable.lposy); 5
        //skyboxTexture[5] = Texture.loadTexture(context, R.drawable.lnegy); 4
    }

    private void renderCubeMapFrameBuffers(Object3D object) {
        //NOTE:
        //PERFECT WITH LEFT, RIGHT, UP, DOWN, FRONT BACK

        object.bind();
        glUseProgram(programTextured);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        //TODO: Get all 6 angles for cube map!

        //////////////////////////////////////////////////////////////
        // SIDE: 1
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_left.bindData();
        reflection_cubemap_polygon_left.position.x = 0.0f;
        reflection_cubemap_polygon_left.position.y = 0.0f;
        reflection_cubemap_polygon_left.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_left.setTexture(frameBufferLeft.texture); // Framebuffer2
        reflection_cubemap_polygon_left.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_left.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 2
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_right.bindData();
        reflection_cubemap_polygon_right.position.x = 0.0f;
        reflection_cubemap_polygon_right.position.y = 0.0f;
        reflection_cubemap_polygon_right.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_right.setTexture(frameBufferRight.texture); // Framebuffer
        reflection_cubemap_polygon_right.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_right.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 3
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_Y, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_up.bindData();
        reflection_cubemap_polygon_up.position.x = 0.0f;
        reflection_cubemap_polygon_up.position.y = 0.0f;
        reflection_cubemap_polygon_up.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_up.setTexture(frameBufferUp.texture); // Framebuffer3
        reflection_cubemap_polygon_up.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_up.draw();

        //////////////////////////////////////////////////////////////
        // SIDE: 4
        //////////////////////////////////////////////////////////////
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_down.bindData();
        reflection_cubemap_polygon_down.position.x = 0.0f;
        reflection_cubemap_polygon_down.position.y = 0.0f;
        reflection_cubemap_polygon_down.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_down.setTexture(frameBufferDown.texture); // Framebuffer4
        reflection_cubemap_polygon_down.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_down.draw();

        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_POSITIVE_Z
        // SIDE: 5
        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_front.bindData();
        reflection_cubemap_polygon_front.position.x = 0.0f;
        reflection_cubemap_polygon_front.position.y = 0.0f;
        reflection_cubemap_polygon_front.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_front.setTexture(frameBufferFront.texture); // Framebuffer6
        reflection_cubemap_polygon_front.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_front.draw();

        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        // SIDE: 6
        //////////////////////////////////////////////////////////////GL_TEXTURE_CUBE_MAP_POSITIVE_Z
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, object.dynamic_cubemap_texture[0], 0);

        reflection_cubemap_polygon_back.bindData();
        reflection_cubemap_polygon_back.position.x = 0.0f;
        reflection_cubemap_polygon_back.position.y = 0.0f;
        reflection_cubemap_polygon_back.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        reflection_cubemap_polygon_back.setTexture(frameBufferBack.texture); // Framebuffer5
        reflection_cubemap_polygon_back.rgba(1f,1f,1f, 1f);
        reflection_cubemap_polygon_back.draw();

        object.unbind();
    }

    private void renderFrameBufferSide(int frameBufferSide) {
        frameBufferHalf.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        fullscreen_rendered_polygon.bindData();
        fullscreen_rendered_polygon.position.x = 0.0f;
        fullscreen_rendered_polygon.position.y = 0.0f;
        fullscreen_rendered_polygon.updateMatrices(projectionMatrix2D, camera[0].aspectRatio, 0f);
        fullscreen_rendered_polygon.setTexture(frameBufferSide);
        fullscreen_rendered_polygon.rgba(1f,1f,1f, 1f);
        fullscreen_rendered_polygon.draw();

        frameBufferHalf.unbind();
    }

    private void setupTextures(){
        cubeTexture = Texture.loadTexture(context, R.drawable.futuristic);
        stoneTexture = Texture.loadTexture(context, R.drawable.stone);
        shipTexture = Texture.loadTexture(context, R.drawable.shiptexture);
        shipTexture2 = Texture.loadTexture(context, R.drawable.shiptexture2);
        marioTexture = Texture.loadTexture(context, R.drawable.mariofv);
        vrMaskTexture = Texture.loadTexture(context, R.drawable.vrscreenimage);
        wormholeTexture = Texture.loadTexture(context, R.drawable.futuristic);
        metalBallTexture = Texture.loadTexture(context, R.drawable.darkmetal);

        /*colorTexture[0] = Texture.loadTexture(context, R.drawable.spaceback);
        colorTexture[1] = Texture.loadTexture(context, R.drawable.spacefront);
        colorTexture[2] = Texture.loadTexture(context, R.drawable.spaceleft);
        colorTexture[3] = Texture.loadTexture(context, R.drawable.spaceright);
        colorTexture[4] = Texture.loadTexture(context, R.drawable.spacetop);
        colorTexture[5] = Texture.loadTexture(context, R.drawable.spacebottom);
        colorTexture[6] = Texture.loadTexture(context, R.drawable.purple);*/

        colorTexture[0] = Texture.loadTexture(context, R.drawable.red);
        colorTexture[1] = Texture.loadTexture(context, R.drawable.green);
        colorTexture[2] = Texture.loadTexture(context, R.drawable.blue);
        colorTexture[3] = Texture.loadTexture(context, R.drawable.purple);
        colorTexture[4] = Texture.loadTexture(context, R.drawable.yellow);
        colorTexture[5] = Texture.loadTexture(context, R.drawable.skyblue);
        colorTexture[6] = Texture.loadTexture(context, R.drawable.orange);

        skyboxTexture[0] = Texture.loadTexture(context, R.drawable.zpos);
        skyboxTexture[1] = Texture.loadTexture(context, R.drawable.zneg);
        skyboxTexture[2] = Texture.loadTexture(context, R.drawable.xpos);
        skyboxTexture[3] = Texture.loadTexture(context, R.drawable.xneg);
        skyboxTexture[4] = Texture.loadTexture(context, R.drawable.ypos);
        skyboxTexture[5] = Texture.loadTexture(context, R.drawable.yneg);

        //skyboxTexture[0] = Texture.loadTexture(context, R.drawable.lposz);
        //skyboxTexture[1] = Texture.loadTexture(context, R.drawable.lnegz);
        //skyboxTexture[2] = Texture.loadTexture(context, R.drawable.lposx);
        //skyboxTexture[3] = Texture.loadTexture(context, R.drawable.lnegx);
        //skyboxTexture[4] = Texture.loadTexture(context, R.drawable.lposy);
        //skyboxTexture[5] = Texture.loadTexture(context, R.drawable.lnegy);

        lensFlareTexture[0] = Texture.loadTexture(context, R.drawable.flare);
        lensFlareTexture[1] = Texture.loadTexture(context, R.drawable.flare2);
        lensFlareTexture[2] = Texture.loadTexture(context, R.drawable.flare3);
        lensFlareTexture[3] = Texture.loadTexture(context, R.drawable.flare4);
        lensFlareTexture[4] = Texture.loadTexture(context, R.drawable.flare3);
        lensFlareTexture[5] = Texture.loadTexture(context, R.drawable.flare2);
    }

    private void setupSounds() {
        sound.load(R.raw.logosong);
        sound.load(R.raw.titlesong, true);
        sound.load(R.raw.trance1, true);
    }

    private void controls(Camera3D camera){
        if (camera != null) {

            Vector3D forward = new Vector3D(camera.viewMatrix[2], camera.viewMatrix[6], camera.viewMatrix[10]);
            Vector3D strafe = new Vector3D(camera.viewMatrix[0], camera.viewMatrix[4], camera.viewMatrix[8]);
            float dx = 0.0f;
            float dy = 0.0f;
            float dz = 0.0f;

            float lx = 0.0f;
            float lz = 0.0f;

            //float sinCameraAngleY = (float) Math.sin(camera.angle.y * Math.PI / 180.0f);
            //float cosCameraAngleY = (float) Math.cos(camera.angle.y * Math.PI / 180.0f);

            for(int i = 0; i < Constants.MAX_FINGERS; i++) {
                if (MainActivity.heldDown[i]) {
                    //FPS Camera
                    final float CAMERA_SPEED = 1f;

                    if (controller[0].touched[i]) {
                        //Forward / Backward
                        dz = controller[0].getYPercent(i) * CAMERA_SPEED;

                                                        //camera.world_position.x += sinCameraAngleY * controller[0].getYPercent(i) * CAMERA_SPEED;
                                                        //camera.world_position.z -= cosCameraAngleY * controller[0].getYPercent(i) * CAMERA_SPEED;

                        //Y Angle
                        camera.current_angle += controller[0].getXPercent(i) * CAMERA_SPEED;
                                                        //dx = controller[0].getXPercent(i) * CAMERA_SPEED;
                    }

                    //Log.d("Controller 1 Bounds", String.valueOf(controller[1].outOfBounds));
                    if (controller[1].touched[i]) {
                        //Up / Down
                        dy = controller[1].getYPercent(i) * CAMERA_SPEED;

                        //Strafe Left / Right
                        dx = controller[1].getXPercent(i) * CAMERA_SPEED;
                        //lx = controller[1].getXPercent(i) * CAMERA_SPEED;
                        //lz = controller[1].getYPercent(i) * CAMERA_SPEED;
                        //camera.world_position.x += cosCameraAngleY * controller[1].getXPercent(i) * CAMERA_SPEED;
                        //camera.world_position.z += sinCameraAngleY * controller[1].getXPercent(i) * CAMERA_SPEED;
                    }

                }

                camera.world_position.x += (-dz * forward.x + dx * strafe.x);
                camera.world_position.y += dy;
                camera.world_position.z += (-dz * forward.z + dx * strafe.z);

                camera.angle.y = camera.current_angle;

                // Uncomment the next two lines for VR controls
                //camera.angle.x = MainActivity.pitch;

                //camera.angle.y = MainActivity.azimuth + camera.current_angle;
                //Log.d(String.valueOf(MainActivity.azimuth), "test");

                camera.offset_position.x = 0.0f;
                camera.offset_position.y = 0f;
                camera.offset_position.z = 0f;
                camera.update();


            }

            if (MainActivity.heldDown[0] == false){
                touchOverLap = false;
                touchInRange = false;
            }
        }
    }

    private void loadShaders(){
        String[] fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.particle_fragment_shader)};
        programParticle = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.particle_vertex_shader), fragment , 1);

        String[] fragments = new String[]{Shader.readTextFileFromResource(context, R.raw.simple_fragment_shader),
                                          Shader.readTextFileFromResource(context, R.raw.ambient_fragment_shader),
                                          Shader.readTextFileFromResource(context, R.raw.diffuse_fragment_shader)};
        programSimple = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.simple_vertex_shader), fragments, 3);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.texture_fragment_shader)};
        programTextured = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.texture_per_pixel_lit_fragment_shader)};
        programTexturedDirectionalSpecularLit = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_per_vertex_lit_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.depth_fragment)};
        programDepth = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.depth_vertex), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.exposure_tone_mapping_fragment_shader)};
        programExposureToneMapping = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.bright_filter_fragment_shader)};
        programBrightFilter = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.horizontal_blur_fragment_shader)};
        programHorizontalBlur = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.vertical_blur_fragment_shader)};
        programVerticalBlur = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.contrast_boost_fragment_shader)};
        programContrastBoost = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);

        fragment = new String[]{Shader.readTextFileFromResource(context, R.raw.multitexture_fragment_shader)};
        programMultiTexture = Shader.buildProgram(Shader.readTextFileFromResource(context, R.raw.texture_vertex_shader), fragment, 1);
    }

    private void changeCameraButtonEvent(){
        if (MainActivity.heldDown[0]){
            if (MainActivity.touchScaled[0].x < 0.1f && MainActivity.touchScaled[0].y < 0.1f) {
                if (!controller[0].touched[0] && !controller[1].touched[0]) {
                    if (tapFlag == false) {
                        tapFlag = true;
                        if (cameraNumber == 0) {
                            cameraNumber = 1;
                            viewableCamera = camera[cameraNumber];
                            otherCamera = camera[0];
                        } else {
                            cameraNumber = 0;
                            viewableCamera = camera[cameraNumber];
                            otherCamera = camera[1];
                        }
                    }
                }
            }
        }
        else{
            tapFlag = false;
        }
    }

    private void createCollidableQuads() {
        vector_space_pa[0] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pb[0] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pc[0] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pd[0] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        vector_space_pa[1] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pb[1] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pc[1] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pd[1] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        vector_space_pa[2] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pb[2] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pc[2] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pd[2] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        vector_space_pa[3] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pb[3] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pc[3] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pd[3] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        vector_space_pa[4] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pb[4] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pc[4] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pd[4] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        vector_space_pa[5] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pb[5] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pc[5] = new Vertex3D((-cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));

        vector_space_pd[5] = new Vertex3D((cube.scalar.x) / (ellipsoid.radius.x + ellipsoid.gap.x),
                (-cube.scalar.y) / (ellipsoid.radius.y + ellipsoid.gap.y),
                (-cube.scalar.z) / (ellipsoid.radius.z + ellipsoid.gap.z));
    }
}


