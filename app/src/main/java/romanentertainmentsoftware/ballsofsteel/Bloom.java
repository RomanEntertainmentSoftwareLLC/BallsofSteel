package romanentertainmentsoftware.ballsofsteel;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_STENCIL_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glUseProgram;

public class Bloom {

    public Bloom() {

    }

    public static void createGaussianWeights(BlurBuffer buffer) {
        float sum = 0.0f;
        float x;
        float threshold_squared = buffer.sigma * buffer.sigma;
        float two_times_threshold_squared = 2.0f * threshold_squared;
        buffer.mu = (float)(buffer.kernel_diameter - 1) / 2.0f;

        // Calculate Gaussian weights based on the kernel diameter
        for (int i = 0; i < buffer.kernel_diameter; i++) {
            x = (i - ((buffer.kernel_diameter - 1) / 2.0f));
            buffer.weights[i] = (1.0f / (float)Math.sqrt(Math.PI * two_times_threshold_squared)) * (float)Math.exp(-1.0 * (x * x) / two_times_threshold_squared);
            sum += buffer.weights[i];
        }

        // Normalize the weights
        for (int i = 0; i < buffer.kernel_diameter; i++) {
            buffer.weights[i] /= sum;
        }
    }

    public static void renderToExposureToneMappingFrameBuffer() {
        // Purpose: Tone mapping is a technique used in image processing and computer graphics to
        //          map one set of colors to another to approximate the appearance of high
        //          dynamic range images in a medium that has a more limited dynamic range.
        //
        //          Camera exposure is the overall brightness or darkness of a photograph. More
        //          specifically, it’s the amount of light that reaches the film or camera sensor
        //          when a picture is being taken. The more you expose the film or camera sensor to
        //          light, the lighter your photo will be. The less light, the darker your photo
        //          will be.
        //
        //          This combines both methods in order to allow a high dynamic range of colors
        //          from the original buffer onto this new framebuffer by the use of exposure
        //          tone mapping.
        Render.exposureToneMappingFrameBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glUseProgram(Render.programExposureToneMapping);
        Render.exposureToneMappingBuffer.update();

        Render.exposure_tone_mapped_polygon.bindData();
        Render.exposure_tone_mapped_polygon.position.x = 0.0f;
        Render.exposure_tone_mapped_polygon.position.y = 0.0f;
        Render.exposure_tone_mapped_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.exposure_tone_mapped_polygon.setTexture(Render.frameBuffer.texture);
        Render.exposure_tone_mapped_polygon.rgba(1f,1f,1f, 1f);
        Render.exposure_tone_mapped_polygon.draw();

        Render.exposureToneMappingFrameBuffer.unbind();
    }

    public static void renderToBrightFilterFrameBuffer() {
        // Purpose: Obtains the brightest color chosen from the exposure tone mapped framebuffer
        //          and plots only those colors onto a new framebuffer
        Render.brightFilterFrameBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glUseProgram(Render.programBrightFilter);

        Render.brightFilterBuffer.update();

        Render.bright_filter_polygon.bindData();
        Render.bright_filter_polygon.position.x = 0.0f;
        Render.bright_filter_polygon.position.y = 0.0f;
        Render.bright_filter_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.bright_filter_polygon.setTexture(Render.exposureToneMappingFrameBuffer.texture);
        Render.bright_filter_polygon.rgba(1f,1f,1f, 1f);
        Render.bright_filter_polygon.draw();

        Render.brightFilterFrameBuffer.unbind();
    }

    public static void renderToBlurSetup() {
        Render.verticalBlurFrameBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glUseProgram(Render.programTextured);
        Render.fullscreen_rendered_polygon.bindData();
        Render.fullscreen_rendered_polygon.position.x = 0.0f;
        Render.fullscreen_rendered_polygon.position.y = 0.0f;
        Render.fullscreen_rendered_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.fullscreen_rendered_polygon.setTexture(Render.brightFilterFrameBuffer.texture);
        Render.fullscreen_rendered_polygon.rgba(1f,1f,1f, 1f);
        Render.fullscreen_rendered_polygon.draw();

        Render.verticalBlurFrameBuffer.unbind();
    }

