package math3D;

import android.util.Log;

import math2D.Vector2D;
import math2D.Vertex2D;

/**
 * Created by Roman Entertainment Software LLC on 5/19/2018.
 */

interface iMatrix2x2Tools{
    void copy(Matrix2x2 matrix);
    void set(float m00, float m01,
             float m10, float m11);
    float[] convertTo1DArray();
    float[][] convertTo2DArray();
    void print();

    Matrix2x2 _copy();
    Matrix2x2 _copy(Matrix2x2 matrix);
    Matrix2x2 _set(float m00, float m01,
                   float m10, float m11);
}

interface iMatrix2x2{
    void zero();
    void identity();
    void scale(float k);
    void multiply(Matrix2x2 matrix);
    void transpose();
    float determinant();
    void adjugate();
    void inverse();

    Matrix2x2 _zero();
    Matrix2x2 _identity();
    Matrix2x2 _scale(float k);
    Matrix2x2 _multiply(Matrix2x2 matrix);
    Matrix2x2 _transpose();
    Matrix2x2 _adjugate();
    Matrix2x2 _inverse();
    Vector2D multiplyVector(Vector2D vector);
    Vertex2D multiplyVertex(Vertex2D vertex);
}

public class Matrix2x2 implements iMatrix2x2Tools, iMatrix2x2{
    public float[][] m = new float[2][2];

    public Matrix2x2(){

    }

    public Matrix2x2(float m00, float m01,
                     float m10, float m11){
        this.m[0][0] = m00; this.m[0][1] = m01;
        this.m[1][0] = m10; this.m[1][1] = m11;
    }

    @Override
    public void copy(Matrix2x2 matrix){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.m[i][j] = matrix.m[i][j];
            }
        }
    }

    @Override
    public void set(float m00, float m01,
                    float m10, float m11){
        this.m[0][0] = m00; this.m[0][1] = m01;
        this.m[1][0] = m10; this.m[1][1] = m11;
    }

    @Override
    public float[] convertTo1DArray(){
        float[] output = new float[4];

        output[0] = this.m[0][0]; output[1] = this.m[0][1];
        output[2] = this.m[1][0]; output[3] = this.m[1][1];

        return output;
    }

    @Override
    public float[][] convertTo2DArray(){
        float[][] output = new float[2][2];

        output[0][0] = this.m[0][0]; output[0][1] = this.m[0][1];
        output[1][0] = this.m[1][0]; output[1][1] = this.m[1][1];

        return output;
    }

    @Override
    public void print(){
        Log.d("Matrix2x2", m[0][0] + ", " + m[0][1]);
        Log.d("Matrix2x2", m[1][0] + ", " + m[1][1]);
    }

    @Override
    public Matrix2x2 _copy(){
        Matrix2x2 output = new Matrix2x2();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                output.m[i][j] = this.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Matrix2x2 _copy(Matrix2x2 matrix){
        Matrix2x2 output = new Matrix2x2();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                output.m[i][j] = matrix.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Matrix2x2 _set(float m00, float m01,
                          float m10, float m11){
        Matrix2x2 output = new Matrix2x2();

        output.m[0][0] = m00; output.m[0][1] = m01;
        output.m[1][0] = m10; output.m[1][1] = m11;

        return output;
    }

    @Override
    public void zero(){
        this.m[0][0] = 0f; this.m[0][1] = 0f;
        this.m[1][0] = 0f; this.m[1][1] = 0f;
    }

    @Override
    public void identity(){
        this.m[0][0] = 1f; this.m[0][1] = 0f;
        this.m[1][0] = 0f; this.m[1][1] = 1f;
    }

    @Override
    public void scale(float k){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.m[i][j] *= k;
            }
        }
    }

    @Override
    public void multiply(Matrix2x2 matrix) {
        Matrix2x2 temp = this._copy();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.m[i][j] = temp.m[i][0] * matrix.m[0][j] +
                        temp.m[i][1] * matrix.m[1][j];
            }
        }
    }

    @Override
    public void transpose() {
        Matrix2x2 temp = new Matrix2x2();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                temp.m[i][j] = this.m[j][i];
            }
        }

        this.copy(temp);
    }

    @Override
    public float determinant(){
        return this.m[0][0] * this.m[1][1] -
                this.m[0][1] * this.m[1][0];

        // Correct!

        // Example:
        // 3 8    assuming m00 m01
        // 4 6             m10 m11

        // 3 * 6 - 8 * 4 = -14  or  m00 * m11 - m01 * m10
        // in a cris-cross manner.
    }

    @Override
    public void adjugate(){
        Matrix2x2 temp = this._copy();

        this.m[0][0] = temp.m[1][1]; this.m[0][1] = -temp.m[0][1];
        this.m[1][0] = -temp.m[1][0]; this.m[1][1] = temp.m[0][0];
    }

    @Override
    public void inverse(){
        float reciprocalDeterminant = 1 / determinant();
        Matrix2x2 adjugate = _adjugate();
        this.copy(adjugate._scale(reciprocalDeterminant));
    }

    @Override
    public Matrix2x2 _zero(){
        Matrix2x2 output = new Matrix2x2();

        output.m[0][0] = 0f; output.m[0][1] = 0f;
        output.m[1][0] = 0f; output.m[1][1] = 0f;

        return output;
    }

    @Override
    public Matrix2x2 _identity(){
        Matrix2x2 output = new Matrix2x2();

        output.m[0][0] = 1f; output.m[0][1] = 0f;
        output.m[1][0] = 0f; output.m[1][1] = 1f;

        return output;
    }

    @Override
    public Matrix2x2 _scale(float k){
        Matrix2x2 output = new Matrix2x2();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                output.m[i][j] = this.m[i][j] * k;
            }
        }

        return output;
    }

    @Override
    public Matrix2x2 _multiply(Matrix2x2 matrix) {
        Matrix2x2 output = new Matrix2x2();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                output.m[i][j] = this.m[i][0] * matrix.m[0][j] +
                        this.m[i][1] * matrix.m[1][j];
            }
        }

        return output;
    }

    @Override
    public Matrix2x2 _transpose() {
        Matrix2x2 output = new Matrix2x2();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                output.m[i][j] = this.m[j][i];
            }
        }
        return output;
    }

    @Override
    public Matrix2x2 _adjugate(){
        Matrix2x2 output = new Matrix2x2();

        output.m[0][0] = this.m[1][1]; output.m[0][1] = -this.m[0][1];
        output.m[1][0] = -this.m[1][0]; output.m[1][1] = this.m[0][0];

        return output;
    }

    @Override
    public Matrix2x2 _inverse(){
        Matrix2x2 output = new Matrix2x2();

        float reciprocalDeterminant = 1 / determinant();
        Matrix2x2 adjugate = _adjugate();
        output.copy(adjugate._scale(reciprocalDeterminant));

        return output;
    }

    @Override
    public Vector2D multiplyVector(Vector2D vector){
        float x = this.m[0][0] * vector.x + this.m[1][0] * vector.y;
        float y = this.m[0][1] * vector.x + this.m[1][1] * vector.y;

        return new Vector2D(x, y);
    }

    @Override
    public Vertex2D multiplyVertex(Vertex2D vertex){
        float x = this.m[0][0] * vertex.x + this.m[1][0] * vertex.y;
        float y = this.m[0][1] * vertex.x + this.m[1][1] * vertex.y;

        return new Vertex2D(x, y);
    }
}

