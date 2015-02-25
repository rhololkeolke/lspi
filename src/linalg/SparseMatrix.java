package linalg;

import Jama.Matrix;

import java.util.HashMap;
import java.util.Map;

// Todo: refactor this class as an interface to allow switching sparse types
public class SparseMatrix {

    private class Index {
        public final int i, j;

        public Index(int i, int j)
        {
            if(i < 0 || j < 0)
                throw new IllegalArgumentException("Indices must be >= 0");

            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Index index = (Index) o;

            if (i != index.i) return false;
            if (j != index.j) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + j;
            return result;
        }
    }

    private Map<Index, Double> values;
    private final int numRows, numCols;

    public SparseMatrix(int numRows, int numCols)
    {
        values = new HashMap<>();

        this.numRows = numRows;
        this.numCols = numCols;
    }

    public SparseMatrix(int numRows, int numCols, int numNonZero)
    {
        values = new HashMap<>(numNonZero);

        this.numRows = numRows;
        this.numCols = numCols;
    }

    public SparseMatrix(SparseMatrix matrix)
    {
        values = new HashMap<>(matrix.size());

        for(Map.Entry<Index, Double> entry : matrix.values.entrySet())
        {
            values.put(entry.getKey(), entry.getValue());
        }

        this.numRows = matrix.numRows;
        this.numCols = matrix.numCols;
    }

    // Convert a dense matrix to a sparse matrix
    public SparseMatrix(Matrix matrix)
    {
        values = new HashMap<>(matrix.getRowDimension()*matrix.getColumnDimension());

        for(int i=0; i<matrix.getRowDimension(); i++)
        {
            for(int j=0; j<matrix.getColumnDimension(); j++)
            {
                values.put(new Index(i, j), matrix.get(i, j));
            }
        }

        this.numRows = matrix.getRowDimension();
        this.numCols = matrix.getColumnDimension();
    }

    public int size()
    {
        return values.size();
    }

    public double get(int i, int j)
    {
        if(i < 0 || i > numRows || j < 0 || j > numCols)
            throw new IndexOutOfBoundsException("Invalid matrix index (" + i +"," + j + ")");

        return values.getOrDefault(new Index(i, j), 0.0);
    }

    public void set(int i, int j, double value)
    {
        if(i < 0 || i > numRows || j < 0 || j > numCols)
            throw new IndexOutOfBoundsException("Invalid matrix index (" + i +"," + j + ")");
        values.put(new Index(i, j), value);
    }

    // add value to current value at index (i, j)
    public void update(int i, int j, double value)
    {
        if(i < 0 || i > numRows || j < 0 || j > numCols)
            throw new IndexOutOfBoundsException("Invalid matrix index (" + i +"," + j + ")");

        Index index = new Index(i, j);
        values.put(index, values.getOrDefault(index, 0.0) + value);
    }

    // sparse matrix times a dense vector
    public Matrix times(Matrix x)
    {
        if(x.getColumnDimension() > 1)
            throw new IllegalArgumentException("Can only multiply by a dense vector");

        if(x.getRowDimension() != numCols)
            throw new IllegalArgumentException("Matrix and vector dimensions don't match." +
                    " (" + numRows + "," + numCols + ") times " + x.getRowDimension() + " vector.");

        Matrix y = new Matrix(x.getRowDimension(), 1);

        for(Map.Entry<Index, Double> entry : values.entrySet())
        {
            Index key = entry.getKey();
            y.set(key.i, 0, y.get(key.i, 0) + entry.getValue()*x.get(key.j, 0));
        }

        return y;
    }

    // in place multiplication
    public SparseMatrix times(double x)
    {
        for(Map.Entry<Index, Double> entry : values.entrySet())
        {
            entry.setValue(entry.getValue()*x);
        }

        return this;
    }

    public static SparseMatrix identity(int n)
    {
        SparseMatrix result = new SparseMatrix(n, n, n);
        for(int i=0; i<n; i++)
        {
            result.set(i, i, 1.0);
        }
        return result;
    }

    public static SparseMatrix diagonal(int n, double value)
    {
        SparseMatrix result = new SparseMatrix(n, n, n);
        for(int i=0; i<n; i++)
        {
            result.set(i, i, value);
        }
        return result;
    }
}
