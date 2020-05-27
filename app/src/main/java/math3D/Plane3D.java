package math3D;

import android.util.Log;

/**
 * Created by Roman Entertainment Software LLC on 5/17/2018.
 */

interface iPlane3DTools{
    void copy(Plane3D plane);
    void set(Vertex3D pa, Vertex3D pb, Vertex3D pc);
    void print();

    Plane3D _copy();
    Plane3D _copy(Plane3D plane);
    Plane3D _set(Vertex3D pa, Vertex3D pb, Vertex3D pc);
}

interface iPlane3D{
    float signedDistanceTo(Vertex3D point);
    float signedDistanceTo(Vertex3D sphere, float radius);
    Vector3D reflect(Vector3D vector);
    boolean CheckPointInTriangle(Vertex3D point);
}

public class Plane3D implements iPlane3DTools, iPlane3D{
    private Vertex3D pa = new Vertex3D();
    private Vertex3D pb = new Vertex3D();
    private Vertex3D pc = new Vertex3D();

    public float a;
    public float b;
    public float c;
    public float d;

    private Vertex3D origin = new Vertex3D();
    private Vector3D normal = new Vector3D();

    public Plane3D(){

    }

    public Plane3D(Vertex3D pa, Vertex3D pb, Vertex3D pc){
        this.pa = pa;
        this.pb = pb;
        this.pc = pc;

        Vector3D v1 = new Vector3D(pa, pb);
        Vector3D v2 = new Vector3D(pa, pc);

        this.origin = pa;
        this.normal = new Vector3D();
        this.normal.setNormal(v1, v2);

        this.a = this.normal.x;
        this.b = this.normal.y;
        this.c = this.normal.z;
        this.d = -(this.normal.x * this.origin.x + this.normal.y * this.origin.y + this.normal.z * this.origin.z);
    }

    @Override
    public void copy(Plane3D plane){
        this.pa.copy(plane.pa);
        this.pb.copy(plane.pb);
        this.pc.copy(plane.pc);

        this.a = plane.a;
        this.b = plane.b;
        this.c = plane.c;
        this.d = plane.d;

        this.origin.copy(plane.origin);
        this.normal.copy(plane.normal);
    }

    @Override
    public void set(Vertex3D pa, Vertex3D pb, Vertex3D pc){
        this.pa = pa;
        this.pb = pb;
        this.pc = pc;

        Vector3D v1 = new Vector3D(pa, pb);
        Vector3D v2 = new Vector3D(pa, pc);

        this.origin = pa;
        this.normal = new Vector3D();
        this.normal.setNormal(v1, v2);

        this.a = this.normal.x;
        this.b = this.normal.y;
        this.c = this.normal.z;
        this.d = -(this.normal.x * this.origin.x + this.normal.y * this.origin.y + this.normal.z * this.origin.z);
    }

    @Override
    public void print(){
        Log.d("Plane3D", this.a + ", " + this.b + ", " + this.c + ", " + this.d);
    }

    @Override
    public Plane3D _copy(){
        Plane3D output = new Plane3D();

        output.pa.copy(this.pa);
        output.pb.copy(this.pb);
        output.pc.copy(this.pc);

        output.a = this.a;
        output.b = this.b;
        output.c = this.c;
        output.d = this.d;

        output.origin.copy(this.origin);
        output.normal.copy(this.normal);

        return output;
    }

    @Override
    public Plane3D _copy(Plane3D plane){
        Plane3D output = new Plane3D();

        output.pa.copy(plane.pa);
        output.pb.copy(plane.pb);
        output.pc.copy(plane.pc);

        output.a = plane.a;
        output.b = plane.b;
        output.c = plane.c;
        output.d = plane.d;

        output.origin.copy(plane.origin);
        output.normal.copy(plane.normal);

        return output;
    }

    @Override
    public Plane3D _set(Vertex3D pa, Vertex3D pb, Vertex3D pc){
        Plane3D output = new Plane3D();

        output.pa = pa;
        output.pb = pb;
        output.pc = pc;

        Vector3D v1 = new Vector3D(pa, pb);
        Vector3D v2 = new Vector3D(pa, pc);

        output.origin = pa;
        output.normal = new Vector3D();
        output.normal.setNormal(v1, v2);

        output.a = output.normal.x;
        output.b = output.normal.y;
        output.c = output.normal.z;
        output.d = -(output.normal.x * output.origin.x + output.normal.y * output.origin.y + output.normal.z * output.origin.z);

        return output;
    }

    @Override
    public float signedDistanceTo(Vertex3D point){
        float dotProduct = (this.normal.x * point.x + this.normal.y * point.y + this.normal.z * point.z);

        return dotProduct + this.d;
    }

    @Override
    public float signedDistanceTo(Vertex3D sphere, float radius){
        float dotProduct = (this.normal.x * sphere.x + this.normal.y * sphere.y + this.normal.z * sphere.z);

        return dotProduct + this.d + -radius;
    }

    @Override
    public Vector3D reflect(Vector3D vector){
        Vector3D output = new Vector3D();

        float dotProduct = (this.a * vector.x + this.b * vector.y + this.c * vector.z);

        //reflect(vector, N) = vector - 2.0 * dot(N, vector) * N

        output.x = vector.x - (2f * dotProduct) * this.a;
        output.y = vector.y - (2f * dotProduct) * this.b;
        output.z = vector.z - (2f * dotProduct) * this.c;

        return output;
    }

    @Override
    public boolean CheckPointInTriangle(Vertex3D point){

        Vector3D e10 = new Vector3D(this.pa, this.pb);
        Vector3D e20 = new Vector3D(this.pa, this.pc);

        float a = e10.dotProduct(e10);
        float b = e10.dotProduct(e20);
        float c = e20.dotProduct(e20);
        float ac_bb = (a * c) - (b * b);

        Vector3D vp = new Vector3D(this.origin, point);

        float d = vp.dotProduct(e10);
        float e = vp.dotProduct(e20);
        float x = (d * c) - (e * b);
        float y = (e * a) - (d * b);
        float z = (x + y) - ac_bb;

        return z < 0.0f && x >= 0.0f && y >= 0.0f;
    }
}
