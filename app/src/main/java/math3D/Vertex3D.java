package math3D;

import android.util.Log;

/**
 * Created by Roman Entertainment Software LLC on 4/27/2018.
 */

interface iVertex3DTools{
    void copy(Vertex3D vertex);
    void set(float x, float y, float z);
    void set(float x, float y, float z, float w);
    void print();

    Vertex3D _copy();
    Vertex3D _copy(Vertex3D vertex);
    Vertex3D _set(float x, float y, float z);
    Vertex3D _set(float x, float y, float z, float w);
}

interface iVertex3D{

}

public class Vertex3D implements iVertex3DTools, iVertex3D{
    public float x;
    public float y;
    public float z;
    public float w;

    public Vertex3D(){
        this.w = 1f;
    }

    public Vertex3D(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1f;
    }

    public Vertex3D(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void copy(Vertex3D vertex){
        this.x = vertex.x;
        this.y = vertex.y;
        this.z = vertex.z;
        this.w = vertex.w;
    }

    @Override
    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1f;
    }

    @Override
    public void set(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void print(){
        Log.d("Vertex3D", this.x + ", " + this.y + ", " + this.z + ", " + this.w);
    }

    @Override
    public Vertex3D _copy(){
        Vertex3D output = new Vertex3D();

        output.x = this.x;
        output.y = this.y;
        output.z = this.z;
        output.w = this.w;

        return output;
    }

    @Override
    public Vertex3D _copy(Vertex3D vertex){
        Vertex3D output = new Vertex3D();

        output.x = vertex.x;
        output.y = vertex.y;
        output.z = vertex.z;
        output.w = vertex.w;

        return output;
    }

    @Override
    public Vertex3D _set(float x, float y, float z){
        Vertex3D output = new Vertex3D();

        output.x = x;
        output.y = y;
        output.z = z;
        output.w = 1f;

        return output;
    }

    @Override
    public Vertex3D _set(float x, float y, float z, float w){
        Vertex3D output = new Vertex3D();

        output.x = x;
        output.y = y;
        output.z = z;
        output.w = w;

        return output;
    }
}