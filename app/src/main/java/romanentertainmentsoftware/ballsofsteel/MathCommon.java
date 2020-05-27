package romanentertainmentsoftware.ballsofsteel;

import math2D.Vector2D;
import math2D.Vertex2D;
import math3D.Vector3D;
import math3D.Vertex3D;

/**
 * Created by Roman Entertainment Software LLC on 4/26/2018.
 */

public class MathCommon {
    public static float csc(float data){
        float sin = (float)Math.sin((double)data);

        if (sin != 0f)
            return 1f / sin;
        else
            return 0.0000001f;
    }

    public static float sec(float data){
        float cos = (float)Math.cos((double)data);

        if (cos != 0f)
            return 1f / cos;
        else
            return 0.0000001f;
    }

    public static float cot(float data){
        float tan;
        if (data != Math.PI / 2f)
            tan = (float)Math.tan((double)data);
        else
            return 0.0000001f;

        if (tan != 0f)
            return 1f / tan;
        else
            return 0.0000001f;
    }

    public static float getRadian(float x1, float y1, float x2, float y2){
        float dx, dy;
        float angle = 0f;
        dx = x2 - x1;
        dy = -y2 - -y1;

        if (dx == 0f){
            if (dy == 0f)
                angle = 0f;
            else if (dy > 0f)
                angle = (float)Math.PI / 2f;
            else
                angle = (float)Math.PI * 3f / 2f;
        }
        else if (dy == 0f){
            if (dx > 0f)
                angle = 0f;
            else
                angle = (float)Math.PI;
        }
        else{
            if (dx < 0f)
                angle = (float)Math.atan(dy / dx) + (float)Math.PI;
            else if (dy < 0f)
                angle = (float)Math.atan(dy / dx) + 2f * (float)Math.PI;
            else
                angle = (float)Math.atan(dy / dx);
        }

        return angle;
    }

    public static float distanceVertexToLine2D(Vertex2D vertex, Vertex2D lineVertexA, Vertex2D lineVertexB){
        Vector2D v = new Vector2D(lineVertexB.x - lineVertexA.x, lineVertexB.y - lineVertexA.y);
        Vector2D w = new Vector2D(vertex.x - lineVertexA.x, vertex.y - lineVertexA.y);

        float c1 = w.x * v.x + w.y * v.y;
        float c2 = v.x * v.x + v.y * v.y;
        float b = c1 / c2;

        Vertex2D Pb = new Vertex2D(lineVertexA.x + b * v.x, lineVertexA.y + b * v.y);
        return (float)Math.sqrt(((vertex.x - Pb.x) * (vertex.x - Pb.x)) +
                ((vertex.y - Pb.y) * (vertex.y - Pb.y)));
    }


    public static float distanceVertexToLine3D(Vertex3D vertex, Vertex3D lineVertexA, Vertex3D lineVertexB){
        Vector3D v = new Vector3D(lineVertexB.x - lineVertexA.x, lineVertexB.y - lineVertexA.y, lineVertexB.z - lineVertexA.z);
        Vector3D w = new Vector3D(vertex.x - lineVertexA.x, vertex.y - lineVertexA.y, vertex.z - lineVertexA.z);

        float c1 = w.x * v.x + w.y * v.y + w.z * v.z;
        float c2 = v.x * v.x + v.y * v.y + v.z * v.z;
        float b = c1 / c2;

        Vertex3D Pb = new Vertex3D(lineVertexA.x + b * v.x, lineVertexA.y + b * v.y, lineVertexA.z + b * v.z);
        return (float)Math.sqrt(((vertex.x - Pb.x) * (vertex.x - Pb.x)) +
                                ((vertex.y - Pb.y) * (vertex.y - Pb.y)) +
                                ((vertex.z - Pb.z) * (vertex.z - Pb.z)));
    }

    public static float distanceVertexToSegment3D(Vertex3D vertex, Vertex3D lineVertexA, Vertex3D lineVertexB)
    {
        Vector3D v = new Vector3D(lineVertexB.x - lineVertexA.x, lineVertexB.y - lineVertexA.y, lineVertexB.z - lineVertexA.z);
        Vector3D w = new Vector3D(vertex.x - lineVertexA.x, vertex.y - lineVertexA.y, vertex.z - lineVertexA.z);

        float c1 = w.x * v.x + w.y * v.y + w.z * v.z;
        if ( c1 <= 0f )
            return (float)Math.sqrt(((vertex.x - lineVertexA.x) * (vertex.x - lineVertexA.x)) +
                                    ((vertex.y - lineVertexA.y) * (vertex.y - lineVertexA.y)) +
                                    ((vertex.z - lineVertexA.z) * (vertex.z - lineVertexA.z)));

        float c2 = v.x * v.x + v.y * v.y + v.z * v.z;
        if ( c2 <= c1 )
            return (float)Math.sqrt(((vertex.x - lineVertexB.x) * (vertex.x - lineVertexB.x)) +
                                    ((vertex.y - lineVertexB.y) * (vertex.y - lineVertexB.y)) +
                                    ((vertex.z - lineVertexB.z) * (vertex.z - lineVertexB.z)));

        float b = c1 / c2;

        Vertex3D Pb = new Vertex3D(lineVertexA.x + b * v.x, lineVertexA.y + b * v.y, lineVertexA.z + b * v.z);
        return (float)Math.sqrt(((vertex.x - Pb.x) * (vertex.x - Pb.x)) +
                ((vertex.y - Pb.y) * (vertex.y - Pb.y)) +
                ((vertex.z - Pb.z) * (vertex.z - Pb.z)));
    }
}
