package math3D;

import android.util.Log;

/**
 * Created by Roman Entertainment Software LLC on 5/19/2018.
 */

interface iMatrix3x3Tools{
    void copy(Matrix3x3 matrix);
    void set(float m00, float m01, float m02,
             float m10, float m11, float m12,
             float m20, float m21, float m22);
    float[] convertTo1DArray();
    float[][] convertTo2DArray();
    void print();

    Matrix3x3 _copy();
    Matrix3x3 _copy(Matrix3x3 matrix);
    Matrix3x3 _set(float m00, float m01, float m02,
                   float m10, float m11, float m12,
                   float m20, float m21, float m22);
}

interface iMatrix3x3{
    void zero();
    void identity();
    void scale(float k);
    void multiply(Matrix3x3 matrix);
    void transpose();
    float determinant();
    void adjugate();
    void inverse();

    Matrix3x3 _zero();
    Matrix3x3 _identity();
    Matrix3x3 _scale(float k);
    Matrix3x3 _multiply(Matrix3x3 matrix);
    Matrix3x3 _transpose();
    Matrix3x3 _adjugate();
    Matrix3x3 _inverse();
    Vector3D multiplyVector(Vector3D vector);
    Vertex3D multiplyVertex(Vertex3D vertex);
}

public class Matrix3x3 implements iMatrix3x3Tools, iMatrix3x3 {

    public float[][] m = new float[3][3];

    public Matrix3x3() {

    }

    public Matrix3x3(float m00, float m01, float m02,
                     float m10, float m11, float m12,
                     float m20, float m21, float m22) {
        m[0][0] = m00; m[0][1] = m01; m[0][2] = m02;
        m[1][0] = m10; m[1][1] = m11; m[1][2] = m12;
        m[2][0] = m20; m[2][1] = m21; m[2][2] = m22;
    }

