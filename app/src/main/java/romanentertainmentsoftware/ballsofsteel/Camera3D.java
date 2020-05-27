package romanentertainmentsoftware.ballsofsteel;

import android.opengl.Matrix;
import android.util.Log;

import math3D.Matrix4x4;
import math3D.Plane3D;
import math3D.Vector3D;
import math3D.Vertex3D;

/**
 * Created by Roman Entertainment Software LLC on 5/8/2018.
 */

public class Camera3D {
    public Vertex3D world_position = new Vertex3D();
    public Vector3D angle = new Vector3D();

    public Vertex3D offset_position = new Vertex3D();
    public Vector3D offset_angle = new Vector3D();

    public Plane3D[] viewFrustum = new Plane3D[6];

    public float fov;
    public float screenWidth;
    public float screenHeight;
    public float aspectRatio;
    public float nearZ;
    public float farZ;

    public Vector3D upVector;
    public Vector3D rightVector;
    public Vector3D lookAtVector;

    private float test_x;
    private float test_z;
    private float ratio;

    public float[] viewMatrix = new float[16];
    public float[] invertedViewMatrix = new float[16];
    public float[] projectionMatrix = new float[16];

    public float current_angle;

    public Vertex3D fc;
    public Vertex3D ftl;
    public Vertex3D ftr;
    public Vertex3D fbl;
    public Vertex3D fbr;

    public Vertex3D nc;
    public Vertex3D ntl;
    public Vertex3D ntr;
    public Vertex3D nbl;
    public Vertex3D nbr;

    public Camera3D(){

    }

    public Camera3D(Vertex3D world_position, Vector3D angle){
        this.world_position = world_position;
        this.angle = angle;
    }

    public static Vector3D multiplyVector(float[] matrix, Vector3D vector){
        Vector3D output;
        float x = matrix[0] * vector.x + matrix[1] * vector.y + matrix[2] * vector.z + matrix[3] * vector.w;
        float y = matrix[4] * vector.x + matrix[5] * vector.y + matrix[6] * vector.z + matrix[7] * vector.w;
        float z = matrix[8] * vector.x + matrix[9] * vector.y + matrix[10] * vector.z + matrix[11] * vector.w;
        float w = matrix[12] * vector.x + matrix[13] * vector.y + matrix[14] * vector.z + matrix[15] * vector.w;

        output = new Vector3D(x, y, z, w);

        return output;
    }

