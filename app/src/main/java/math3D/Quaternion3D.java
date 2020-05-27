package math3D;

import android.util.Log;

/**
 * Created by Roman Entertainment Software LLC on 5/3/2018.
 */

interface iQuaternion3DTools{
    void copy();
    void set(float x, float y, float z, float w);
    void print();

    Quaternion3D _copy();
    Quaternion3D _copy(Quaternion3D quaternion);
    Quaternion3D _set(float x, float y, float z, float w);
}

interface iQuaternion3D{
    float length();
    float lengthSquared();
    void normalize();
    float dotProduct(Quaternion3D quaternion);
    void conjugate();
    void add(Quaternion3D vector);
    void subtract(Quaternion3D vector);
    void multiply(Quaternion3D quaternion);
    void multiply(Vector3D vector);

    Quaternion3D _normalize();
    Quaternion3D _add(Quaternion3D quaternion);
    Quaternion3D _subtract(Quaternion3D quaternion);
    Quaternion3D _multiply(Quaternion3D quaternion);
    Quaternion3D _multiply(Vector3D vector);
}

public class Quaternion3D implements iQuaternion3DTools, iQuaternion3D{
    public float x;
    public float y;
    public float z;
    public float w;

    private final float epsilon = 0.0000000001f;

    public Quaternion3D(){

    }

    public Quaternion3D(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void copy() {

    }

    @Override
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void print() {
        Log.d("Quaternion3D", this.x + ", " + this.y + ", " + this.z + ", " + this.w);
    }

    @Override
    public Quaternion3D _copy() {
        return null;
    }

    @Override
    public Quaternion3D _copy(Quaternion3D quaternion) {
        return null;
    }

    @Override
    public Quaternion3D _set(float x, float y, float z, float w) {
        return null;
    }

    @Override
    public float length(){
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    @Override
    public float lengthSquared(){
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    @Override
    public void normalize(){
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
    public float dotProduct(Quaternion3D quaternion){
        return this.x * quaternion.x + this.y * quaternion.y + this.z * quaternion.z;
    }

    @Override
    public void conjugate(){
        float x = -this.x;
        float y = -this.y;
        float z = -this.z;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void add(Quaternion3D vector){
        float x = this.x + vector.x;
        float y = this.y + vector.y;
        float z = this.z + vector.z;
        float w = this.w + vector.w;

        this.set(x, y, z, w);
    }

    @Override
    public void subtract(Quaternion3D vector){
        float x = this.x - vector.x;
        float y = this.y - vector.y;
        float z = this.z - vector.z;
        float w = this.w - vector.w;

        this.set(x, y, z, w);
    }

    @Override
    public void multiply(Quaternion3D quaternion){
        float x = this.x * quaternion.w + this.w * quaternion.x + this.y * quaternion.z - this.z * quaternion.y;
        float y = this.y * quaternion.w + this.w * quaternion.y + this.z * quaternion.x - this.x * quaternion.z;
        float z = this.z * quaternion.w + this.w * quaternion.z + this.x * quaternion.y - this.y * quaternion.x;
        float w = this.w * quaternion.w - this.x * quaternion.x - this.y * quaternion.y - this.z * quaternion.z;

        this.set(x, y, z, w);
    }

    @Override
    public void multiply(Vector3D vector){
        float x =  this.w * vector.x + this.y * vector.z - this.z * vector.y;
        float y =  this.w * vector.y + this.z * vector.x - this.x * vector.z;
        float z =  this.w * vector.z + this.x * vector.y - this.y * vector.x;
        float w = -this.x * vector.x - this.y * vector.y - this.z * vector.z;

        this.set(x, y, z, w);
    }

    @Override
    public Quaternion3D _normalize(){
        Quaternion3D output = new Quaternion3D();
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
    public Quaternion3D _add(Quaternion3D quaternion){
        Quaternion3D output = new Quaternion3D();

        output.x = this.x + quaternion.x;
        output.y = this.y + quaternion.y;
        output.z = this.z + quaternion.z;
        output.w = this.w + quaternion.w;

        return output;
    }

    @Override
    public Quaternion3D _subtract(Quaternion3D quaternion){
        Quaternion3D output = new Quaternion3D();

        output.x = this.x - quaternion.x;
        output.y = this.y - quaternion.y;
        output.z = this.z - quaternion.z;
        output.w = this.w - quaternion.w;

        return output;
    }

    @Override
    public Quaternion3D _multiply(Quaternion3D quaternion){
        Quaternion3D output = new Quaternion3D();

        output.x = this.x * quaternion.w + this.w * quaternion.x + this.y * quaternion.z - this.z * quaternion.y;
        output.y = this.y * quaternion.w + this.w * quaternion.y + this.z * quaternion.x - this.x * quaternion.z;
        output.z = this.z * quaternion.w + this.w * quaternion.z + this.x * quaternion.y - this.y * quaternion.x;
        output.w = this.w * quaternion.w - this.x * quaternion.x - this.y * quaternion.y - this.z * quaternion.z;

        return output;
    }

    @Override
    public Quaternion3D _multiply(Vector3D vector){
        Quaternion3D output = new Quaternion3D();

        output.x =  this.w * vector.x + this.y * vector.z - this.z * vector.y;
        output.y =  this.w * vector.y + this.z * vector.x - this.x * vector.z;
        output.z =  this.w * vector.z + this.x * vector.y - this.y * vector.x;
        output.w = -this.x * vector.x - this.y * vector.y - this.z * vector.z;

        return output;
    }
}
