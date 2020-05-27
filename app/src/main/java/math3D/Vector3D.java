package math3D;

import android.util.Log;

/**
 * Created by Roman Entertainment Software LLC on 5/3/2018.
 */

interface iVector3DTools{
    void copy(Vector3D vector);
    void set(float x, float y , float z);
    void set(float x, float y, float z, float w);
    void set(Vertex3D vertexA, Vertex3D vertexB);
    void print();

    Vector3D _copy();
    Vector3D _copy(Vector3D vector);
    Vector3D _set(float x, float y, float z);
    Vector3D _set(float x, float y, float z, float w);
    Vector3D _set(Vertex3D vertexA, Vertex3D vertexB);
}

interface iVector3D{
    float length();
    float lengthSquared();
    void normalize();
    float dotProduct(Vector3D vector);
    float dotProduct(Vertex3D vertex);
    void crossProduct(Vector3D vectorA, Vector3D vectorB);
    void setNormal(Vector3D vectorA, Vector3D vectorB);
    void add(Vector3D vector);
    void subtract(Vector3D vector);

    Vector3D _normalize();
    Vector3D _crossProduct(Vector3D vectorA, Vector3D vectorB);
    Vector3D _setNormal(Vector3D vector);
    Vector3D _setNormal(Vector3D vectorA, Vector3D vectorB);
    Vector3D _add(Vector3D vector);
    Vector3D _subtract(Vector3D vector);
}

public class Vector3D implements iVector3DTools, iVector3D{
    public float x;
    public float y;
    public float z;
    public float w;

    private final float epsilon = 0.0000000001f;

    public Vector3D(){

    }

    public Vector3D(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0f;
    }

    public Vector3D(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector3D(Vertex3D vertexA, Vertex3D vertexB){
        this.x = vertexB.x - vertexA.x;
        this.y = vertexB.y - vertexA.y;
        this.z = vertexB.z - vertexA.z;
        this.w = 0f;
    }

    @Override
    public void copy(Vector3D vector){
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        this.w = vector.w;
    }

    @Override
    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0f;
    }

    @Override
    public void set(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void set(Vertex3D vertexA, Vertex3D vertexB){
        this.x = vertexB.x - vertexA.x;
        this.y = vertexB.y - vertexA.y;
        this.z = vertexB.z - vertexA.z;
        this.w = 0f;
    }

    public void print(){
        Log.d("Vector3D", this.x + ", " + this.y + ", " + this.z + ", " + this.w);
    }

    @Override
    public Vector3D _copy(){
        Vector3D output = new Vector3D();

        output.x = this.x;
        output.y = this.y;
        output.z = this.z;
        output.w = this.w;

        return output;
    }

    @Override
    public Vector3D _copy(Vector3D vector){
        Vector3D output = new Vector3D();

        output.x = vector.x;
        output.y = vector.y;
        output.z = vector.z;
        output.w = vector.w;

        return output;
    }

    @Override
    public Vector3D _set(float x, float y, float z){
        Vector3D output = new Vector3D();

        output.x = x;
        output.y = y;
        output.z = z;
        output.w = 0f;

        return output;
    }

    @Override
    public Vector3D _set(float x, float y, float z, float w){
        Vector3D output = new Vector3D();

        output.x = x;
        output.y = y;
        output.z = z;
        output.w = w;

        return output;
    }

    @Override
    public Vector3D _set(Vertex3D vertexA, Vertex3D vertexB){
        Vector3D output = new Vector3D();

        output.x = vertexB.x - vertexA.x;
        output.y = vertexB.y - vertexA.y;
        output.z = vertexB.z - vertexA.z;
        output.w = 0f;

        return output;
    }

    @Override
    public float length(){
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Override
    public void normalize() {
        float length = this.length();

        if (length != 0f){
            this.x /= length;
            this.y /= length;
            this.z /= length;
        }
        else {
            this.x /= epsilon;
            this.y /= epsilon;
            this.z /= epsilon;
        }
    }

    @Override
    public float dotProduct(Vector3D vector){
        return this.x * vector.x + this.y * vector.y + this.z * vector.z;
    }

    @Override
    public float dotProduct(Vertex3D vertex){
        return this.x * vertex.x + this.y * vertex.y + this.z * vertex.z;
    }

    @Override
    public void crossProduct(Vector3D vectorA, Vector3D vectorB){
        this.x = vectorA.y * vectorB.z - vectorA.z * vectorB.y;
        this.y = vectorA.x * vectorB.z - vectorA.z * vectorB.x;
        this.z = vectorA.x * vectorB.y - vectorA.y * vectorB.x;
    }

    @Override
    public void setNormal(Vector3D vectorA, Vector3D vectorB){
        // Cross product to B and A for openGLs right handed system
        // otherwise cross product A and B for left handed system.
        this.crossProduct(vectorB, vectorA);
        this.normalize();
    }

    @Override
    public void add(Vector3D vector){
        float x = this.x + vector.x;
        float y = this.y + vector.y;
        float z = this.z + vector.z;

        this.set(x, y, z);
    }

    @Override
    public void subtract(Vector3D vector){
        float x = this.x - vector.x;
        float y = this.y - vector.y;
        float z = this.z - vector.z;

        this.set(x, y, z);
    }

    @Override
    public Vector3D _normalize(){
        Vector3D output = new Vector3D();
        float length = this.length();

        if (length != 0f){
            output.x /= length;
            output.y /= length;
            output.z /= length;
        }
        else {
            output.x /= epsilon;
            output.y /= epsilon;
            output.z /= epsilon;
        }

        return output;
    }

    @Override
    public Vector3D _crossProduct(Vector3D vectorA, Vector3D vectorB){
        Vector3D output = new Vector3D();

        output.x = vectorA.y * vectorB.z - vectorA.z * vectorB.y;
        output.y = vectorA.x * vectorB.z - vectorA.z * vectorB.x;
        output.z = vectorA.x * vectorB.y - vectorA.y * vectorB.x;

        return output;
    }

    @Override
    public Vector3D _setNormal(Vector3D vector){
        // Cross product to B and A for openGLs right handed system
        // otherwise cross product A and B for left handed system.
        Vector3D output = new Vector3D();
        output.crossProduct(vector, this);
        output.normalize();

        return output;
    }

    @Override
    public Vector3D _setNormal(Vector3D vectorA, Vector3D vectorB){
        // Cross product to B and A for openGLs right handed system
        // otherwise cross product A and B for left handed system.
        Vector3D output = new Vector3D();
        output.crossProduct(vectorB, vectorA);
        output.normalize();

        return output;
    }

    @Override
    public Vector3D _add(Vector3D vector){
        Vector3D output = new Vector3D();

        output.x = this.x + vector.x;
        output.y = this.y + vector.y;
        output.z = this.z + vector.z;

        return output;
    }

    @Override
    public Vector3D _subtract(Vector3D vector){
        Vector3D output = new Vector3D();

        output.x = this.x - vector.x;
        output.y = this.y - vector.y;
        output.z = this.z - vector.z;

        return output;
    }
}