    public void update(){
        float[] cameraTranslationMatrix = new float[16];
        float[] cameraRotateMatrixX = new float[16];
        float[] cameraRotateMatrixY = new float[16];
        float[] cameraRotateMatrixZ = new float[16];
        float[] cameraRotateMatrix = new float[16];

        Matrix.setIdentityM(cameraTranslationMatrix, 0);
        Matrix.setIdentityM(cameraRotateMatrixX, 0);
        Matrix.setIdentityM(cameraRotateMatrixY, 0);
        Matrix.setIdentityM(cameraRotateMatrixZ, 0);
        Matrix.setIdentityM(viewMatrix, 0);

        //Matrix.setLookAtM(viewMatrix, 0,
        //        0f, 0f, 1f,
        //        0.0f, 0.0f, 0.0f,
        //        0.0f, 1.0f, 0.0f);

        Matrix.rotateM(cameraRotateMatrixZ, 0, angle.z + offset_angle.z, 0f, 0f, 1f);
        Matrix.rotateM(cameraRotateMatrixX, 0, angle.x + offset_angle.x, 1f, 0f, 0f);
        Matrix.rotateM(cameraRotateMatrixY, 0, angle.y + offset_angle.y, 0f, 1f, 0f);

        Matrix.multiplyMM(cameraRotateMatrix, 0, cameraRotateMatrixZ, 0,cameraRotateMatrixX, 0);
        Matrix.multiplyMM(cameraRotateMatrix, 0, cameraRotateMatrix, 0,cameraRotateMatrixY, 0);

        //if (angle.y >= -90f && angle.y <= 90f)
        //    flip = 1f;
        //else
        //    flip = -1f;

        //test_x = offset_position.x * (float)Math.cos(angle.y * Math.PI / 180f) - offset_position.z * (float)Math.sin(angle.y * Math.PI / 180f);
        //test_z = offset_position.z * (float)Math.cos(angle.y * Math.PI / 180f) + offset_position.x * (float)Math.sin(angle.y * Math.PI / 180f);

        //Log.d("X: ", String.valueOf(test_x));
        //Log.d("Z: ", String.valueOf(test_z));

        Matrix.translateM(cameraTranslationMatrix, 0, -world_position.x + -offset_position.x, -world_position.y + -offset_position.y, -world_position.z + -offset_position.z);
        Matrix.multiplyMM(viewMatrix, 0, cameraRotateMatrix, 0,cameraTranslationMatrix, 0);

        upVector = new Vector3D(0.0f, 1.0f, 0.0f);
        rightVector = new Vector3D(1.0f, 0.0f, 0.0f);
        lookAtVector = new Vector3D(0.0f, 0.0f, -1.0f);

        upVector = multiplyVector(cameraRotateMatrix, upVector);
        rightVector = multiplyVector(cameraRotateMatrix, rightVector);
        lookAtVector = multiplyVector(cameraRotateMatrix, lookAtVector);

        upVector.normalize();
        rightVector.normalize();
        lookAtVector.normalize();

        final float NZ = 1f;
        final float FZ = 50f;

        float hNear = 2f * (float)Math.tan(Double.valueOf(fov) / 2.0) * NZ;
        float wNear = hNear * aspectRatio;
        float hFar = 2f * (float)Math.tan(Double.valueOf(fov) / 2.0) * FZ;
        float wFar = hFar * aspectRatio;

        float halfHNear = hNear * 0.5f;
        float halfWNear = wNear * 0.5f;
        float halfHFar = hFar * 0.5f;
        float halfWFar = wFar * 0.5f;

        fc = new Vertex3D(0f, 0f, 0f);
        ftl = new Vertex3D(0f, 0f, 0f);
        ftr = new Vertex3D(0f, 0f, 0f);
        fbl = new Vertex3D(0f, 0f, 0f);
        fbr = new Vertex3D(0f, 0f, 0f);

        nc = new Vertex3D(0f, 0f, 0f);
        ntl = new Vertex3D(0f, 0f, 0f);
        ntr = new Vertex3D(0f, 0f, 0f);
        nbl = new Vertex3D(0f, 0f, 0f);
        nbr = new Vertex3D(0f, 0f, 0f);

        fc.x = world_position.x + lookAtVector.x * FZ;
        fc.y = world_position.y + lookAtVector.y * FZ;
        fc.z = world_position.z + lookAtVector.z * FZ;

        nc.x = world_position.x + lookAtVector.x * NZ;
        nc.y = world_position.y + lookAtVector.y * NZ;
        nc.z = world_position.z + lookAtVector.z * NZ;

        Vertex3D upVector_halfHFar = new Vertex3D(upVector.x * halfHFar, upVector.y * halfHFar, upVector.z * halfHFar);
        Vertex3D rightVector_halfWFar = new Vertex3D(rightVector.x * halfWFar, rightVector.y * halfWFar, rightVector.z * halfWFar);
        Vertex3D upVector_halfHNear = new Vertex3D(upVector.x * halfHNear, upVector.y * halfHNear, upVector.z * halfHNear);
        Vertex3D rightVector_halfWNear = new Vertex3D(rightVector.x * halfWNear, rightVector.y * halfWNear, rightVector.z * halfWNear);

        ftl.x = fc.x + upVector_halfHFar.x - rightVector_halfWFar.x;
        ftl.y = fc.y + upVector_halfHFar.y - rightVector_halfWFar.y;
        ftl.z = fc.z + upVector_halfHFar.z - rightVector_halfWFar.z;

        ftr.x = fc.x + upVector_halfHFar.x + rightVector_halfWFar.x;
        ftr.y = fc.y + upVector_halfHFar.y + rightVector_halfWFar.y;
        ftr.z = fc.z + upVector_halfHFar.z + rightVector_halfWFar.z;

        fbl.x = fc.x - upVector_halfHFar.x - rightVector_halfWFar.x;
        fbl.y = fc.y - upVector_halfHFar.y - rightVector_halfWFar.y;
        fbl.z = fc.z - upVector_halfHFar.z - rightVector_halfWFar.z;

        fbr.x = fc.x - upVector_halfHFar.x + rightVector_halfWFar.x;
        fbr.y = fc.y - upVector_halfHFar.y + rightVector_halfWFar.y;
        fbr.z = fc.z - upVector_halfHFar.z + rightVector_halfWFar.z;

        ntl.x = nc.x + upVector_halfHNear.x - rightVector_halfWNear.x;
        ntl.y = nc.y + upVector_halfHNear.y - rightVector_halfWNear.y;
        ntl.z = nc.z + upVector_halfHNear.z - rightVector_halfWNear.z;

        ntr.x = nc.x + upVector_halfHNear.x + rightVector_halfWNear.x;
        ntr.y = nc.y + upVector_halfHNear.y + rightVector_halfWNear.y;
        ntr.z = nc.z + upVector_halfHNear.z + rightVector_halfWNear.z;

        nbl.x = nc.x - upVector_halfHNear.x - rightVector_halfWNear.x;
        nbl.y = nc.y - upVector_halfHNear.y - rightVector_halfWNear.y;
        nbl.z = nc.z - upVector_halfHNear.z - rightVector_halfWNear.z;

        nbr.x = nc.x - upVector_halfHNear.x + rightVector_halfWNear.x;
        nbr.y = nc.y - upVector_halfHNear.y + rightVector_halfWNear.y;
        nbr.z = nc.z - upVector_halfHNear.z + rightVector_halfWNear.z;

        viewFrustum[0] = new Plane3D(ftl, ftr, fbl); // Forward
        viewFrustum[1] = new Plane3D(ftr, ntr, fbr); // Right
        viewFrustum[2] = new Plane3D(ntr, ntl, nbr); // Back
        viewFrustum[3] = new Plane3D(ntl, ftl, nbl); // Right
        viewFrustum[4] = new Plane3D(ntl, ntr, ftl); // Up
        viewFrustum[5] = new Plane3D(fbl, fbr, nbl); // Down


        Matrix.invertM(invertedViewMatrix, 0, viewMatrix, 0);
    }

    public void release(){
        world_position = null;
        angle = null;

        viewMatrix = null;
        invertedViewMatrix = null;
        projectionMatrix = null;
    }
}
