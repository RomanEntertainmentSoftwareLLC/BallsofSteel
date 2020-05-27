package math2D;

import android.util.Log;

/**
 * Created by Roman Entertainment Software LLC on 5/13/2018.
 */

interface iVector2DTools{
    void copy(Vector2D vector);
    void set(float x, float y);
    void set(Vertex2D vertexA, Vertex2D vertexB);
    void print();

    Vector2D _copy();
    Vector2D _copy(Vector2D vector);
    Vector2D _set(float x, float y);
    Vector2D _set(Vertex2D vertexA, Vertex2D vertexB);
}

interface iVector2D{
    float length();
    float lengthSquared();
    void normalize();
    float dotProduct(Vector2D vector);
    float dotProduct(Vertex2D vertex);
    void crossProduct();
    void setNormal();
    void add(Vector2D vector);
    void subtract(Vector2D vector);

    Vector2D _normalize();
    Vector2D _crossProduct();
    Vector2D _setNormal();
    Vector2D _add(Vector2D vector);
    Vector2D _subtract(Vector2D vector);

}

public class Vector2D implements iVector2DTools, iVector2D{
    public float x;
    public float y;

    private final float epsilon = 0.0000000001f;

    public Vector2D(){

    }

    public Vector2D(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vertex2D vertexA, Vertex2D vertexB){
        this.x = vertexB.x - vertexA.x;
        this.y = vertexB.y - vertexA.y;
    }

    @Override
    public void copy(Vector2D vector){
        this.x = vector.x;
        this.y = vector.y;
    }

    @Override
    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void set(Vertex2D vertexA, Vertex2D vertexB){
        this.x = vertexB.x - vertexA.x;
        this.y = vertexB.y - vertexA.y;
    }

    @Override
    public void print(){
        Log.d("Vector2D", this.x + ", " + this.y);
    }

    @Override
    public Vector2D _copy(){
        Vector2D output = new Vector2D();

        output.x = this.x;
        output.y = this.y;

        return output;
    }

    @Override
    public Vector2D _copy(Vector2D vector){
        Vector2D output = new Vector2D();

        output.x = vector.x;
        output.y = vector.y;

        return output;
    }

    @Override
    public Vector2D _set(float x, float y){
        Vector2D output = new Vector2D();

        output.x = x;
        output.y = y;

        return output;
    }

    @Override
    public Vector2D _set(Vertex2D vertexA, Vertex2D vertexB){
        Vector2D output = new Vector2D();

        output.x = vertexB.x - vertexA.x;
        output.y = vertexB.y - vertexA.y;

        return output;
    }

    @Override
    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    @Override
    public void normalize() {
        float length = this.length();

        if (length != 0f){
            this.x /= length;
            this.y /= length;
        }
        else{
            this.x /= epsilon;
            this.y /= epsilon;
        }
    }

    @Override
    public float dotProduct(Vector2D vector){
        return this.x * vector.x + this.y * vector.y;
    }

    @Override
    public float dotProduct(Vertex2D vertex){
        return this.x * vertex.x + this.y * vertex.y;
    }

    @Override
    public void crossProduct() {
        this.set(-this.y, this.x);
    }

    @Override
    public void setNormal() {
        this.crossProduct();
        this.normalize();
    }

    @Override
    public void add(Vector2D vector){
        float x = this.x + vector.x;
        float y = this.y + vector.y;

        this.set(x, y);
    }

    @Override
    public void subtract(Vector2D vector){
        float x = this.x - vector.x;
        float y = this.y - vector.y;

        this.set(x, y);
    }

    @Override
    public Vector2D _normalize() {
        Vector2D output = new Vector2D();
        float length = this.length();

        if (length != 0f){
            output.x /= length;
            output.y /= length;
        }
        else{
            output.x /= epsilon;
            output.y /= epsilon;
        }

        return output;
    }

    @Override
    public Vector2D _crossProduct() {
        Vector2D output = new Vector2D();

        output.set(-this.y, this.x);

        return output;
    }

    @Override
    public Vector2D _setNormal() {
        Vector2D output = this._copy();
        output.crossProduct();
        output.normalize();

        return output;
    }

    @Override
    public Vector2D _add(Vector2D vector){
        Vector2D output = new Vector2D();

        output.x = this.x + vector.x;
        output.y = this.y + vector.y;

        return output;
    }

    @Override
    public Vector2D _subtract(Vector2D vector){
        Vector2D output = new Vector2D();

        output.x = this.x - vector.x;
        output.y = this.y - vector.y;

        return output;
    }
}
