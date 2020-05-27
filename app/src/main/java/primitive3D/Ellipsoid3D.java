package primitive3D;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

import math3D.Plane3D;
import math3D.Vector3D;
import math3D.Vertex3D;

public class Ellipsoid3D {
    public Vertex3D position;
    public Vertex3D radius;
    public Vertex3D gap;
    public Vertex3D vector_space_position;
    public Vertex3D vector_space_old_position;

    public Ellipsoid3D(){
        position = new Vertex3D();
        radius = new Vertex3D();
        gap = new Vertex3D();
        vector_space_position = new Vertex3D();
        vector_space_old_position = new Vertex3D();
    }

    public void create_vector_space(){
        vector_space_position.x = position.x / (radius.x + gap.x);
        vector_space_position.y = position.y / (radius.y + gap.y);
        vector_space_position.z = position.z / (radius.z + gap.z);
    }

    public boolean embedded_in_plane(Vertex3D pa, Vertex3D pb, Vertex3D pc) {
        Vector3D v1 = new Vector3D(pa, pb);
        Vector3D v2 = new Vector3D(pa, pc);

        Vertex3D origin = pa;
        Vector3D planeNormal = new Vector3D();
        planeNormal.setNormal(v1, v2);

        Vector3D planePointVector = new Vector3D(origin, vector_space_position);

        float d = -planeNormal.dotProduct(origin);

        float normalDotNormal = planeNormal.dotProduct(planeNormal);

        float t0 = -1 - (planePointVector.dotProduct(planeNormal)) / (float)Math.sqrt(normalDotNormal);
        float t1 = 1 - (planePointVector.dotProduct(planeNormal)) / (float)Math.sqrt(normalDotNormal);

        // Swap so t0 < t1
        if (t0 > t1) {
            float temp = t1;
            t1 = t0;
            t0 = temp;
        }

        // Check that at least one result is within range:
        if (t0 > 0.0f || t1 < 0.0f) {
            // Both t values are outside values [-1,1]
            // No collision possible:
            //Log.d ("Boundarys T0 T1", String.valueOf(t0) + ", " + String.valueOf(t1));
            return false;
        }

        // Clamp to [-1,1]
        //if (t0 < -1.0) t0 = -1.0f;
        //if (t1 < -1.0) t1 = -1.0f;
        //if (t0 > 1.0) t0 = 1.0f;
        //if (t1 > 1.0) t1 = 1.0f;


        //Log.d ("Boundarys T0 T1", String.valueOf(t0) + ", " + String.valueOf(t1));
        return true;
    }

    public Vertex3D getTriangleIntersectionPoint(Vertex3D pa, Vertex3D pb, Vertex3D pc) {

        Vector3D vector = new Vector3D(pa, vector_space_position);
        Vector3D segment_vector1 = new Vector3D(pa, pb);
        Vector3D segment_vector2 = new Vector3D(pa, pc);

        float segment_length1 = segment_vector1.length();
        float segment_length2 = segment_vector2.length();

        segment_vector1.normalize();
        segment_vector2.normalize();

        float t0 = segment_vector1.dotProduct(vector);
        float t1 = segment_vector2.dotProduct(vector);

        if (t0 < 0.0f) t0 = 0.0f;
        if (t1 < 0.0f) t1 = 0.0f;
        if (t0 > segment_length1) t0 = segment_length1;
        if (t1 > segment_length2) t1 = segment_length2;

        segment_vector1.x *= t0;
        segment_vector1.y *= t0;
        segment_vector1.z *= t0;

        segment_vector2.x *= t1;
        segment_vector2.y *= t1;
        segment_vector2.z *= t1;

        segment_vector1.x += pa.x;
        segment_vector1.y += pa.y;
        segment_vector1.z += pa.z;

        segment_vector2.x += segment_vector1.x;
        segment_vector2.y += segment_vector1.y;
        segment_vector2.z += segment_vector1.z;

        Vertex3D intersectionPoint = new Vertex3D(segment_vector2.x, segment_vector2.y, segment_vector2.z);

        return intersectionPoint;
    }

    public boolean CheckPointInTriangle(Vertex3D point, Vertex3D pa, Vertex3D pb, Vertex3D pc) {

        Vector3D e10 = new Vector3D(pa, pb);
        Vector3D e20 = new Vector3D(pa, pc);

        float a = e10.dotProduct(e10);
        float b = e10.dotProduct(e20);
        float c = e20.dotProduct(e20);
        float ac_bb = (a * c) - (b * b);

        Vector3D vp = new Vector3D(pa, point);

        float d = vp.dotProduct(e10);
        float e = vp.dotProduct(e20);
        float x = (d * c) - (e * b);
        float y = (e * a) - (d * b);
        float z = (x + y) - ac_bb;

        return z < 0.0f && x >= 0.0f && y >= 0.0f;
    }

