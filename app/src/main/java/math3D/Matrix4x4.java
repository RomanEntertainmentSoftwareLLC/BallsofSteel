package math3D;

import android.util.Log;
import romanentertainmentsoftware.ballsofsteel.MathCommon;

/**
 * Created by Roman Entertainment Software LLC on 5/3/2018.
 */

interface iMatrix4x4Tools{
    void copy(Matrix4x4 matrix);
    void set(float m00, float m01, float m02, float m03,
             float m10, float m11, float m12, float m13,
             float m20, float m21, float m22, float m23,
             float m30, float m31, float m32, float m33);
    float[] convertTo1DArray();
    float[][] convertTo2DArray();
    void print();

    Matrix4x4 _copy();
    Matrix4x4 _copy(Matrix4x4 matrix);
    Matrix4x4 _set(float m00, float m01, float m02, float m03,
                   float m10, float m11, float m12, float m13,
                   float m20, float m21, float m22, float m23,
                   float m30, float m31, float m32, float m33);
}

interface iMatrix4x4{
    void zero();
    void identity();
    void scale(float k);
    void multiply(Matrix4x4 matrix);
    void transpose();
    float determinant();
    void adjugate();
    void inverse();

    Matrix4x4 _zero();
    Matrix4x4 _identity();
    Matrix4x4 _scale(float k);
    Matrix4x4 _multiply(Matrix4x4 matrix);
    Matrix4x4 _transpose();
    Matrix4x4 _adjugate();
    Matrix4x4 _inverse();
    Vector3D multiplyVector(Vector3D vector);
    Vertex3D multiplyVertex(Vertex3D vertex);
}

public class Matrix4x4 implements iMatrix4x4Tools, iMatrix4x4{

    public float[][] m = new float[4][4];

    public Matrix4x4(){

    }

    public Matrix4x4(float m00, float m01, float m02, float m03,
                     float m10, float m11, float m12, float m13,
                     float m20, float m21, float m22, float m23,
                     float m30, float m31, float m32, float m33){
        m[0][0] = m00; m[0][1] = m01; m[0][2] = m02; m[0][3] = m03;
        m[1][0] = m10; m[1][1] = m11; m[1][2] = m12; m[1][3] = m13;
        m[2][0] = m20; m[2][1] = m21; m[2][2] = m22; m[2][3] = m23;
        m[3][0] = m30; m[3][1] = m31; m[3][2] = m32; m[3][3] = m33;
    }

