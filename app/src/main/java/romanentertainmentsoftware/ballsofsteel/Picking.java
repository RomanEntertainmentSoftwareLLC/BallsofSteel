package romanentertainmentsoftware.ballsofsteel;

import android.opengl.Matrix;
import android.util.Log;
import math3D.Vertex3D;

/**
 * Created by Roman Entertainment Software LLC on 5/18/2018.
 */
public class Picking {

    public Picking(){

    }

    public static float[] transform(float[] matrix, float[] vector) {

        float[] result = new float[4];

        float x = matrix[0] * vector[0] + matrix[4] * vector[1] + matrix[8] * vector[2] + matrix[12] * vector[3];
        float y = matrix[1] * vector[0] + matrix[5] * vector[1] + matrix[9] * vector[2] + matrix[13] * vector[3];
        float z = matrix[2] * vector[0] + matrix[6] * vector[1] + matrix[10] * vector[2] + matrix[14] * vector[3];
        float w = matrix[3] * vector[0] + matrix[7] * vector[1] + matrix[11] * vector[2] + matrix[15] * vector[3];

        result[0] = x;
        result[1] = y;
        result[2] = z;
        result[3] = w;

        return result;
    }

    public static float[] getNormalizedDeviceCoords(float touchX, float touchY){
        float[] result = new float[2];
        result[0] = (2f * touchX) / Render.camera[0].screenWidth - 1f;
        result[1] = 1f - (2f * touchY) / Render.camera[0].screenHeight;

        return result;
    }

    public static float[] getEyeCoords(Camera3D camera, float[] clipCoords){
        float[] invertedProjection = new float[16];
        Matrix.invertM(invertedProjection, 0, camera.projectionMatrix, 0);

        float[] eyeCoords = new float[4];
        //eyeCoords = transform(invertedProjection, clipCoords);
        Matrix.multiplyMV(eyeCoords, 0, invertedProjection, 0 ,clipCoords, 0);


        float[] result = new float[]{eyeCoords[0], eyeCoords[1], -1f, 0f};

        return result;
    }

    public static float[] getWorldCoords(Camera3D camera, float[] eyeCoords){
        float[] invertedViewMatrix = new float[16];
        Matrix.invertM(invertedViewMatrix, 0, camera.viewMatrix, 0);

        float[] rayWorld = new float[4];
        //rayWorld = transform(invertedViewMatrix, eyeCoords);
        //rayWorld[0] = eyeCoords[0];
        //rayWorld[1] = eyeCoords[1];
        //rayWorld[2] = eyeCoords[2];
        Matrix.multiplyMV(rayWorld, 0, invertedViewMatrix, 0 ,eyeCoords, 0);

        float length = (float)Math.sqrt(rayWorld[0] * rayWorld[0] +
                                        rayWorld[1] * rayWorld[1] +
                                        rayWorld[2] * rayWorld[2]);
        if(length != 0){
            rayWorld[0] /= length;
            rayWorld[1] /= length;
            rayWorld[2] /= length;
        }

        return rayWorld;
    }

    public static float[] calculateMouseRay(Camera3D camera){
        float[] worldCoords = new float[1];

        if (MainActivity.pointerCount <= Constants.MAX_FINGERS) {
            float touchX = MainActivity.touch[0].x;
            float touchY = MainActivity.touch[0].y;

            float[] normalizedDeviceCoords = getNormalizedDeviceCoords(touchX, touchY);
            float[] homogeneousClipCoords = new float[]{normalizedDeviceCoords[0], normalizedDeviceCoords[1], -1f, 1f};
            float[] eyeCoords = getEyeCoords(camera, homogeneousClipCoords);
            worldCoords = getWorldCoords(camera, eyeCoords);

        }

        return worldCoords;

    }

