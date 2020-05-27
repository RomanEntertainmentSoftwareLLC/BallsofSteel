package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;
import android.util.Log;
import primitive2D.Quad2D;

/**
 * Created by Roman Entertainment Software LLC on 4/15/2018.
 */

public class Logo {
    private Context context;
    private final int MAX = 2;

    public long logoMilliseconds;
    private double logoTime;
    private float logoSpeed;
    public boolean logoEnabled;

    private long logoHoldMilliseconds;
    private double logoHoldTime;
    private boolean logoHoldEnabled;
    private float logoTimeToHold;

    private long logoFadeMilliseconds;
    private double logoFadeTime;
    private float logoFadeSpeed;
    private boolean logoFadeEnabled;

    private long logo2HoldMilliseconds;
    private double logo2HoldTime;
    private boolean logo2HoldEnabled;
    private float logo2TimeToHold;

    private long logo2FadeMilliseconds;
    private double logo2FadeTime;
    private float logo2FadeSpeed;
    private boolean logo2FadeEnabled;

    public boolean finito;

    public float[] alpha = new float[MAX];
    private Quad2D logo;
    private Quad2D legal;
    private int[] texture = new int[MAX];

    private float logoAngle;
    private float legalAngle;

    Logo(Context context){
        Log.d("Result", "Logo Class Created!");

        this.context = context;

        logoMilliseconds = System.currentTimeMillis();
        logoSpeed = timeConvert(2f);
        logoEnabled = true;

        logoHoldEnabled = false;
        logoTimeToHold = 3f;

        logoFadeSpeed = timeConvert(2f);
        logoFadeEnabled = false;

        logo2HoldEnabled = false;
        logo2TimeToHold = 5f; //5 seconds

        logo2FadeSpeed = timeConvert(2f);
        logo2FadeEnabled = false;
    }

    private float timeConvert(float seconds)
    {
        if (seconds != 0f)
            return (1000 / seconds) * 0.001f;
        else
            return 0f;
    }

    public void run()
    {
        if (logoEnabled == true) {
            logoTime = ((double)System.currentTimeMillis() - (double)logoMilliseconds) * 0.001;
            alpha[0] = logoSpeed * (float)logoTime;
            if (alpha[0] >= 1f) {
                alpha[0] = 1f;
                logoEnabled = false;
                logoHoldMilliseconds = System.currentTimeMillis();
                logoHoldEnabled = true;
            }
        }

        if (logoHoldEnabled == true) {
            logoHoldTime = ((double)System.currentTimeMillis() - (double)logoHoldMilliseconds) * 0.001;
            if (logoHoldTime >= logoTimeToHold) {
                logoHoldTime = logoTimeToHold;
                logoHoldEnabled = false;
                logoFadeMilliseconds = System.currentTimeMillis();
                logoFadeEnabled = true;
            }
        }

        if (logoFadeEnabled == true) {
            logoFadeTime = ((double)System.currentTimeMillis() - (double)logoFadeMilliseconds) * 0.001;
            alpha[0] = 1f - (logoFadeSpeed * (float)logoFadeTime);
            alpha[1] = (logoFadeSpeed * (float)logoFadeTime);

            if (alpha[1] >= 1f) {
                alpha[1] = 1f;
            }

            if (alpha[0] <= 0f) {
                alpha[0] = 0f;
                logoFadeEnabled = false;
                logo2HoldMilliseconds = System.currentTimeMillis();
                logo2HoldEnabled = true;
            }
        }

        if (logo2HoldEnabled == true) {
            logo2HoldTime = ((double)System.currentTimeMillis() - (double)logo2HoldMilliseconds) * 0.001;
            if (logo2HoldTime >= logo2TimeToHold) {
                logo2HoldTime = logo2TimeToHold;
                logo2HoldEnabled = false;
                logo2FadeMilliseconds = System.currentTimeMillis();
                logo2FadeEnabled = true;
            }
        }

        if (logo2FadeEnabled == true) {
            logo2FadeTime = ((double)System.currentTimeMillis() - (double)logo2FadeMilliseconds) * 0.001;
            alpha[1] = 1f - (logo2FadeSpeed * (float)logo2FadeTime);

            if (alpha[1] <= 0f) {
                alpha[1] = 0f;
                logo2FadeEnabled = false;
                finito = true;
            }
        }
    }

    private void setupLogo(){
        logo = new Quad2D(context, "Quad2D: Logo", Render.programTextured,
                0f, 0f,
                Render.camera[0].aspectRatio, 0f,
                0f,1f,
                Render.camera[0].aspectRatio,1f,
                1f, 1f, 1f, 1f,
                1f,1f);
    }

    private void setupLegal(){
        legal = new Quad2D(context, "Quad2D: Legal", Render.programTextured,
                0f, 0f,
                Render.camera[0].aspectRatio, 0f,
                0f,1f,
                Render.camera[0].aspectRatio,1f,
                1f, 1f, 1f, 1f,
                1f,1f);
    }

    public void setupTextures() {
        texture[0] = Texture.loadTexture(context, R.drawable.logo);
        texture[1] = Texture.loadTexture(context, R.drawable.legal);
    }

    public void setup(){
        setupLogo();
        setupLegal();
        setupTextures();
    }

    public void renderLogo(){
        logo.position.x = 0f;
        logo.position.y = 0f;
        logoAngle = 0f;
        logo.bindData();
        logo.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, logoAngle);
        logo.setTexture(texture[0]);
        logo.rgba(1f,1f,1f, alpha[0]);
        logo.draw();
    }

    public void renderLegal(){
        legal.position.x = 0f;
        legal.position.y = 0f;
        legalAngle = 0f;
        legal.bindData();
        legal.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, legalAngle);
        legal.setTexture(texture[1]);
        legal.rgba(1f,1f,1f, alpha[1]);
        legal.draw();
    }

    public void release(){
        // Release arrays
        alpha = null;
        texture = null;

        // Release Quad2D objects
        if (logo != null){
            logo.release();
            logo = null;
        }

        if (legal != null){
            legal.release();
            legal = null;
        }
    }
}