    public boolean CheckEllipsoidInTriangle(Vertex3D pa, Vertex3D pb, Vertex3D pc, Vertex3D r1, Vertex3D r2, Vertex3D r3) {
        //TODO: THIS METHOD IS FUCKED UP MATHEMATICALLY!!!! FIX IT. I MEAN COMMON!!! 3 FUCKING RADIUSES FOR THE ELLIPSOID???

        Vector3D e10 = new Vector3D(new Vertex3D(pa.x + r1.x, pa.y + r1.y, pa.z + r1.z), new Vertex3D(pb.x + r2.x, pb.y + r2.y, pb.z + r2.z));
        Vector3D e20 = new Vector3D(new Vertex3D(pa.x + r1.x, pa.y + r1.y, pa.z + r1.z), new Vertex3D(pc.x + r3.x, pc.y + r3.y, pc.z + r2.z));

        float a = e10.dotProduct(e10);
        float b = e10.dotProduct(e20);
        float c = e20.dotProduct(e20);

        float ac_bb = (a * c) - (b * b);

        Vector3D vp = new Vector3D(new Vertex3D(pa.x + r1.x, pa.y + r1.y, pa.z + r1.z), vector_space_position);

        float d = vp.dotProduct(e10);
        float e = vp.dotProduct(e20);
        float x = (d * c) - (e * b);
        float y = (e * a) - (d * b);
        float z = (x + y) - ac_bb;

        //Log.d("Pos", String.valueOf(d) + ", " + String.valueOf(e));
        //Log.d("Pos", String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z));

        return z < 0.0f && x >= 0.0f && y >= 0.0f;
    }

    public boolean within_quad_boundary(Vertex3D vector_space_pa, Vertex3D vector_space_pb, Vertex3D vector_space_pc, Vertex3D vector_space_pd){
        boolean result_triangle_a = CheckEllipsoidInTriangle(vector_space_pa, vector_space_pb, vector_space_pc, new Vertex3D(- 1f, 1f, 0f), new Vertex3D(1f, 1f, 0f), new Vertex3D(- 1f, -1f, 0f));
        boolean result_triangle_b = CheckEllipsoidInTriangle(vector_space_pb, vector_space_pc, vector_space_pd, new Vertex3D(1f, 1f, 0f), new Vertex3D(-1f, -1f, 0f), new Vertex3D(1f, -1f, 0f));

        if (result_triangle_a == true || result_triangle_b == true)
            return true;
        else
            return false;
    }

    public boolean toQuadCollision(Vertex3D pa, Vertex3D pb, Vertex3D pc, Vertex3D pd) {

        Vector3D v1 = new Vector3D(pa, pb);
        Vector3D v2 = new Vector3D(pa, pc);
        Vertex3D origin = new Vertex3D(pa.x, pa.y, pa.z);
        Vector3D normal = new Vector3D();
        normal = v1._setNormal(v2);
        //Vector3D vector = new Vector3D(vector_space_position, origin);
        //Vertex3D intersectionPoint = getTriangleIntersectionPoint(pa, pb, pc);
        //Vector3D velocity = new Vector3D(vector_space_old_position, vector_space_position);
        float old_signed_distance = normal.dotProduct(vector_space_old_position) - (normal.dotProduct(origin));
        float signed_distance = normal.dotProduct(vector_space_position) - (normal.dotProduct(origin));

        Log.d("old signed distance", String.valueOf(old_signed_distance));
        Log.d("signed distance", String.valueOf(signed_distance));

        //Case 1: Perfect collision
        if (signed_distance >= 0.0f && signed_distance <= 1.0f) {
            if (within_quad_boundary(pa, pb, pc, pd)) {
                if (embedded_in_plane(pa, pb, pc) == true) {
                    //TODO: Check points and edges
                    // Point A
                    //float a = velocity.dotProduct(velocity);
                    //float b = 2.0f * vector.dotProduct(velocity);
                    //float c = vector.dotProduct(vector) - 1.0f;
                    //float discriminate = b * b - 4.0f * a * c;

                    //Log.d("velocity", String.valueOf(velocity.x) + ", " + String.valueOf(velocity.y) + ", " + String.valueOf(velocity.z));
                    //Log.d("discriminate", String.valueOf(discriminate));

                    Log.d("PERFECT RESULT ONE", "11111");

                    return true;
                }
            }
            else {
                Log.d("OUT PERFECT BOUNDARY", "11111");
                return false;
            }
        }
        else if (signed_distance >= -1.0f && signed_distance < 0.0f) {
            if (within_quad_boundary(pa, pb, pc, pd)) {
                if (embedded_in_plane(pa, pb, pc) == true) {
                    //TODO: Check points and edges
                    // Point A
                    //float a = velocity.dotProduct(velocity);
                    //float b = 2.0f * vector.dotProduct(velocity);
                    //float c = vector.dotProduct(vector) - 1.0f;
                    //float discriminate = b * b - 4.0f * a * c;

                    //Log.d("velocity", String.valueOf(velocity.x) + ", " + String.valueOf(velocity.y) + ", " + String.valueOf(velocity.z));
                    //Log.d("discriminate", String.valueOf(discriminate));

                    Log.d("PERFECT RESULT TWO", "22222");

                    return true;
                }
            }
            else {
                Log.d("OUT PERFECT BOUNDARY", "22222");
                return false;
            }
        }
        //Case 2: Piercing collision
        else if (old_signed_distance >= 0.0f && signed_distance < 0.0f) {
            if (within_quad_boundary(pa, pb, pc, pd)) {
                Log.d("PIERCE RESULT ONE", "11111");
                return true;
            }
            else {
                Log.d("OUT PIERCE BOUNDARY", "11111");
                return false;
            }
        }
        else if (old_signed_distance < 0.0f && signed_distance >= 0.0f) {
            if (within_quad_boundary(pa, pb, pc, pd)) {
                Log.d("PIERCE RESULT TWO", "22222");
                return true;
            }
            else {
                Log.d("OUT PIERCE BOUNDARY", "22222");
                return false;
            }
        }

        return false;
    }

