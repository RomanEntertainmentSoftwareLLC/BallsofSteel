package math2D;

/**
 * Created by Roman Entertainment Software LLC on 4/27/2018.
 */

import android.util.Log;

interface iVertex2DTools{
    void copy(Vertex2D vertex);
    void set(float x, float y);
    void print();

    Vertex2D _copy();
    Vertex2D _copy(Vertex2D vertex);
    Vertex2D _set(float x, float y);
}

interface iVertex2D{
    void vectorSpaceDivide(float ellipsoid_radius_x, float ellipsoid_radius_y);
    void vectorSpaceDivide(Vector2D ellipsoid_radius);

    Vertex2D _vectorSpaceDivide(float ellipsoid_radius_x, float ellipsoid_radius_y);
    Vertex2D _vectorSpaceDivide(Vector2D ellipsoid_radius);
}

public class Vertex2D implements iVertex2DTools, iVertex2D{
    public float x;
    public float y;

    public Vertex2D(){

    }

    public Vertex2D(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void copy(Vertex2D vertex){
        this.x = vertex.x;
        this.y = vertex.y;
    }

    @Override
    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public void print(){
        Log.d("Vertex2D", this.x + ", " + this.y);
    }

    @Override
    public Vertex2D _copy(){
        Vertex2D output = new Vertex2D();

        output.x = this.x;
        output.y = this.y;

        return output;
    }

    @Override
    public Vertex2D _copy(Vertex2D vertex){
        Vertex2D output = new Vertex2D();

        output.x = vertex.x;
        output.y = vertex.y;

        return output;
    }

    @Override
    public Vertex2D _set(float x, float y){
        Vertex2D output = new Vertex2D();

        output.x = x;
        output.y = y;

        return output;
    }

    @Override
    public void vectorSpaceDivide(float ellipsoid_radius_x, float ellipsoid_radius_y) {
        float x = this.x / ellipsoid_radius_x;
        float y = this.y / ellipsoid_radius_y;

        this.x = x;
        this.y = y;
    }

    @Override
    public void vectorSpaceDivide(Vector2D ellipsoid_radius) {
        float x = this.x / ellipsoid_radius.x;
        float y = this.y / ellipsoid_radius.y;

        this.x = x;
        this.y = y;
    }

    @Override
    public Vertex2D _vectorSpaceDivide(float ellipsoid_radius_x, float ellipsoid_radius_y) {
        Vertex2D output = new Vertex2D();

        output.x = this.x / ellipsoid_radius_x;
        output.y = this.y / ellipsoid_radius_y;

        return output;
    }

    @Override
    public Vertex2D _vectorSpaceDivide(Vector2D ellipsoid_radius) {
        Vertex2D output = new Vertex2D();

        output.x = this.x / ellipsoid_radius.x;
        output.y = this.y / ellipsoid_radius.y;

        return output;
    }
}