    @Override
    public void copy(Matrix3x3 matrix) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.m[i][j] = matrix.m[i][j];
            }
        }
    }

    @Override
    public void set(float m00, float m01, float m02,
                    float m10, float m11, float m12,
                    float m20, float m21, float m22){
        this.m[0][0] = m00; this.m[0][1] = m01; this.m[0][2] = m02;
        this.m[1][0] = m10; this.m[1][1] = m11; this.m[1][2] = m12;
        this.m[2][0] = m20; this.m[2][1] = m21; this.m[2][2] = m22;
    }

    @Override
    public float[] convertTo1DArray() {
        float[] output = new float[9];

        output[0] = this.m[0][0]; output[1] = this.m[0][1]; output[2] = this.m[0][2];
        output[3] = this.m[1][0]; output[4] = this.m[1][1]; output[5] = this.m[1][2];
        output[6] = this.m[2][0]; output[7] = this.m[2][1]; output[8] = this.m[2][2];

        return output;
    }

    @Override
    public float[][] convertTo2DArray() {
        float[][] output = new float[3][3];

        output[0][0] = this.m[0][0]; output[0][1] = this.m[0][1]; output[0][2] = this.m[0][2];
        output[1][0] = this.m[1][0]; output[1][1] = this.m[1][1]; output[1][2] = this.m[1][2];
        output[2][0] = this.m[2][0]; output[2][1] = this.m[2][1]; output[2][2] = this.m[2][2];

        return output;
    }

    @Override
    public void print() {
        Log.d("Matrix3x3", m[0][0] + ", " + m[0][1] + ", " + m[0][2]);
        Log.d("Matrix3x3", m[1][0] + ", " + m[1][1] + ", " + m[1][2]);
        Log.d("Matrix3x3", m[2][0] + ", " + m[2][1] + ", " + m[2][2]);
    }

    @Override
    public Matrix3x3 _copy() {
        Matrix3x3 output = new Matrix3x3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output.m[i][j] = this.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Matrix3x3 _copy(Matrix3x3 matrix) {
        Matrix3x3 output = new Matrix3x3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output.m[i][j] = matrix.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Matrix3x3 _set(float m00, float m01, float m02,
                          float m10, float m11, float m12,
                          float m20, float m21, float m22){
        Matrix3x3 output = new Matrix3x3();

        output.m[0][0] = m00; output.m[0][1] = m01; output.m[0][2] = m02;
        output.m[1][0] = m10; output.m[1][1] = m11; output.m[1][2] = m12;
        output.m[2][0] = m20; output.m[2][1] = m21; output.m[2][2] = m22;

        return output;
    }

    @Override
    public void zero() {
        this.m[0][0] = 0f; this.m[0][1] = 0f; this.m[0][2] = 0f;
        this.m[1][0] = 0f; this.m[1][1] = 0f; this.m[1][2] = 0f;
        this.m[2][0] = 0f; this.m[2][1] = 0f; this.m[2][2] = 0f;
    }

    @Override
    public void identity() {
        this.m[0][0] = 1f; this.m[0][1] = 0f; this.m[0][2] = 0f;
        this.m[1][0] = 0f; this.m[1][1] = 1f; this.m[1][2] = 0f;
        this.m[2][0] = 0f; this.m[2][1] = 0f; this.m[2][2] = 1f;
    }

    @Override
    public void scale(float k) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.m[i][j] *= k;
            }
        }
    }

    @Override
    public void multiply(Matrix3x3 matrix) {
        Matrix3x3 temp = this._copy();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.m[i][j] = temp.m[i][0] * matrix.m[0][j] + temp.m[i][1] * matrix.m[1][j] + temp.m[i][2] * matrix.m[2][j];
            }
        }
    }

    @Override
    public void transpose() {
        Matrix3x3 temp = new Matrix3x3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                temp.m[i][j] = this.m[j][i];
            }
        }

        this.copy(temp);
    }

    @Override
    public float determinant() {
        Matrix2x2 A = new Matrix2x2(this.m[1][1], this.m[1][2],
                this.m[2][1], this.m[2][2]);
        Matrix2x2 B = new Matrix2x2(this.m[1][0], this.m[1][2],
                this.m[2][0], this.m[2][2]);
        Matrix2x2 C = new Matrix2x2(this.m[1][0], this.m[1][1],
                this.m[2][0], this.m[2][1]);

        return this.m[0][0] * A.determinant() -
                this.m[0][1] * B.determinant() +
                this.m[0][2] * C.determinant();

        // Corrected and verified!

        // If you take the first  number m00, crossing out the first row and first column leaves the 2x2 determinant  m11 * m22 - m12 * m21
        // If you take the second number m01, crossing out the first row and second column leaves the 2x2 determinant m10 * m22 - m12 * m20
        // If you take the third  number m02, crossing out the first row and third column leaves the 2x2 determinant  m10 * m21 - m11 * m20

        // To put it all together, the first set of numbers is positive, the second set is negative (important), and the third set is positive,
        // in a back and forth manner.
    }

    @Override
    public void adjugate(){
        Matrix2x2 A = new Matrix2x2(this.m[1][1], this.m[1][2],
                this.m[2][1], this.m[2][2]);
        Matrix2x2 B = new Matrix2x2(this.m[1][0], this.m[1][2],
                this.m[2][0], this.m[2][2]);
        Matrix2x2 C = new Matrix2x2(this.m[1][0], this.m[1][1],
                this.m[2][0], this.m[2][1]);
        Matrix2x2 D = new Matrix2x2(this.m[0][1], this.m[0][2],
                this.m[2][1], this.m[2][2]);
        Matrix2x2 E = new Matrix2x2(this.m[0][0], this.m[0][2],
                this.m[2][0], this.m[2][2]);
        Matrix2x2 F = new Matrix2x2(this.m[0][0], this.m[0][1],
                this.m[2][0], this.m[2][1]);
        Matrix2x2 G = new Matrix2x2(this.m[0][1], this.m[0][2],
                this.m[1][1], this.m[1][2]);
        Matrix2x2 H = new Matrix2x2(this.m[0][0], this.m[0][2],
                this.m[1][0], this.m[1][2]);
        Matrix2x2 I = new Matrix2x2(this.m[0][0], this.m[0][1],
                this.m[1][0], this.m[1][1]);

        Matrix3x3 output = new Matrix3x3(A.determinant(), -B.determinant(), C.determinant(),
                -D.determinant(), E.determinant(), -F.determinant(),
                G.determinant(), -H.determinant(), I.determinant());
        this.copy(output);
    }

    @Override
    public void inverse(){
        Matrix3x3 matrixOfMinors;
        Matrix3x3 transposedCoFactor;
        final float epsilon = 0.0000000001f;

        matrixOfMinors = this._adjugate();
        float determinant = this.determinant();
        float reciprocalDeterminant = 0.0f;

        if (determinant != 0.0f)
            reciprocalDeterminant = 1.0f / determinant;
        else
            reciprocalDeterminant = 1.0f / epsilon;

        transposedCoFactor = matrixOfMinors._transpose();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.m[i][j] = reciprocalDeterminant * transposedCoFactor.m[i][j];
            }
        }
    }

    @Override
    public Matrix3x3 _zero() {
        Matrix3x3 output = new Matrix3x3();

        output.m[0][0] = 0f; output.m[0][1] = 0f; output.m[0][2] = 0f;
        output.m[1][0] = 0f; output.m[1][1] = 0f; output.m[1][2] = 0f;
        output.m[2][0] = 0f; output.m[2][1] = 0f; output.m[2][2] = 0f;

        return output;
    }

    @Override
    public Matrix3x3 _identity() {
        Matrix3x3 output = new Matrix3x3();

        output.m[0][0] = 1f; output.m[0][1] = 0f; output.m[0][2] = 0f;
        output.m[1][0] = 0f; output.m[1][1] = 1f; output.m[1][2] = 0f;
        output.m[2][0] = 0f; output.m[2][1] = 0f; output.m[2][2] = 1f;

        return output;
    }

    @Override
    public Matrix3x3 _scale(float k) {
        Matrix3x3 output = new Matrix3x3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output.m[i][j] = this.m[i][j] * k;
            }
        }

        return output;
    }

    @Override
    public Matrix3x3 _multiply(Matrix3x3 matrix) {
        Matrix3x3 output = new Matrix3x3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output.m[i][j] = this.m[i][0] * matrix.m[0][j] + this.m[i][1] * matrix.m[1][j] + this.m[i][2] * this.m[2][j];
            }
        }

        return output;
    }

    @Override
    public Matrix3x3 _transpose() {
        Matrix3x3 output = new Matrix3x3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output.m[i][j] = this.m[j][i];
            }
        }
        return output;
    }

    @Override
    public Matrix3x3 _adjugate(){
        Matrix3x3 output;

        Matrix2x2 A = new Matrix2x2(this.m[1][1], this.m[1][2],
                this.m[2][1], this.m[2][2]);
        Matrix2x2 B = new Matrix2x2(this.m[1][0], this.m[1][2],
                this.m[2][0], this.m[2][2]);
        Matrix2x2 C = new Matrix2x2(this.m[1][0], this.m[1][1],
                this.m[2][0], this.m[2][1]);
        Matrix2x2 D = new Matrix2x2(this.m[0][1], this.m[0][2],
                this.m[2][1], this.m[2][2]);
        Matrix2x2 E = new Matrix2x2(this.m[0][0], this.m[0][2],
                this.m[2][0], this.m[2][2]);
        Matrix2x2 F = new Matrix2x2(this.m[0][0], this.m[0][1],
                this.m[2][0], this.m[2][1]);
        Matrix2x2 G = new Matrix2x2(this.m[0][1], this.m[0][2],
                this.m[1][1], this.m[1][2]);
        Matrix2x2 H = new Matrix2x2(this.m[0][0], this.m[0][2],
                this.m[1][0], this.m[1][2]);
        Matrix2x2 I = new Matrix2x2(this.m[0][0], this.m[0][1],
                this.m[1][0], this.m[1][1]);

        output = new Matrix3x3(A.determinant(), -B.determinant(), C.determinant(),
                -D.determinant(), E.determinant(), -F.determinant(),
                G.determinant(), -H.determinant(), I.determinant());

        return output;
    }

    @Override
    public Matrix3x3 _inverse(){
        Matrix3x3 matrixOfMinors;
        Matrix3x3 transposedCoFactor;
        Matrix3x3 output = new Matrix3x3();
        final float epsilon = 0.0000000001f;

        matrixOfMinors = this._adjugate();
        float determinant = this.determinant();
        float reciprocalDeterminant = 0.0f;

        if (determinant != 0.0f)
            reciprocalDeterminant = 1.0f / determinant;
        else
            reciprocalDeterminant = 1.0f / epsilon;

        transposedCoFactor = matrixOfMinors._transpose();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                output.m[i][j] = reciprocalDeterminant * transposedCoFactor.m[i][j];
            }
        }

        return output;
    }

    @Override
    public Vector3D multiplyVector(Vector3D vector){
        float x = this.m[0][0] * vector.x + this.m[1][0] * vector.y + this.m[2][0] * vector.z;
        float y = this.m[0][1] * vector.x + this.m[1][1] * vector.y + this.m[2][1] * vector.z;
        float z = this.m[0][2] * vector.x + this.m[1][2] * vector.y + this.m[2][2] * vector.z;

        return new Vector3D(x, y, z);
    }

    @Override
    public Vertex3D multiplyVertex(Vertex3D vertex){
        float x = this.m[0][0] * vertex.x + this.m[1][0] * vertex.y + this.m[2][0] * vertex.z;
        float y = this.m[0][1] * vertex.x + this.m[1][1] * vertex.y + this.m[2][1] * vertex.z;
        float z = this.m[0][2] * vertex.x + this.m[1][2] * vertex.y + this.m[2][2] * vertex.z;

        return new Vertex3D(x, y, z);
    }
}