    @Override
    public void copy(Matrix4x4 matrix){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.m[i][j] = matrix.m[i][j];
            }
        }
    }

    @Override
    public void set(float m00, float m01, float m02, float m03,
                    float m10, float m11, float m12, float m13,
                    float m20, float m21, float m22, float m23,
                    float m30, float m31, float m32, float m33){
        this.m[0][0] = m00; this.m[0][1] = m01; this.m[0][2] = m02; this.m[0][3] = m03;
        this.m[1][0] = m10; this.m[1][1] = m11; this.m[1][2] = m12; this.m[1][3] = m13;
        this.m[2][0] = m20; this.m[2][1] = m21; this.m[2][2] = m22; this.m[2][3] = m23;
        this.m[3][0] = m30; this.m[3][1] = m31; this.m[3][2] = m32; this.m[3][3] = m33;
    }

    @Override
    public float[] convertTo1DArray(){
        float[] result = new float[16];

        result[0] = this.m[0][0]; result[1] = this.m[0][1]; result[2] = this.m[0][2]; result[3] = this.m[0][3];
        result[4] = this.m[1][0]; result[5] = this.m[1][1]; result[6] = this.m[1][2]; result[7] = this.m[1][3];
        result[8] = this.m[2][0]; result[9] = this.m[2][1]; result[10] = this.m[2][2]; result[11] = this.m[2][3];
        result[12] = this.m[3][0]; result[13] = this.m[3][1]; result[14] = this.m[3][2]; result[15] = this.m[3][3];

        return result;
    }

    @Override
    public float[][] convertTo2DArray(){
        float[][] output = new float[4][4];

        output[0][0] = this.m[0][0]; output[0][1] = this.m[0][1]; output[0][2] = this.m[0][2]; output[0][3] = this.m[0][3];
        output[1][0] = this.m[1][0]; output[1][1] = this.m[1][1]; output[1][2] = this.m[1][2]; output[1][3] = this.m[1][3];
        output[2][0] = this.m[2][0]; output[2][1] = this.m[2][1]; output[2][2] = this.m[2][2]; output[2][3] = this.m[2][3];
        output[3][0] = this.m[3][0]; output[3][1] = this.m[3][1]; output[3][2] = this.m[3][2]; output[3][3] = this.m[3][3];

        return output;
    }

    @Override
    public void print() {
        Log.d("Matrix4x4", m[0][0] + ", " + m[0][1] + ", " + m[0][2] + ", " + m[0][3]);
        Log.d("Matrix4x4", m[1][0] + ", " + m[1][1] + ", " + m[1][2] + ", " + m[1][3]);
        Log.d("Matrix4x4", m[2][0] + ", " + m[2][1] + ", " + m[2][2] + ", " + m[2][3]);
        Log.d("Matrix4x4", m[3][0] + ", " + m[3][1] + ", " + m[3][2] + ", " + m[3][3]);
    }

    @Override
    public Matrix4x4 _copy() {
        Matrix4x4 output = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output.m[i][j] = this.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Matrix4x4 _copy(Matrix4x4 matrix) {
        Matrix4x4 output = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output.m[i][j] = matrix.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Matrix4x4 _set(float m00, float m01, float m02, float m03,
                          float m10, float m11, float m12, float m13,
                          float m20, float m21, float m22, float m23,
                          float m30, float m31, float m32, float m33){
        Matrix4x4 output = new Matrix4x4();

        output.m[0][0] = m00; output.m[0][1] = m01; output.m[0][2] = m02; output.m[0][3] = m03;
        output.m[1][0] = m10; output.m[1][1] = m11; output.m[1][2] = m12; output.m[1][3] = m13;
        output.m[2][0] = m20; output.m[2][1] = m21; output.m[2][2] = m22; output.m[2][3] = m23;
        output.m[3][0] = m30; output.m[3][1] = m31; output.m[3][2] = m32; output.m[3][3] = m33;

        return output;
    }

    @Override
    public void zero(){
        this.m[0][0] = 0f; this.m[0][1] = 0f; this.m[0][2] = 0f; this.m[0][3] = 0f;
        this.m[1][0] = 0f; this.m[1][1] = 0f; this.m[1][2] = 0f; this.m[1][3] = 0f;
        this.m[2][0] = 0f; this.m[2][1] = 0f; this.m[2][2] = 0f; this.m[2][3] = 0f;
        this.m[3][0] = 0f; this.m[3][1] = 0f; this.m[3][2] = 0f; this.m[3][3] = 0f;
    }

    @Override
    public void identity(){
        this.m[0][0] = 1f; this.m[0][1] = 0f; this.m[0][2] = 0f; this.m[0][3] = 0f;
        this.m[1][0] = 0f; this.m[1][1] = 1f; this.m[1][2] = 0f; this.m[1][3] = 0f;
        this.m[2][0] = 0f; this.m[2][1] = 0f; this.m[2][2] = 1f; this.m[2][3] = 0f;
        this.m[3][0] = 0f; this.m[3][1] = 0f; this.m[3][2] = 0f; this.m[3][3] = 1f;
    }

    @Override
    public void scale(float k){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.m[i][j] *= k;
            }
        }
    }

    @Override
    public void multiply(Matrix4x4 matrix) {
        Matrix4x4 temp = this._copy();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.m[i][j] = temp.m[i][0] * matrix.m[0][j] + temp.m[i][1] * matrix.m[1][j] + temp.m[i][2] * matrix.m[2][j] + temp.m[i][3] * matrix.m[3][j];
            }
        }
    }

    @Override
    public void transpose() {
        Matrix4x4 temp = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                temp.m[i][j] = this.m[j][i];
            }
        }

        this.copy(temp);
    }

    @Override
    public float determinant() {
        Matrix3x3 A = new Matrix3x3(this.m[1][1], this.m[1][2], this.m[1][3], this.m[2][1], this.m[2][2], this.m[2][3], this.m[3][1], this.m[3][2] , this.m[3][3]);
        Matrix3x3 B = new Matrix3x3(this.m[1][0], this.m[1][2], this.m[1][3], this.m[2][0], this.m[2][2], this.m[2][3], this.m[3][0], this.m[3][2] , this.m[3][3]);
        Matrix3x3 C = new Matrix3x3(this.m[1][0], this.m[1][1], this.m[1][3], this.m[2][0], this.m[2][1], this.m[2][3], this.m[3][0], this.m[3][1] , this.m[3][3]);
        Matrix3x3 D = new Matrix3x3(this.m[1][0], this.m[1][1], this.m[1][2], this.m[2][0], this.m[2][1], this.m[2][2], this.m[3][0], this.m[3][1] , this.m[3][2]);

        return this.m[0][0] * A.determinant() -
                this.m[0][1] * B.determinant() +
                this.m[0][2] * C.determinant() -
                this.m[0][3] * D.determinant();
    }

    @Override
    public void adjugate(){
        Matrix3x3 A = new Matrix3x3(this.m[1][1], this.m[1][2], this.m[1][3], this.m[2][1], this.m[2][2], this.m[2][3], this.m[3][1], this.m[3][2], this.m[3][3]);
        Matrix3x3 B = new Matrix3x3(this.m[1][0], this.m[1][2], this.m[1][3], this.m[2][0], this.m[2][2], this.m[2][3], this.m[3][0], this.m[3][2], this.m[3][3]);
        Matrix3x3 C = new Matrix3x3(this.m[1][0], this.m[1][1], this.m[1][3], this.m[2][0], this.m[2][1], this.m[2][3], this.m[3][0], this.m[3][1], this.m[3][3]);
        Matrix3x3 D = new Matrix3x3(this.m[1][0], this.m[1][1], this.m[1][2], this.m[2][0], this.m[2][1], this.m[2][2], this.m[3][0], this.m[3][1], this.m[3][2]);

        Matrix3x3 E = new Matrix3x3(this.m[0][1], this.m[0][2], this.m[0][3], this.m[2][1], this.m[2][2], this.m[2][3], this.m[3][1], this.m[3][2], this.m[3][3]);
        Matrix3x3 F = new Matrix3x3(this.m[0][0], this.m[0][2], this.m[0][3], this.m[2][0], this.m[2][2], this.m[2][3], this.m[3][0], this.m[3][2], this.m[3][3]);
        Matrix3x3 G = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][3], this.m[2][0], this.m[2][1], this.m[2][3], this.m[3][0], this.m[3][1], this.m[3][3]);
        Matrix3x3 H = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][2], this.m[2][0], this.m[2][1], this.m[2][2], this.m[3][0], this.m[3][1], this.m[3][2]);

        Matrix3x3 I = new Matrix3x3(this.m[0][1], this.m[0][2], this.m[0][3], this.m[1][1], this.m[1][2], this.m[1][3], this.m[3][1], this.m[3][2], this.m[3][3]);
        Matrix3x3 J = new Matrix3x3(this.m[0][0], this.m[0][2], this.m[0][3], this.m[1][0], this.m[1][2], this.m[1][3], this.m[3][0], this.m[3][2], this.m[3][3]);
        Matrix3x3 K = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][3], this.m[1][0], this.m[1][1], this.m[1][3], this.m[3][0], this.m[3][1], this.m[3][3]);
        Matrix3x3 L = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][2], this.m[1][0], this.m[1][1], this.m[1][2], this.m[3][0], this.m[3][1], this.m[3][2]);

        Matrix3x3 M = new Matrix3x3(this.m[0][1], this.m[0][2], this.m[0][3], this.m[1][1], this.m[1][2], this.m[1][3], this.m[2][1], this.m[2][2], this.m[2][3]);
        Matrix3x3 N = new Matrix3x3(this.m[0][0], this.m[0][2], this.m[0][3], this.m[1][0], this.m[1][2], this.m[1][3], this.m[2][0], this.m[2][2], this.m[2][3]);
        Matrix3x3 O = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][3], this.m[1][0], this.m[1][1], this.m[1][3], this.m[2][0], this.m[2][1], this.m[2][3]);
        Matrix3x3 P = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][2], this.m[1][0], this.m[1][1], this.m[1][2], this.m[2][0], this.m[2][1], this.m[2][2]);

        Matrix4x4 output = new Matrix4x4(A.determinant(), -B.determinant(), C.determinant(), -D.determinant(),
                -E.determinant(), F.determinant(), -G.determinant(), H.determinant(),
                I.determinant(), -J.determinant(), K.determinant(), -L.determinant(),
                -M.determinant(), N.determinant(), -O.determinant(), P.determinant());
        this.copy(output);
    }

    @Override
    public void inverse(){
        Matrix4x4 matrixOfMinors;
        Matrix4x4 transposedCoFactor;
        final float epsilon = 0.0000000001f;

        matrixOfMinors = this._adjugate();
        float determinant = this.determinant();
        float reciprocalDeterminant = 0.0f;

        if (determinant != 0.0f)
            reciprocalDeterminant = 1.0f / determinant;
        else
            reciprocalDeterminant = 1.0f / epsilon;

        transposedCoFactor = matrixOfMinors._transpose();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.m[i][j] = reciprocalDeterminant * transposedCoFactor.m[i][j];
            }
        }
    }

    @Override
    public Matrix4x4 _zero(){
        Matrix4x4 output = new Matrix4x4();

        output.m[0][0] = 0f; output.m[0][1] = 0f; output.m[0][2] = 0f; output.m[0][3] = 0f;
        output.m[1][0] = 0f; output.m[1][1] = 0f; output.m[1][2] = 0f; output.m[1][3] = 0f;
        output.m[2][0] = 0f; output.m[2][1] = 0f; output.m[2][2] = 0f; output.m[2][3] = 0f;
        output.m[3][0] = 0f; output.m[3][1] = 0f; output.m[3][2] = 0f; output.m[3][3] = 0f;

        return output;
    }

    @Override
    public Matrix4x4 _identity(){
        Matrix4x4 output = new Matrix4x4();

        output.m[0][0] = 1f; output.m[0][1] = 0f; output.m[0][2] = 0f; output.m[0][3] = 0f;
        output.m[1][0] = 0f; output.m[1][1] = 1f; output.m[1][2] = 0f; output.m[1][3] = 0f;
        output.m[2][0] = 0f; output.m[2][1] = 0f; output.m[2][2] = 1f; output.m[2][3] = 0f;
        output.m[3][0] = 0f; output.m[3][1] = 0f; output.m[3][2] = 0f; output.m[3][3] = 1f;

        return output;
    }

    @Override
    public Matrix4x4 _scale(float k){
        Matrix4x4 output = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output.m[i][j] = this.m[i][j] * k;
            }
        }

        return output;
    }

    @Override
    public Matrix4x4 _multiply(Matrix4x4 matrix) {
        Matrix4x4 output = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output.m[i][j] = this.m[i][0] * matrix.m[0][j] + this.m[i][1] * matrix.m[1][j] + this.m[i][2] * matrix.m[2][j] + this.m[i][3] * matrix.m[3][j];
            }
        }

        return output;
    }

    @Override
    public Matrix4x4 _transpose() {
        Matrix4x4 output = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output.m[i][j] = this.m[j][i];
            }
        }

        return output;
    }

    @Override
    public Matrix4x4 _adjugate(){
        Matrix3x3 A = new Matrix3x3(this.m[1][1], this.m[1][2], this.m[1][3], this.m[2][1], this.m[2][2], this.m[2][3], this.m[3][1], this.m[3][2], this.m[3][3]);
        Matrix3x3 B = new Matrix3x3(this.m[1][0], this.m[1][2], this.m[1][3], this.m[2][0], this.m[2][2], this.m[2][3], this.m[3][0], this.m[3][2], this.m[3][3]);
        Matrix3x3 C = new Matrix3x3(this.m[1][0], this.m[1][1], this.m[1][3], this.m[2][0], this.m[2][1], this.m[2][3], this.m[3][0], this.m[3][1], this.m[3][3]);
        Matrix3x3 D = new Matrix3x3(this.m[1][0], this.m[1][1], this.m[1][2], this.m[2][0], this.m[2][1], this.m[2][2], this.m[3][0], this.m[3][1], this.m[3][2]);

        Matrix3x3 E = new Matrix3x3(this.m[0][1], this.m[0][2], this.m[0][3], this.m[2][1], this.m[2][2], this.m[2][3], this.m[3][1], this.m[3][2], this.m[3][3]);
        Matrix3x3 F = new Matrix3x3(this.m[0][0], this.m[0][2], this.m[0][3], this.m[2][0], this.m[2][2], this.m[2][3], this.m[3][0], this.m[3][2], this.m[3][3]);
        Matrix3x3 G = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][3], this.m[2][0], this.m[2][1], this.m[2][3], this.m[3][0], this.m[3][1], this.m[3][3]);
        Matrix3x3 H = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][2], this.m[2][0], this.m[2][1], this.m[2][2], this.m[3][0], this.m[3][1], this.m[3][2]);

        Matrix3x3 I = new Matrix3x3(this.m[0][1], this.m[0][2], this.m[0][3], this.m[1][1], this.m[1][2], this.m[1][3], this.m[3][1], this.m[3][2], this.m[3][3]);
        Matrix3x3 J = new Matrix3x3(this.m[0][0], this.m[0][2], this.m[0][3], this.m[1][0], this.m[1][2], this.m[1][3], this.m[3][0], this.m[3][2], this.m[3][3]);
        Matrix3x3 K = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][3], this.m[1][0], this.m[1][1], this.m[1][3], this.m[3][0], this.m[3][1], this.m[3][3]);
        Matrix3x3 L = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][2], this.m[1][0], this.m[1][1], this.m[1][2], this.m[3][0], this.m[3][1], this.m[3][2]);

        Matrix3x3 M = new Matrix3x3(this.m[0][1], this.m[0][2], this.m[0][3], this.m[1][1], this.m[1][2], this.m[1][3], this.m[2][1], this.m[2][2], this.m[2][3]);
        Matrix3x3 N = new Matrix3x3(this.m[0][0], this.m[0][2], this.m[0][3], this.m[1][0], this.m[1][2], this.m[1][3], this.m[2][0], this.m[2][2], this.m[2][3]);
        Matrix3x3 O = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][3], this.m[1][0], this.m[1][1], this.m[1][3], this.m[2][0], this.m[2][1], this.m[2][3]);
        Matrix3x3 P = new Matrix3x3(this.m[0][0], this.m[0][1], this.m[0][2], this.m[1][0], this.m[1][1], this.m[1][2], this.m[2][0], this.m[2][1], this.m[2][2]);

        Matrix4x4 output = new Matrix4x4(A.determinant(), -B.determinant(), C.determinant(), -D.determinant(),
                -E.determinant(), F.determinant(), -G.determinant(), H.determinant(),
                I.determinant(), -J.determinant(), K.determinant(), -L.determinant(),
                -M.determinant(), N.determinant(), -O.determinant(), P.determinant());

        return output;
    }

    @Override
    public Matrix4x4 _inverse(){
        Matrix4x4 matrixOfMinors;
        Matrix4x4 transposedCoFactor;
        Matrix4x4 output = new Matrix4x4();
        final float epsilon = 0.0000000001f;

        matrixOfMinors = this._adjugate();
        float determinant = this.determinant();
        float reciprocalDeterminant = 0.0f;

        if (determinant != 0.0f)
            reciprocalDeterminant = 1.0f / determinant;
        else
            reciprocalDeterminant = 1.0f / epsilon;

        transposedCoFactor = matrixOfMinors._transpose();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                output.m[i][j] = reciprocalDeterminant * transposedCoFactor.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Vector3D multiplyVector(Vector3D vector){
        float x = this.m[0][0] * vector.x + this.m[1][0] * vector.y + this.m[2][0] * vector.z + this.m[3][0];
        float y = this.m[0][1] * vector.x + this.m[1][1] * vector.y + this.m[2][1] * vector.z + this.m[3][1];
        float z = this.m[0][2] * vector.x + this.m[1][2] * vector.y + this.m[2][2] * vector.z + this.m[3][2];
        float w = this.m[0][3] * vector.x + this.m[1][3] * vector.y + this.m[2][3] * vector.z + this.m[3][3];

        return new Vector3D(x, y, z, w);
    }

    @Override
    public Vertex3D multiplyVertex(Vertex3D vertex){
        float x = this.m[0][0] * vertex.x + this.m[1][0] * vertex.y + this.m[2][0] * vertex.z + this.m[3][0];
        float y = this.m[0][1] * vertex.x + this.m[1][1] * vertex.y + this.m[2][1] * vertex.z + this.m[3][1];
        float z = this.m[0][2] * vertex.x + this.m[1][2] * vertex.y + this.m[2][2] * vertex.z + this.m[3][2];
        float w = this.m[0][3] * vertex.x + this.m[1][3] * vertex.y + this.m[2][3] * vertex.z + this.m[3][3];

        return new Vertex3D(x, y, z, w);
    }

    /*
    public static Matrix4x4 translate(Matrix4x4 matrix, float x, float y, float z){
        matrix.m[3][0] = x; matrix.m[3][1] = y; matrix.m[3][2] = z; matrix.m[3][3] = 1f;

        return matrix;
    }
    */
    /*
    public static Matrix4x4 rotateX(Matrix4x4 matrix, float radians){
        matrix.m[0][0] = 1f; matrix.m[0][1] = 0f; matrix.m[0][2] = 0f; matrix.m[0][3] = 0f;
        matrix.m[1][0] = 0f; matrix.m[1][1] = (float) Math.cos(radians); matrix.m[1][2] = (float) Math.sin(radians); matrix.m[1][3] = 0f;
        matrix.m[2][0] = 0f; matrix.m[2][1] = (float)-Math.sin(radians); matrix.m[2][2] = (float) Math.cos(radians); matrix.m[2][3] = 0f;
        matrix.m[3][0] = 0f; matrix.m[3][1] = 0f; matrix.m[3][2] = 0f; matrix.m[3][3] = 1f;

        return matrix;
    }
    */
    /*
    public static Matrix4x4 rotateY(Matrix4x4 matrix, float radians){
        matrix.m[0][0] = (float) Math.cos(radians); matrix.m[0][1] = 0f; matrix.m[0][2] = (float)-Math.sin(radians); matrix.m[0][3] = 0f;
        matrix.m[1][0] = 0f; matrix.m[1][1] = 1f; matrix.m[1][2] = 0f; matrix.m[1][3] = 0f;
        matrix.m[2][0] = (float) Math.sin(radians); matrix.m[2][1] = 0f; matrix.m[2][2] = (float) Math.cos(radians); matrix.m[2][3] = 0f;
        matrix.m[3][0] = 0f; matrix.m[3][1] = 0f; matrix.m[3][2] = 0f; matrix.m[3][3] = 1f;

        return matrix;
    }
    */
    /*
    public static Matrix4x4 rotateZ(Matrix4x4 matrix, float radians){
        matrix.m[0][0] = (float) Math.cos(radians); matrix.m[0][1] = (float) Math.sin(radians); matrix.m[0][2] = 0f; matrix.m[0][3] = 0f;
        matrix.m[1][0] = (float)-Math.sin(radians); matrix.m[1][1] = (float) Math.cos(radians); matrix.m[1][2] = 0f; matrix.m[1][3] = 0f;
        matrix.m[2][0] = 0f; matrix.m[2][1] = 0f; matrix.m[2][2] = 1f; matrix.m[2][3] = 0f;
        matrix.m[3][0] = 0f; matrix.m[3][1] = 0f; matrix.m[3][2] = 0f; matrix.m[3][3] = 1f;

        return matrix;
    }
    */

    /*

    public static Matrix4x4 perspectiveFOVRH(Matrix4x4 matrix, float fov, float aspectRatio, float nearZ, float farZ){
        float xScale = 0f;
        float yScale = 0f;//MathCommon.cot(fov / 2f);

        if (aspectRatio != 0f)
            xScale = yScale / aspectRatio;

        if ((farZ - nearZ) != 0) {
            matrix.m[0][0] = xScale; matrix.m[0][1] = 0f; matrix.m[0][2] = 0f; matrix.m[0][3] = 0f;
            matrix.m[1][0] = 0f; matrix.m[1][1] = yScale; matrix.m[1][2] = 0f; matrix.m[1][3] = 0f;
            matrix.m[2][0] = 0f; matrix.m[2][1] = 0f; matrix.m[2][2] = farZ / (farZ - nearZ); matrix.m[2][3] = 1f;
            matrix.m[3][0] = 0f; matrix.m[3][1] = 0f; matrix.m[3][2] = -nearZ * farZ / (farZ - nearZ); matrix.m[3][3] = 0f;
        }

        return matrix;
    }

    public static Matrix4x4 perspectiveFOVLH(Matrix4x4 matrix, float fov, float aspectRatio, float nearZ, float farZ){
        float xScale = 0f;
        float yScale = 0f;//MathCommon.cot(fov / 2f);

        if (aspectRatio != 0f)
            xScale = yScale / aspectRatio;

        if ((nearZ - farZ) != 0) {
            matrix.m[0][0] = xScale; matrix.m[0][1] = 0f; matrix.m[0][2]= 0f; matrix.m[0][3] = 0f;
            matrix.m[1][0] = 0f; matrix.m[1][1] = yScale; matrix.m[1][2] = 0f; matrix.m[1][3] = 0f;
            matrix.m[2][0] = 0f; matrix.m[2][1] = 0f; matrix.m[2][2] = farZ / (nearZ - farZ); matrix.m[2][3] = -1f;
            matrix.m[3][0] = 0f; matrix.m[3][1] = 0f; matrix.m[3][2] = nearZ * farZ / (nearZ - farZ); matrix.m[3][3] = 0f;
        }

        return matrix;
    }
    */
}