    public static boolean getRaySphereIntersection(float[] rayOrigin, float[] spherePosition, float[] rayDirection, float radius){
        float[] vector = new float[4];

        // Calculate the a, b, c and d coefficients.
        // a = (XB-XA)^2 + (YB-YA)^2 + (ZB-ZA)^2
        // b = 2 * ((XB-XA)(XA-XC) + (YB-YA)(YA-YC) + (ZB-ZA)(ZA-ZC))
        // c = (XA-XC)^2 + (YA-YC)^2 + (ZA-ZC)^2 - r^2
        // d = b^2 - 4*a*c

        //Create a vector between the origin and the sphere position
        vector[0] = rayOrigin[0] - spherePosition[0];
        vector[1] = rayOrigin[1] - spherePosition[1];
        vector[2] = rayOrigin[2] - spherePosition[2];

        //Calculate the coefficients of a, b, c, and d
        float a = (rayDirection[0] * rayDirection[0]) +
                  (rayDirection[1] * rayDirection[1]) +
                  (rayDirection[2] * rayDirection[2]);
        float b = (rayDirection[0] * vector[0] +
                   rayDirection[1] * vector[1] +
                   rayDirection[2] * vector[2]) * 2f;
        float c = (vector[0] * vector[0] +
                   vector[1] * vector[1] +
                   vector[2] * vector[2]) - (radius * radius);

        // Find the discriminant.
        float d = (b * b) - (4.0f * a * c);

        if (d == 0f) {
            //one root
        }
        else if (d > 0f) {
            //two roots
            float x1 = -b + (float)Math.sqrt(d) / (2f * a);
            float x2 = -b - (float)Math.sqrt(d) / (2f * a);

            if ((x1 >= 0f) || (x2 >= 0f)){
                return true;
            }

            if ((x1 < 0f) || (x2 >= 0f)){
                return true;
            }
        }

        return false;
    }

    public static boolean getRayEllipsoidIntersection(float[] rayOrigin, float[] spherePosition, float[] rayDirection, float[] radius){
        float[] vectorSpaceRayOrigin = new float[4];
        float[] vectorSpaceSpherePosition = new float[4];
        float[] vector = new float[4];
        float[] vectorSpaceDirection = new float[4];

        // Calculate the a, b, c and d coefficients.
        // a = (XB-XA)^2 + (YB-YA)^2 + (ZB-ZA)^2
        // b = 2 * ((XB-XA)(XA-XC) + (YB-YA)(YA-YC) + (ZB-ZA)(ZA-ZC))
        // c = (XA-XC)^2 + (YA-YC)^2 + (ZA-ZC)^2 - r^2
        // d = b^2 - 4*a*c

        //Convert the ray origin into vector space by dividing it by a 3D radius
        vectorSpaceRayOrigin[0] = rayOrigin[0] / radius[0];
        vectorSpaceRayOrigin[1] = rayOrigin[1] / radius[1];
        vectorSpaceRayOrigin[2] = rayOrigin[2] / radius[2];

        //Convert the sphere position into vector space by dividing it by a 3D radius
        vectorSpaceSpherePosition[0] = spherePosition[0] / radius[0];
        vectorSpaceSpherePosition[1] = spherePosition[1] / radius[1];
        vectorSpaceSpherePosition[2] = spherePosition[2] / radius[2];

        //Create a vector between the origin and the sphere position
        vector[0] = vectorSpaceRayOrigin[0] - vectorSpaceSpherePosition[0];
        vector[1] = vectorSpaceRayOrigin[1] - vectorSpaceSpherePosition[1];
        vector[2] = vectorSpaceRayOrigin[2] - vectorSpaceSpherePosition[2];

        //Yes even the ray direction needs converted to vector space. Didn't work without it
        vectorSpaceDirection[0] = rayDirection[0] / radius[0];
        vectorSpaceDirection[1] = rayDirection[1] / radius[1];
        vectorSpaceDirection[2] = rayDirection[2] / radius[2];

        //Calculate the coefficients of a, b, c, and d
        float a = (vectorSpaceDirection[0] * vectorSpaceDirection[0]) +
                  (vectorSpaceDirection[1] * vectorSpaceDirection[1]) +
                  (vectorSpaceDirection[2] * vectorSpaceDirection[2]);
        float b = (vectorSpaceDirection[0] * vector[0] +
                   vectorSpaceDirection[1] * vector[1] +
                   vectorSpaceDirection[2] * vector[2]) * 2.0f;
        float c = (vector[0] * vector[0] +
                   vector[1] * vector[1] +
                   vector[2] * vector[2]) - 1f;

        // Find the discriminant.
        float d = (b * b) - (4.0f * a * c);

        if (d == 0f) {
            //one root
        }
        else if (d > 0f) {
            //two roots
            float x1 = -b + (float)Math.sqrt(d) / (2.0f * a);
            float x2 = -b - (float)Math.sqrt(d) / (2.0f * a);

            if ((x1 >= 0f) || (x2 >= 0f)){
                return true;
            }

            if ((x1 < 0f) || (x2 >= 0f)){
                return true;
            }
        }

        return false;
    }

