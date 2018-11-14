package org.openlca.core.matrix.format;

public final class MatrixConverter {

	private MatrixConverter() {
	}

	public static DenseMatrix dense(IMatrix m) {
		if (m instanceof DenseMatrix)
			return (DenseMatrix) m;

		int rows = m.rows();
		int cols = m.columns();
		DenseMatrix d = new DenseMatrix(rows, cols);

		if (m instanceof HashMatrix) {
			HashMatrix s = (HashMatrix) m;
			s.iterate((row, col, val) -> d.set(row, col, val));
			return d;
		}

		// default implementation
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				d.set(row, col, m.get(row, col));
			}
		}
		return d;
	}

	public static HashMatrix hashSparse(IMatrix matrix) {
		if (matrix instanceof HashMatrix)
			return (HashMatrix) matrix;

		int rows = matrix.rows();
		int cols = matrix.columns();
		HashMatrix sparse = new HashMatrix(rows, cols);

		for (int col = 0; col < cols; col++) {
			for (int row = 0; row < rows; row++) {
				double val = matrix.get(row, col);
				if (val == 0)
					continue;
				sparse.set(row, col, val);
			}
		}
		return sparse;
	}

}
