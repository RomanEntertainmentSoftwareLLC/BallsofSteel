package romanentertainmentsoftware.ballsofsteel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import math2D.Vertex2D;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import javax.microedition.khronos.opengles.GL10;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

public class MainActivity extends Activity implements SensorEventListener {

    private final String TAG = "MAINACTIVITY";
    public static Context context;
    public static  GLSurfaceView glSurfaceView;

    public static Vertex2D[] initialTouch = new Vertex2D[Constants.MAX_FINGERS];
    public static Vertex2D[] touch = new Vertex2D[Constants.MAX_FINGERS];
    public static Vertex2D[] finalTouch = new Vertex2D[Constants.MAX_FINGERS];

    public static Vertex2D[] initialTouchScaled = new Vertex2D[Constants.MAX_FINGERS];
    public static Vertex2D[] touchScaled = new Vertex2D[Constants.MAX_FINGERS];
    public static Vertex2D[] finalTouchScaled = new Vertex2D[Constants.MAX_FINGERS];

    public static int orientation;
    public static int width;
    public static int height;

    public static boolean[] heldDown = new boolean[Constants.MAX_FINGERS];
    public static boolean[] moved = new boolean[Constants.MAX_FINGERS];

    public static int pointerIndex;
    public static int pointerId;
    public static int pointerCount;

    public static SensorManager sensorManager;

    public static float[] rotMatrix = new float[9];
    public static float[] radianValues = new float[3];

    public static float azimuth;
    public static float pitch;
    public static float roll;

    public static float initial_azimuth;

    private static InputManager.InputDeviceListener sListener;

    public MainActivity() {
        ////Log.d(TAG,"MainActivity() fired");
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ////Log.d(TAG, "onCreate() fired");
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ////Log.d(TAG, "You are in portrait mode");
        } else {
            ////Log.d(TAG, "You are in landscape mode");
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        Boolean supportes3 = (configurationInfo.reqGlEsVersion>= 0x30000);

        if (supportes3) {
            //Toast.makeText(getApplicationContext(), "Your Device Support OpenGL ES3", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Your Device doesn’t Support OpenGL ES3", Toast.LENGTH_LONG).show();
        }


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        for (int i = 0; i < Constants.MAX_FINGERS; i++){
            initialTouch[i] = new Vertex2D();
            touch[i] = new Vertex2D();
            finalTouch[i] = new Vertex2D();
            initialTouchScaled[i] = new Vertex2D();
            touchScaled[i] = new Vertex2D();
            finalTouchScaled[i] = new Vertex2D();
        }

        //Toast.makeText(getApplicationContext(), size.x + ", " + size.y, Toast.LENGTH_SHORT).show();

        addDeviceListener();

        //Sets fullscreen mode and removes the top bar
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(new Render(this));
        setContentView(glSurfaceView);
    }

    @Override
    protected void onRestart(){
        ////Log.d(TAG,"onRestart() fired");
        super.onRestart();
    }


    @Override
    protected void onStart(){
        ////Log.d(TAG, "onStart() fired");
        super.onStart();
    }

