package romanentertainmentsoftware.ballsofsteel;

import static android.opengl.GLES30.*;
import static javax.microedition.khronos.opengles.GL10.GL_MULTISAMPLE;
import static javax.microedition.khronos.opengles.GL11ExtensionPack.GL_DEPTH_COMPONENT32;

/**
 * Created by Roman Entertainment Software LLC on 6/7/2018.
 */

public class FrameBuffer {
    final int MSAA = 8;

    int[] frameBuffer = new int[1];
    int[] colorBuffer = new int[1];
    int[] depthBuffer = new int[1];

    public int texture;

    public int fboWidth;
    public int fboHeight;

    public FrameBuffer() {

    }

    public void createFrameBuffer(int width, int height) {
        ////http://www.songho.ca/opengl/gl_fbo.html

        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);

        fboWidth = width;
        fboHeight = height;

        int[] temp = new int[1];

        ////////////////////////////////////////////////////////////////////////
        // Generate framebuffer
        glGenFramebuffers(1, frameBuffer, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        ////////////////////////////////////////////////////////////////////////
        // Generate color renderbuffer
        glGenRenderbuffers(1, colorBuffer, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer[0]);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, MSAA, GL_RGBA8, fboWidth, fboHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colorBuffer[0]);
        ////////////////////////////////////////////////////////////////////////
        // Generate depth renderbuffer
        int[] w = new int[1];
        int[] h = new int[1];
        w[0] = fboWidth;
        h[0] = fboHeight;

        glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_WIDTH, w, 0);
        glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_HEIGHT, h, 0);

        glGenRenderbuffers(1, depthBuffer, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer[0]);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, MSAA, GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER,  depthBuffer[0]);
        ////////////////////////////////////////////////////////////////////////
        // generate texture
        glGenTextures(1, temp, 0);
        texture = temp[0];
        // Bind texture
        glBindTexture(GL_TEXTURE_2D, texture);

        // Define texture parameters
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboWidth, fboHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
        ////////////////////////////////////////////////////////////////////////

        // we are done, reset
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer[0]);
        glViewport(0, 0, fboWidth, fboHeight);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, fboWidth, fboHeight);
    }
}