    public static void renderToHorizontalBlurFrameBuffer() {
        Render.horizontalBlurFrameBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glUseProgram(Render.programHorizontalBlur);

        Render.blurBuffer.update_horizontal_blur();

        Render.horizontal_blur_buffer_polygon.bindData();
        Render.horizontal_blur_buffer_polygon.position.x = 0.0f;
        Render.horizontal_blur_buffer_polygon.position.y = 0.0f;
        Render.horizontal_blur_buffer_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.horizontal_blur_buffer_polygon.setTexture(Render.verticalBlurFrameBuffer.texture);
        Render.horizontal_blur_buffer_polygon.rgba(1f,1f,1f, 1f);
        Render.horizontal_blur_buffer_polygon.draw();

        Render.horizontalBlurFrameBuffer.unbind();
    }

    public static void renderToVerticalBlurFrameBuffer() {
        Render.verticalBlurFrameBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glUseProgram(Render.programVerticalBlur);

        Render.blurBuffer.update_vertical_blur();

        Render.vertical_blur_buffer_polygon.bindData();
        Render.vertical_blur_buffer_polygon.position.x = 0.0f;
        Render.vertical_blur_buffer_polygon.position.y = 0.0f;
        Render.vertical_blur_buffer_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.vertical_blur_buffer_polygon.setTexture(Render.horizontalBlurFrameBuffer.texture);
        Render.vertical_blur_buffer_polygon.rgba(1f,1f,1f, 1f);
        Render.vertical_blur_buffer_polygon.draw();

        Render.verticalBlurFrameBuffer.unbind();
    }

    public static void renderToContrastBoostFrameBuffer() {
        Render.contrastBoostFrameBuffer.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glUseProgram(Render.programContrastBoost);

        Render.contrastBoostBuffer.update();

        Render.contrast_boost_polygon.bindData();
        Render.contrast_boost_polygon.position.x = 0.0f;
        Render.contrast_boost_polygon.position.y = 0.0f;
        Render.contrast_boost_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.contrast_boost_polygon.setTexture(Render.verticalBlurFrameBuffer.texture);
        Render.contrast_boost_polygon.rgba(1f,1f,1f, 1f);
        Render.contrast_boost_polygon.draw();

        Render.contrastBoostFrameBuffer.unbind();
    }

    public static void renderToBloomFrameBuffer() {
        Render.bloomFrameBuffer.bind();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glUseProgram(Render.programMultiTexture);

        Render.fullscreen_rendered_multitextured_polygon.bindData();
        Render.fullscreen_rendered_multitextured_polygon.position.x = 0.0f;
        Render.fullscreen_rendered_multitextured_polygon.position.y = 0.0f;
        Render.fullscreen_rendered_multitextured_polygon.updateMatrices(Render.projectionMatrix2D, Render.camera[0].aspectRatio, 0f);
        Render.fullscreen_rendered_multitextured_polygon.setTextures(Render.contrastBoostFrameBuffer.texture, Render.frameBuffer.texture);
        Render.fullscreen_rendered_multitextured_polygon.rgba(1f,1f,1f, 1f);
        Render.fullscreen_rendered_multitextured_polygon.draw();

        Render.bloomFrameBuffer.unbind();
    }

    public static void bloomEffect(){
        renderToExposureToneMappingFrameBuffer();
        renderToBrightFilterFrameBuffer();
        renderToBlurSetup();

        for (int i = 0; i < Render.blurBuffer.number_of_times_blurred; i++) {
            renderToHorizontalBlurFrameBuffer();
            renderToVerticalBlurFrameBuffer();
        }

        renderToContrastBoostFrameBuffer();
        renderToBloomFrameBuffer();
    }
}