    @Override
    protected void onResume() {
        ////Log.d(TAG, "onResume() fired");
        super.onResume();
        glSurfaceView.onResume();

        sensorManager.registerListener(this , sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);

        if (Render.sound != null) {
            if(Render.sound.numberOfSounds > 0) {
                for (int i = 0; i < Render.sound.numberOfSounds; i++) {
                    ////Log.d("WAS PLAYING", String.valueOf(Render.sound.wasPlaying.get(i)));
                    if (Render.sound.wasPlaying.get(i) == true) {
                        Render.sound.play(i);
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        ////Log.d(TAG, "onPause()");
        super.onPause();
        glSurfaceView.onPause();

        sensorManager.unregisterListener(this);
        if (Render.sound != null) {
            if(Render.sound.numberOfSounds > 0) {
                for (int i = 0; i < Render.sound.numberOfSounds; i++) {
                    if (Render.sound.wasPlaying.get(i) == true) {
                        Render.sound.pause(i);
                    }
                }
            }
        }
    }

    @Override
    protected void onStop(){
        ////Log.d(TAG,"onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        ////Log.d(TAG,"onDestroy()");
        super.onDestroy();

        if (Render.logo != null){
            ////Log.d(TAG,"Releasing logo");
            Render.logo.release();
            Render.logo = null;
        }

        if (Render.title != null){
            ////Log.d(TAG,"Releasing title");
            Render.title.release();
            Render.title = null;
        }

        if (Render.sound != null){
            ////Log.d(TAG,"Releasing sounds");
            Render.sound.release();
            Render.sound = null;
        }

        if (Render.camera[0] != null){
            ////Log.d(TAG, "Releasing camera[0]");
            Render.camera[0].release();
            Render.camera[0] = null;
        }

        if (Render.camera[1] != null){
            ////Log.d(TAG, "Releasing camera[1]");
            Render.camera[1].release();
            Render.camera[1] = null;
        }

        if (Render.viewableCamera != null){
            ////Log.d(TAG, "Releasing viewableCamera");
            Render.viewableCamera.release();
            Render.viewableCamera = null;
        }

        if (Render.otherCamera != null){
            ////Log.d(TAG, "Releasing otherCamera");
            Render.otherCamera.release();
            Render.otherCamera = null;
        }

        if (Render.light != null){
            ////Log.d(TAG, "Releasing light");
            Render.light.release();
            Render.light = null;
        }

        if (Render.filmCamera != null){
            ////Log.d(TAG, "Releasing filmCamera");
            Render.filmCamera.release();
            Render.filmCamera = null;
        }

        if (Render.cube != null){
            ////Log.d(TAG, "Releasing cube");
            Render.cube.release();
            Render.cube = null;
        }

        if (Render.ship[0] != null){
            ////Log.d(TAG, "Releasing ship[0]");
            Render.ship[0].release();
            Render.ship[0] = null;
        }

        if (Render.ship[1] != null){
            ////Log.d(TAG, "Releasing ship[1]");
            Render.ship[1].release();
            Render.ship[1] = null;
        }

        if (Render.ball != null){
            ////Log.d(TAG, "Releasing ball");
            Render.ball.release();
            Render.ball = null;
        }

        if (Render.ball2 != null){
            ////Log.d(TAG, "Releasing ball2");
            Render.ball2.release();
            Render.ball2 = null;
        }

        if (Render.mario != null){
            ////Log.d(TAG, "Releasing mario");
            Render.mario.release();
            Render.mario = null;
        }

        if (Render.particle != null){
            ////Log.d(TAG, "Releasing particle");
            Render.particle.release();
            Render.particle = null;
        }

        if (Render.line != null){
            ////Log.d(TAG, "Releasing line");
            Render.line.release();
            Render.line = null;
        }

        if (Render.controller[0] != null){
            ////Log.d(TAG, "Releasing controller[0]");
            Render.controller[0].release();
            Render.controller[0] = null;
        }

        if (Render.controller[1] != null){
            ////Log.d(TAG, "Releasing controller[1]");
            Render.controller[1].release();
            Render.controller[1] = null;
        }
    }

    @Override
    public void onBackPressed() {
        //Note: This will take control of the back button
        ////Log.d(TAG,"onBackPressed()");
        Toast.makeText(this, "You pressed back", Toast.LENGTH_SHORT).show();

        //dont call **super**, if u want disable back button.
        //super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //Note: this handles the changes in orientation with the device rather than reset the whole app!
        //Note: doesn't work unless you add this in your AndroidManifest.xml in <activity>:
        //              android:configChanges="keyboardHidden|orientation|screenSize"
        //Log.d(TAG,"onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        orientation = this.getResources().getConfiguration().orientation;

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Log.d(TAG, "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Log.d(TAG, "Portrait");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pointerCount = event.getPointerCount();
        pointerIndex = event.getActionIndex();
        pointerId = event.getPointerId(pointerIndex);

        if (pointerCount > Constants.MAX_FINGERS)
            pointerCount = Constants.MAX_FINGERS;

        ////Log.d("pointerindex", String.valueOf(pointerIndex));


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (pointerCount <= Constants.MAX_FINGERS){
                    moved[0] = false;
                    heldDown[0] = true;

                    initialTouch[0].x = event.getX(pointerIndex);
                    initialTouch[0].y = event.getY(pointerIndex);
                    touch[0].x = event.getX(pointerIndex);
                    touch[0].y = event.getY(pointerIndex);

                    touchScaled[0].x = touch[0].x / Render.camera[0].screenWidth;
                    touchScaled[0].y = touch[0].y / Render.camera[0].screenHeight;
                    initialTouchScaled[0].x = initialTouch[0].x / Render.camera[0].screenWidth;
                    initialTouchScaled[0].y = initialTouch[0].y / Render.camera[0].screenHeight;
                    finalTouchScaled[0].x = initialTouchScaled[0].x;
                    finalTouchScaled[0].y = initialTouchScaled[0].y;

                }

                ////Log.d(TAG, "Action was DOWN");

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (pointerCount <= Constants.MAX_FINGERS) {
                    for (int i = 0; i < pointerCount; ++i) {
                        pointerId = event.getPointerId(i);

                        if (heldDown[i])
                            moved[i] = true;
                        else
                            moved[i] = false;

                        touch[i].x = event.getX(i);
                        touch[i].y = event.getY(i);
                        touchScaled[i].x = touch[i].x / Render.camera[0].screenWidth;
                        touchScaled[i].y = touch[i].y / Render.camera[0].screenHeight;
                    }
                }

                ////Log.d(TAG, "Action was MOVE");

                break;
            }
            case MotionEvent.ACTION_UP: {
                if (pointerCount <= Constants.MAX_FINGERS) {
                    moved[0] = false;
                    heldDown[0] = false;
                    finalTouch[0].x = event.getX(pointerIndex);
                    finalTouch[0].y = event.getY(pointerIndex);

                    ////Log.d(TAG, "Action was UP");

                    if (initialTouch[0].x < finalTouch[0].x) {
                        ////Log.d(TAG, "Left to Right swipe performed");
                    }

                    if (initialTouch[0].x > finalTouch[0].x) {
                        ////Log.d(TAG, "Right to Left swipe performed");
                    }

                    if (initialTouch[0].y < finalTouch[0].y) {
                        ////Log.d(TAG, "Up to Down swipe performed");
                    }

                    if (initialTouch[0].y > finalTouch[0].y) {
                        ////Log.d(TAG, "Down to Up swipe performed");
                    }
                }

                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (pointerCount <= Constants.MAX_FINGERS) {
                    for (int i = 1; i < pointerCount; ++i) {
                        pointerId = event.getPointerId(i);

                        moved[i] = false;
                        heldDown[i] = true;

                        initialTouch[i].x = event.getX(i);
                        initialTouch[i].y = event.getY(i);
                        touch[i].x = event.getX(i);
                        touch[i].y = event.getY(i);

                        touchScaled[i].x = touch[i].x / Render.camera[0].screenWidth;
                        touchScaled[i].y = touch[i].y / Render.camera[0].screenHeight;
                        initialTouchScaled[i].x = initialTouch[i].x / Render.camera[0].screenWidth;
                        initialTouchScaled[i].y = initialTouch[i].y / Render.camera[0].screenHeight;
                        finalTouchScaled[i].x = initialTouchScaled[i].x;
                        finalTouchScaled[i].y = initialTouchScaled[i].y;
                    }
                }

                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                if (pointerCount <= Constants.MAX_FINGERS) {
                    for (int i = 1; i < pointerCount; ++i) {
                        pointerId = event.getPointerId(i);

                        moved[i] = false;
                        heldDown[i] = false;
                        finalTouch[i].x = event.getX(i);
                        finalTouch[i].y = event.getY(i);

                        if (initialTouch[i].x < finalTouch[i].x) {
                            ////Log.d(TAG, "Left to Right swipe performed");
                        }

                        if (initialTouch[i].x > finalTouch[i].x) {
                            ////Log.d(TAG, "Right to Left swipe performed");
                        }

                        if (initialTouch[i].y < finalTouch[i].y) {
                            ////Log.d(TAG, "Up to Down swipe performed");
                        }

                        if (initialTouch[i].y > finalTouch[i].y) {
                            ////Log.d(TAG, "Down to Up swipe performed");
                        }
                    }
                }

                break;
            }
            case MotionEvent.ACTION_CANCEL:
                ////Log.d(TAG,"Action was CANCEL");
                break;

            case MotionEvent.ACTION_OUTSIDE:
                ////Log.d(TAG, "Movement occurred outside bounds of current screen element");
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotMatrix, event.values);
            SensorManager.remapCoordinateSystem(rotMatrix,
                    SensorManager.AXIS_X, SensorManager.AXIS_Y, rotMatrix);
            SensorManager.getOrientation(rotMatrix, radianValues);
            azimuth = (float) Math.toDegrees(radianValues[0]);
            roll = (float) Math.toDegrees(radianValues[1]);
            pitch = (float)Math.toDegrees(radianValues[2]) + 90f;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private static void addDeviceListener() {
        sListener = new InputManager.InputDeviceListener() {
            @Override
            public void onInputDeviceAdded(int deviceId) {

            }

            @Override
            public void onInputDeviceRemoved(int deviceId) {

            }

            @Override
            public void onInputDeviceChanged(int deviceId) {

            }
        };
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        int source = event.getSource();

        if((source & InputDevice.SOURCE_JOYSTICK)== InputDevice.SOURCE_JOYSTICK && event.getAction() == MotionEvent.ACTION_MOVE)
        {
            return true;
        }

        return super.onGenericMotionEvent(event);
    }
}
