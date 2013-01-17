/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu;

import android.graphics.Matrix;

/**
 * Standard Matrix operations take place here. Let this be your matrix buddy.
 * 
 * @author Chet
 */
public class MatrixBuddy
{

    public static final float[][] invertMatrix(float[][] A)
    {

	float B[][] = new float[3][3];// the transpose of a matrix A
	float C[][] = new float[3][3];// the adjunct matrix of transpose of a matrix A not adjunct of A
	float X[][] = new float[3][3];// the inverse
	int i, j;
	float x, n = 0;// n is the determinant of A

	for (i = 0; i < 3; i++)
	{
	    for (j = 0; j < 3; j++)
	    {
		B[i][j] = 0;
		C[i][j] = 0;
	    }
	}

	for (i = 0, j = 0; j < 3; j++)
	{
	    if (j == 2)
	    {
		n += A[i][j] * A[i + 1][0] * A[i + 2][1];
	    }
	    else
		if (j == 1)
		{
		    n += A[i][j] * A[i + 1][j + 1] * A[i + 2][0];
		}
		else
		{
		    n += A[i][j] * A[i + 1][j + 1] * A[i + 2][j + 2];
		}
	}
	for (i = 2, j = 0; j < 3; j++)
	{
	    if (j == 2)
	    {
		n -= A[i][j] * A[i - 1][0] * A[i - 2][1];
	    }
	    else
		if (j == 1)
		{
		    n -= A[i][j] * A[i - 1][j + 1] * A[i - 2][0];
		}
		else
		{
		    n -= A[i][j] * A[i - 1][j + 1] * A[i - 2][j + 2];
		}
	}

	x = 0;

	if (n != 0)
	{
	    x = 1.0f / n;
	}
	else
	{
	    System.out.printf("Division by 0, not good!\n");

	}

	for (i = 0; i < 3; i++)
	{
	    for (j = 0; j < 3; j++)
	    {

		B[i][j] = A[j][i];

	    }
	}

	C[0][0] = B[1][1] * B[2][2] - (B[2][1] * B[1][2]);
	C[0][1] = (-1) * (B[1][0] * B[2][2] - (B[2][0] * B[1][2]));
	C[0][2] = B[1][0] * B[2][1] - (B[2][0] * B[1][1]);

	C[1][0] = (-1) * (B[0][1] * B[2][2] - B[2][1] * B[0][2]);
	C[1][1] = B[0][0] * B[2][2] - B[2][0] * B[0][2];
	C[1][2] = (-1) * (B[0][0] * B[2][1] - B[2][0] * B[0][1]);

	C[2][0] = B[0][1] * B[1][2] - B[1][1] * B[0][2];
	C[2][1] = (-1) * (B[0][0] * B[1][2] - B[1][0] * B[0][2]);
	C[2][2] = B[0][0] * B[1][1] - B[1][0] * B[0][1];

	for (i = 0; i < 3; i++)
	{
	    for (j = 0; j < 3; j++)
	    {
		X[i][j] = C[i][j] * x;

	    }
	}

	return X;

    }

    public static final float[][] oneDto2D(float[] a)
    {
	float b[][] = new float[3][3];
	b[0][0] = a[0];
	b[0][1] = a[1];
	b[0][2] = a[2];
	b[1][0] = a[3];
	b[1][1] = a[4];
	b[1][2] = a[5];
	b[2][0] = a[6];
	b[2][1] = a[7];
	b[2][2] = a[8];
	return b;
    }

    public static final float[][] multiplyMatrices(float[][] A, float[][] B)
    {
	int m, n, p, q;
	float sum = 0;
	int i, j, k;

	m = A.length;
	n = A[0].length;

	// System.out.println("SizeA: "+m+"x"+n);

	// System.out.println("Enter the number of rows and columns of first matrix");
	p = B.length;
	q = B[0].length;

	// System.out.println("SizeB "+p+"x"+q);
	if (n != p)
	{
	    System.out.println("Matrices with entered orders can't be multiplied with each other.");
	}
	else
	{
	    float mul[][] = new float[m][q];

	    for (i = 0; i < m; i++)
	    {
		for (j = 0; j < q; j++)
		{
		    for (k = 0; k < p; k++)
		    {
			sum = sum + A[i][k] * B[k][j];
		    }

		    mul[i][j] = sum;
		    sum = 0;
		}
	    }

	    // System.out.println("Product of entered matrices:-");

	    return mul;

	}

	return null;
    }

    /**
     * This is used as a substitute for Matrix.MapPoints() because for some reason it does
     * not work in some cases. this transforms the coordinates to whatever matrix is supplied. 
     * inv specifies whether the matrix is being used for the transform or the inverse of the
     * matrix.
     */
    public static final float[][] transform(float x, float y, Matrix matrix, boolean inv)
    {
	float clickCoord[][] = new float[3][1];
	
	clickCoord[0][0] = x;
	clickCoord[1][0] = y;
	clickCoord[2][0] = 1.0f;

	Constants.dump2DFloatArrayToFile("clickCoords_MatrixBuddy", clickCoord);
	float[] ff = new float[9];
	matrix.getValues(ff);
	Constants.dump1DFloatArrayToFile("matrix_MatrixBuddy", ff);
	
	// Copy the matrix to a new matrix I. This is a deep copy
	Matrix I = new Matrix();
	I.set(matrix);
	
	// Invert matrix if the invert flag is set 
	if (inv)
	    I.invert(I);

	float[] ii = new float[9];
	I.getValues(ii);
	Constants.dump1DFloatArrayToFile("I_MatrixBuddy", ii);

	// Make a copy of the inverted matrix
	float val[] = new float[9];
	I.getValues(val);
	Constants.dump1DFloatArrayToFile("val_MatrixBuddy", val);
	
	// NOTE: oneDto2D converts the 9 element linear matrix returned from getValues() back to a 3x3 matrix 
	clickCoord = MatrixBuddy.multiplyMatrices(MatrixBuddy.oneDto2D(val), clickCoord);	
	
	Constants.dump2DFloatArrayToFile("returned_clickCoords_MatrixBuddy", clickCoord);
	
	return clickCoord;

    }
}