    public static float[] getPointOnRay(Camera3D camera, float[] ray, float distance) {
        float[] result = new float[4];
        float[] start = new float[]{ camera.world_position.x, camera.world_position.y, camera.world_position.z };
        float[] scaledRay = new float[]{ ray[0] * distance, ray[1] * distance, ray[2] * distance };

        result[0] = start[0] + scaledRay[0];
        result[1] = start[1] + scaledRay[1];
        result[2] = start[2] + scaledRay[2];
        result[3] = 1f;

        start = null;
        scaledRay = null;

        return result;
    }

    public static float[] getPointOnRayVectorSpace(Camera3D camera, float[] ray, float distance,  float[] radius) {
        float[] result = new float[3];
        float[] vectorSpaceRay = new float[3];
        float[] vectorSpaceDistance = new float[3];
        float[] vectorSpaceStart = new float[3];
        float[] vectorSpaceScaledRay = new float[4];

        vectorSpaceRay[0] = ray[0] / radius[0];
        vectorSpaceRay[1] = ray[1] / radius[1];
        vectorSpaceRay[2] = ray[2] / radius[2];

        vectorSpaceDistance[0] = distance / radius[0];
        vectorSpaceDistance[1] = distance / radius[1];
        vectorSpaceDistance[2] = distance / radius[2];

        vectorSpaceStart[0] = camera.world_position.x / radius[0];
        vectorSpaceStart[1] = camera.world_position.y / radius[1];
        vectorSpaceStart[2] = camera.world_position.z / radius[2];

        vectorSpaceScaledRay[0] = vectorSpaceRay[0] * distance;
        vectorSpaceScaledRay[1] = vectorSpaceRay[1] * distance;
        vectorSpaceScaledRay[2] = vectorSpaceRay[2] * distance;

        result[0] = vectorSpaceStart[0] + vectorSpaceScaledRay[0];
        result[1] = vectorSpaceStart[1] + vectorSpaceScaledRay[1];
        result[2] = vectorSpaceStart[2] + vectorSpaceScaledRay[2];

        vectorSpaceRay = null;
        vectorSpaceDistance = null;
        vectorSpaceStart = null;
        vectorSpaceScaledRay = null;

        return result;
    }

    public static boolean intersectionInRangeSphere(Camera3D camera, float rayStart, float rayFinish, float[] rayDirection, float[] position, float radius) {
        float[] startPoint = getPointOnRay(camera, rayDirection, rayStart);
        float[] endPoint = getPointOnRay(camera, rayDirection, rayFinish);

        float distance = MathCommon.distanceVertexToSegment3D(new Vertex3D(position[0], position[1], position[2]),
                                                              new Vertex3D(startPoint[0], startPoint[1], startPoint[2]),
                                                              new Vertex3D(endPoint[0], endPoint[1], endPoint[2]));
        if (distance <= radius)
            return true;

        return false;
    }

    public static boolean intersectionInRangeEllipsoid(Camera3D camera, float rayStart, float rayFinish, float[] rayDirection, float[] position, float[] radius) {
        float[] vectorSpaceStartPoint;
        float[] vectorSpaceEndPoint;
        float[] vectorSpacePosition = new float[3];

        vectorSpaceStartPoint = getPointOnRayVectorSpace(camera, rayDirection, rayStart, radius);
        vectorSpaceEndPoint = getPointOnRayVectorSpace(camera, rayDirection, rayFinish, radius);

        vectorSpacePosition[0] = position[0] / radius[0];
        vectorSpacePosition[1] = position[1] / radius[1];
        vectorSpacePosition[2] = position[2] / radius[2];

        float distance = MathCommon.distanceVertexToSegment3D(new Vertex3D(vectorSpacePosition[0], vectorSpacePosition[1], vectorSpacePosition[2]),
                                                              new Vertex3D(vectorSpaceStartPoint[0], vectorSpaceStartPoint[1], vectorSpaceStartPoint[2]),
                                                              new Vertex3D(vectorSpaceEndPoint[0], vectorSpaceEndPoint[1], vectorSpaceEndPoint[2]));

        vectorSpaceStartPoint = null;
        vectorSpaceEndPoint = null;
        vectorSpacePosition = null;

        if (distance <= 1f)
            return true;

        return false;
    }
}
