package org.openlca.core.matrix;

import java.util.ArrayList;

import org.openlca.core.math.IMatrix;
import org.openlca.core.math.IMatrixFactory;

import gnu.trove.map.hash.TIntDoubleHashMap;

/**
 * A cost matrix contains the cost entries of a product system. The cost
 * categories are mapped to the rows and the process-products of the product
 * system are mapped to the columns.
 */
public class CostMatrix {

	public final ProductIndex productIndex;
	public final LongIndex categoryIndex;
	public final IMatrix values;

	CostMatrix(LongIndex categories, ProductIndex products, IMatrix values) {
		this.categoryIndex = categories;
		this.productIndex = products;
		this.values = values;
	}

	public boolean isEmpty() {
		return productIndex == null || productIndex.size() == 0
				|| categoryIndex == null || categoryIndex.size() == 0
				|| values == null;
	}

	public static CostMatrix build(Inventory inventory, IMatrixFactory<?> factory) {
		return new CostMatrixBuilder(inventory, factory).build();
	}

	private static class CostMatrixBuilder {

		private Inventory inventory;
		private IMatrixFactory<?> factory;

		private LongIndex catIndex;
		private ArrayList<TIntDoubleHashMap> values;

		private CostMatrixBuilder(Inventory inventory, IMatrixFactory<?> factory) {
			this.inventory = inventory;
			this.factory = factory;
			catIndex = new LongIndex();
			values = new ArrayList<>();
		}

		private CostMatrix build() {
			if (inventory == null)
				return new CostMatrix(null, null, null);
			scan(inventory.getTechnologyMatrix());
			scan(inventory.getInterventionMatrix());
			IMatrix values = createMatrix();
			return new CostMatrix(catIndex, inventory.getProductIndex(), values);
		}

		private void scan(ExchangeMatrix matrix) {
			if (matrix == null)
				return;
			matrix.iterate((row, col, cell) -> {
				double val = cell.getCostValue();
				if (val == 0 || cell.exchange == null) {
					return;
				}
				long category = cell.exchange.costCategory;
				int costRow = catIndex.put(category);
				add(costRow, col, val);
			});
		}

		private void add(int row, int col, double value) {
			TIntDoubleHashMap matrixRow = null;
			if (row < values.size())
				matrixRow = values.get(row);
			else {
				matrixRow = new TIntDoubleHashMap();
				values.add(matrixRow);
			}
			double existingValue = matrixRow.get(col);
			matrixRow.put(col, existingValue + value);
		}

		private IMatrix createMatrix() {
			if (factory == null || inventory.getProductIndex() == null)
				return null;
			IMatrix m = factory.create(catIndex.size(),
					inventory.getProductIndex().size());
			for (int i = 0; i < values.size(); i++) {
				int row = i; // effectively final
				TIntDoubleHashMap map = values.get(row);
				map.forEachEntry((col, value) -> {
					m.setEntry(row, col, value);
					return true;
				});
			}
			return m;
		}
	}
}
