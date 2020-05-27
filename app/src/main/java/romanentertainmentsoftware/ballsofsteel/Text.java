package romanentertainmentsoftware.ballsofsteel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import primitive2D.Quad2D;

import static android.opengl.GLES30.*;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by Roman Entertainment Software LLC on 6/26/2018.
 */
public class Text {

    private Context context;
    private Quad2D textPolygon;

    public Text(Context context){
        this.context = context;
        textPolygon = new Quad2D(context, "Quad2D Text Polygon (Unused)", Render.programTextured,
                0f, 0f,
                1f, 0f,
                0f,1f,
                1f,1f,
                1f, 1f, 1f, 1f,
                1f,1f);


    }

    public void DrawText(String text, float x, float y){
        final int[] texture = new int[1];
        glGenTextures(1, texture, 0);

        if (texture[0] == 0) {
            return;
        }

        final Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_4444);

        if (bitmap == null) {
            glDeleteTextures(1, texture, 0);
            return;
        }

        //bitmap.eraseColor(0);

        final Canvas canvas = new Canvas(bitmap);

        final Drawable background = context.getResources().getDrawable(R.drawable.transparent512);
        background.setBounds(0, 0, (int)Render.camera[0].screenWidth, (int)Render.camera[0].screenHeight);
        background.draw(canvas);

        final Paint textPaint = new Paint();

        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(255, 0, 255, 0);

        float textY = 1.0f + 32f * 0.75f;

        for (String line: text.split("\n"))
        {
            canvas.drawText(line, 1, textY, textPaint);
            textY += -textPaint.ascent() + textPaint.descent();
        }

        glBindTexture(GL_TEXTURE_2D, texture[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();


        textPolygon.position.x = x;
        textPolygon.position.y = y;
        textPolygon.bindData();
        textPolygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        textPolygon.setTexture(texture[0]);
        textPolygon.rgba(1f,1f,1f, 1f);
        textPolygon.draw();

        glBindTexture(GL_TEXTURE_2D, 0);

    }
}
