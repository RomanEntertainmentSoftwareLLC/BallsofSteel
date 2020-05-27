package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;

import math2D.Vector2D;
import math2D.Vertex2D;
import primitive2D.Quad2D;

/**
 * Created by Roman Entertainment Software LLC on 5/26/2018.
 */
public class AnalogController {
    Context context;
    //2D Quads
    public Quad2D controllerOuter;
    public Quad2D controllerInner;

    public int outerTexture;
    public int innerTexture;

    private float outerControllerSize; //between 0f - 1f
    private float innerControllerSize; //between 0f - 1f

    public boolean[] touched;

    private float radius1;
    private float radius2;

    private Vertex2D circlePos;
    private float[] distance;
    private float[] radian;
    private float[] xx;
    private float[] yy;

    public AnalogController(Context context, float x, float y, float outerControllerSize, float innerControllerSize){
        this.context = context;
        this.outerControllerSize = outerControllerSize;
        this.innerControllerSize = innerControllerSize;

        controllerOuter = new Quad2D(context, "Quad2D: Controller Outer", Render.programTextured,
                -outerControllerSize, -outerControllerSize,
                outerControllerSize,  -outerControllerSize,
                -outerControllerSize, outerControllerSize,
                outerControllerSize, outerControllerSize,
                1f, 1f, 1f, 1f,
                1f, 1f);
        controllerOuter.position.x = x;
        controllerOuter.position.y = y;

        controllerInner = new Quad2D(context, "Quad2D: Controller Inner",Render.programTextured,
                -innerControllerSize, -innerControllerSize,
                innerControllerSize,  -innerControllerSize,
                -innerControllerSize, innerControllerSize,
                innerControllerSize, innerControllerSize,
                1f, 1f, 1f, 1f,
                1f, 1f);
        controllerInner.position.x = controllerOuter.position.x;
        controllerInner.position.y = controllerOuter.position.y;

        radius1 = outerControllerSize * Render.camera[0].screenHeight;
        radius2 = innerControllerSize * Render.camera[0].screenHeight;

        touched = new boolean[Constants.MAX_FINGERS];
        distance = new float[Constants.MAX_FINGERS];
        radian = new float[Constants.MAX_FINGERS];
        xx = new float[Constants.MAX_FINGERS];
        yy = new float[Constants.MAX_FINGERS];

    }

    public void setupTextures(){
        outerTexture = Texture.loadTexture(context, R.drawable.controller);
        innerTexture = Texture.loadTexture(context, R.drawable.controller);
    }

    private Vertex2D convertScreenCoords(float x, float y){
        return new Vertex2D(x * Render.camera[0].screenWidth, y * Render.camera[0].screenHeight);
    }

    private Vertex2D convertScaledCoords(float x, float y){
        return new Vertex2D(x / Render.camera[0].screenWidth, y / Render.camera[0].screenHeight);
    }

    public void checkForTouch(int i){
        if (MainActivity.heldDown[i]) {
            circlePos = convertScreenCoords(controllerOuter.position.x, controllerOuter.position.y);
            distance[i] = (float) Math.sqrt(((MainActivity.touch[i].x - circlePos.x) * (MainActivity.touch[i].x - circlePos.x)) +
                                               ((MainActivity.touch[i].y - circlePos.y) * (MainActivity.touch[i].y - circlePos.y))) + radius2;

            if (distance[i] < (radius1 + radius2)){
                touched[i] = true;
            }
            else{
                touched[i] = false;
            }
        } else {
            touched[i] = false;
        }
    }

    public void runAnalogStick(int i){
        if (touched[i]) {
            if (distance[i] >= radius1 && distance[i] < (radius1 + radius2)){
                // Outer boundary
                // Hit the border of the circle only if the mouse cursor
                // is greater than the center of the small circle and
                // less than the border of the large circle
                float radian = MathCommon.getRadian(circlePos.x, circlePos.y, MainActivity.touch[i].x, MainActivity.touch[i].y);
                float xx = ((radius1 - radius2) * (float)Math.cos(-radian)) + circlePos.x;
                float yy = ((radius1 - radius2) * (float)Math.sin(-radian)) + circlePos.y;
                controllerInner.position = convertScaledCoords(xx, yy);
            }
            else if (distance[i] < radius1){
                // Freely move around only if the mouse is less than the center of the small circle
                controllerInner.position = convertScaledCoords(MainActivity.touch[i].x, MainActivity.touch[i].y);
            }
            else if (distance[i] >= (radius1 + radius2)){
                // Mouse cursor is completely outside the large circle
                // Out of bounds
                controllerInner.position.x = controllerOuter.position.x;
                controllerInner.position.y = controllerOuter.position.y;
            }
        }
        else{
            // Touch was released, goto the center
            int unTouched = 0;

            for (int j = 0; j < Constants.MAX_FINGERS; j++) {
                if (!touched[j])
                    unTouched++;
            }

            if (unTouched == Constants.MAX_FINGERS){
                controllerInner.position.x = controllerOuter.position.x;
                controllerInner.position.y = controllerOuter.position.y;
            }
        }
    }

    public float getXPercent(int i){
        Vector2D normal = new Vector2D(1f, 0f);
        Vertex2D controllerPosition = convertScreenCoords(controllerOuter.position.x, controllerOuter.position.y);
        Vector2D vector = new Vector2D(controllerPosition, MainActivity.touch[i]);
        float signedDistance = normal.dotProduct(vector);
        float signedDistancePercent = signedDistance / (radius1 - radius2);

        if (signedDistancePercent > 1f)
            signedDistancePercent = 1f;

        if (signedDistancePercent < -1f)
            signedDistancePercent = -1f;

        normal = null;
        controllerPosition = null;
        vector = null;

        return signedDistancePercent;
    }

    public float getYPercent(int i){
        Vector2D normal = new Vector2D(0f, -1f);
        Vertex2D controllerPosition = convertScreenCoords(controllerOuter.position.x, controllerOuter.position.y);
        Vector2D vector = new Vector2D(controllerPosition, MainActivity.touch[i]);
        float signedDistance = normal.dotProduct(vector);
        float signedDistancePercent = signedDistance / (radius1 - radius2);

        if (signedDistancePercent > 1f)
            signedDistancePercent = 1f;

        if (signedDistancePercent < -1f)
            signedDistancePercent = -1f;

        normal = null;
        controllerPosition = null;
        vector = null;

        return signedDistancePercent;
    }

    public void draw(){
        controllerOuter.bindData();
        controllerOuter.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        controllerOuter.setTexture(outerTexture);
        controllerOuter.rgba(1f,1f,1f, 0.5f);
        controllerOuter.draw();

        controllerInner.bindData();
        controllerInner.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        controllerInner.setTexture(innerTexture);
        controllerInner.rgba(1f,1f,1f, 0.75f);
        controllerInner.draw();
    }

    public void release(){
        if (controllerOuter != null){
            controllerOuter.release();
            controllerOuter = null;
        }

        if (controllerInner != null){
            controllerInner.release();
            controllerInner = null;
        }
    }

}
