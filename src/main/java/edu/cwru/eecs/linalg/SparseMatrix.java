package edu.cwru.eecs.linalg;

import Jama.Matrix;

import java.util.HashMap;
import java.util.Map;

// Todo: refactor this class as an interface to allow switching sparse types
public class SparseMatrix {

    private class Index {

        public final int rows;
        public final int cols;

        public Index(int rows, int cols) {
            if (rows < 0 || cols < 0) {
                throw new IllegalArgumentException("Indices must be >= 0");
            }

            this.rows = rows;
            this.cols = cols;
        }

        @Override
        public boolean equals(Object other) {

            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            Index index = (Index) other;

            if (rows != index.rows) {
                return false;
            }
            if (cols != index.cols) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = rows;
            result = 31 * result + cols;
            return result;
        }
    }

    private Map<Index, Double> values;
    public final int numRows;
    public final int numCols;

    /**
     * Constructs an empty sparse matrix with the specified number of rows and columns.
     *
     * @param numRows Number of rows for this matrix
     * @param numCols Number of columns for this matrix
     */
    public SparseMatrix(int numRows, int numCols) {
        values = new HashMap<>();

        this.numRows = numRows;
        this.numCols = numCols;
    }

    /**
     * Constructs an empty sparse matrix with the specified number of rows and columns, but
     * preallocates the specified number of spaces to be filled with non-zero elements.
     *
     * @param numRows    Number of rows for this matrix
     * @param numCols    Number of columns for this matrix
     * @param numNonZero Number of preallocated non-zero elements
     */
    public SparseMatrix(int numRows, int numCols, int numNonZero) {
        values = new HashMap<>(numNonZero);

        this.numRows = numRows;
        this.numCols = numCols;
    }

    /**
     * Copies a sparse matrix.
     *
     * @param matrix Matrix to copy
     */
    public SparseMatrix(SparseMatrix matrix) {
        values = new HashMap<>(matrix.size());

        for (Map.Entry<Index, Double> entry : matrix.values.entrySet()) {
            values.put(entry.getKey(), entry.getValue());
        }

        this.numRows = matrix.numRows;
        this.numCols = matrix.numCols;
    }

    /**
     * Converts a dense matrix to a sparse matrix.
     *
     * @param matrix Dense matrix to convert
     */
    public SparseMatrix(Matrix matrix) {
        values = new HashMap<>(matrix.getRowDimension() * matrix.getColumnDimension());

        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                values.put(new Index(i, j), matrix.get(i, j));
            }
        }

        this.numRows = matrix.getRowDimension();
        this.numCols = matrix.getColumnDimension();
    }

    public int size() {
        return values.size();
    }

    /**
     * Get the element at the specified row and column.
     *
     * @param row Row of the element
     * @param col Column of the element
     * @return The value of the element at that index
     */
    public double get(int row, int col) {
        if (row < 0 || row > numRows || col < 0 || col > numCols) {
            throw new IndexOutOfBoundsException("Invalid matrix index (" + row + "," + col + ")");
        }

        return values.getOrDefault(new Index(row, col), 0.0);
    }

    /**
     * Set the element at the specified row and column to the specified value.
     *
     * @param row   Row of the element
     * @param col   Column of the element
     * @param value Value to set element to
     */
    public void set(int row, int col, double value) {
        if (row < 0 || row > numRows || col < 0 || col > numCols) {
            throw new IndexOutOfBoundsException("Invalid matrix index (" + row + "," + col + ")");
        }
        values.put(new Index(row, col), value);
    }

    /**
     * Add the specified value to the value at the specified row and column.
     *
     * @param row   Row of the element to update
     * @param col   Column of the element to update
     * @param value Value to add to the existing element
     */
    public void update(int row, int col, double value) {
        if (row < 0 || row > numRows || col < 0 || col > numCols) {
            throw new IndexOutOfBoundsException("Invalid matrix index (" + row + "," + col + ")");
        }

        Index index = new Index(row, col);
        values.put(index, values.getOrDefault(index, 0.0) + value);
    }

    /**
     * Multiply this sparse matrix by the specified dense vector.
     *
     * @param denseMat The dense vector to multiply by
     * @return The resulting dense vector
     */
    public Matrix times(Matrix denseMat) {
        if (denseMat.getColumnDimension() > 1) {
            throw new IllegalArgumentException("Can only multiply by a dense vector");
        }

        if (denseMat.getRowDimension() != numCols) {
            throw new IllegalArgumentException("Matrix and vector dimensions don't match."
                                               + " (" + numRows + "," + numCols + ") times "
                                               + denseMat.getRowDimension() + " vector.");
        }

        Matrix resultMat = new Matrix(denseMat.getRowDimension(), 1);

        for (Map.Entry<Index, Double> entry : values.entrySet()) {
            Index key = entry.getKey();
            resultMat.set(key.rows, 0, resultMat.get(key.rows, 0)
                    + entry.getValue() * denseMat.get(key.cols, 0));
        }

        return resultMat;
    }

    /**
     * In place multiplication of a scalar.
     *
     * @param scalar Scalar to multiply by
     * @return This matrix
     */
    public SparseMatrix times(double scalar) {
        for (Map.Entry<Index, Double> entry : values.entrySet()) {
            entry.setValue(entry.getValue() * scalar);
        }

        return this;
    }

    public double dot(Matrix denseMat) {
        if (denseMat.getColumnDimension() > 1) {
            throw new IllegalArgumentException("Can only multiply by a vector");
        }

        if (denseMat.getRowDimension() != numRows) {
            throw new IllegalArgumentException("Vector dimensions don't match.");
        }

        double total = 0;
        for (Map.Entry<Index, Double> entry : values.entrySet()) {
            Index key = entry.getKey();
            total += entry.getValue() * denseMat.get(key.rows, 0);
        }
        return total;
    }

    public double dot(SparseMatrix sparseMat) {
        if (sparseMat.numCols > 1) {
            throw new IllegalArgumentException("Can only multiply by a vector");
        }

        if (sparseMat.numRows != numRows) {
            throw new IllegalArgumentException("Vector dimensions don't match");
        }

        double total = 0;
        for (Map.Entry<Index, Double> entry : values.entrySet()) {
            Index key = entry.getKey();
            total += entry.getValue() * sparseMat.get(key.rows, 0);
        }
        return total;
    }

    /**
     * Construct a square identity matrix of the specified size.
     *
     * @param matrixDim Size of the square matrix
     * @return Identity matrix that is matrixDim x matrixDim in size
     */
    public static SparseMatrix identity(int matrixDim) {
        SparseMatrix result = new SparseMatrix(matrixDim, matrixDim, matrixDim);
        for (int i = 0; i < matrixDim; i++) {
            result.set(i, i, 1.0);
        }
        return result;
    }

    /**
     * Constructs a sparse matrix with the diagonal set to the specified value.
     *
     * @param matrixDim Size of the square matrix
     * @param value     Value to set the diagonals to
     * @return Resulting sparse diagonal matrix
     */
    public static SparseMatrix diagonal(int matrixDim, double value) {
        SparseMatrix result = new SparseMatrix(matrixDim, matrixDim, matrixDim);
        for (int i = 0; i < matrixDim; i++) {
            result.set(i, i, value);
        }
        return result;
    }
}