    public Vertex3D toQuadCollisionResponse(Vertex3D vector_space_position, Vertex3D vector_space_old_position, Vertex3D vector_space_pa, Vertex3D vector_space_pb, Vertex3D vector_space_pc, Vertex3D vector_space_pd) {
        Vertex3D result = new Vertex3D();
        Vector3D v1 = new Vector3D(vector_space_pa, vector_space_pb);
        Vector3D v2 = new Vector3D(vector_space_pa, vector_space_pc);
        Vertex3D origin = new Vertex3D(vector_space_pa.x, vector_space_pa.y, vector_space_pa.z);
        Vector3D normal = new Vector3D();
        normal = v1._setNormal(v2);
        Vertex3D intersectionPoint = getTriangleIntersectionPoint(vector_space_pa, vector_space_pb, vector_space_pc);

        float old_signed_distance = normal.dotProduct(vector_space_old_position) - (normal.dotProduct(origin));
        float signed_distance = normal.dotProduct(vector_space_position) - (normal.dotProduct(origin));

        Log.d("", "COLLIDED!!!");
        Log.d("COL old signed distance", String.valueOf(old_signed_distance));
        Log.d("COL signed distance", String.valueOf(signed_distance));
        Log.d("normals", String.valueOf(normal.x));

        //TODO: Works but we are gonna need some sort of sliding plane i think

        if (within_quad_boundary(vector_space_pa, vector_space_pb, vector_space_pc, vector_space_pd)) {
            //Case 1: Perfect collision
            if (signed_distance >= 0.0f && signed_distance <= 1.0f) {
                result.x = intersectionPoint.x + (1.0f * normal.x);
                result.y = intersectionPoint.y + (1.0f * normal.y);
                result.z = intersectionPoint.z + (1.0f * normal.z);

                Log.d("COL PERFECT RESULT ONE", "11111");

                return result;
            }
            else if (signed_distance >= -1.0f && signed_distance < 0.0f) {
                result.x = intersectionPoint.x + (1.0f * normal.x);
                result.y = intersectionPoint.y + (1.0f * normal.y);
                result.z = intersectionPoint.z + (1.0f * normal.z);

                Log.d("COL PERFECT RESULT TWO", "22222");

                return result;
            }
            //Case 2: Piercing collision
            else if (old_signed_distance >= 0.0f && signed_distance < 0.0f)
            {
                result.x = vector_space_position.x - ((signed_distance - 1.0f) * normal.x);
                result.y = vector_space_position.y - ((signed_distance - 1.0f) * normal.y);
                result.z = vector_space_position.z - ((signed_distance - 1.0f) * normal.z);

                Log.d("COL PIERCE RESULT ONE", "11111");

                return result;

                //new_position = position + ((signed_distance - 1.0f) * -normal);
                //return new_position;
            }
            else if (old_signed_distance < 0.0f && signed_distance >= 0.0f)
            {
                result.x = vector_space_position.x - ((signed_distance + 1.0f) * normal.x);
                result.y = vector_space_position.y - ((signed_distance + 1.0f) * normal.y);
                result.z = vector_space_position.z - ((signed_distance + 1.0f) * normal.z);

                Log.d("COL PIERCE RESULT TWO", "22222");

                return result;

                //new_position = position + ((signed_distance + 1.0f) * -normal);
                //return new_position;
            }
        }

        //Log.d("NO COLLISION", "00000");

        return vector_space_position;
    }
}
