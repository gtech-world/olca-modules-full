package org.openlca.core.results;

import java.util.ArrayList;
import java.util.List;

import org.openlca.core.database.EntityCache;
import org.openlca.core.matrix.FlowIndex;
import org.openlca.core.matrix.NwSetTable;
import org.openlca.core.model.descriptors.FlowDescriptor;
import org.openlca.core.model.descriptors.ImpactCategoryDescriptor;

public class SimpleResultProvider<T extends SimpleResult> extends
		BaseResultProvider<T> {

	public SimpleResultProvider(T result, EntityCache cache) {
		super(result, cache);
	}

	/**
	 * Returns the flow results of the inventory results. *No* entries are
	 * generated for 0-values.
	 */
	public List<FlowResult> getTotalFlowResults() {
		FlowIndex index = result.getFlowIndex();
		List<FlowResult> results = new ArrayList<>();
		for (FlowDescriptor d : getFlowDescriptors()) {
			double val = result.getTotalFlowResult(d.getId());
			if (val == 0)
				continue;
			FlowResult r = new FlowResult();
			r.setFlow(d);
			r.setInput(index.isInput(d.getId()));
			r.setValue(val);
			results.add(r);
		}
		return results;
	}

	public FlowResult getTotalFlowResult(FlowDescriptor flow) {
		FlowIndex index = result.getFlowIndex();
		long flowId = flow.getId();
		FlowResult r = new FlowResult();
		r.setFlow(flow);
		r.setInput(index.isInput(flowId));
		double val = result.getTotalFlowResult(flow.getId());
		r.setValue(val);
		return r;
	}

	/** Switches the sign for input-flows. */
	protected double adoptFlowResult(double value, long flowId) {
		if (value == 0)
			return 0; // avoid -0 in the results
		boolean inputFlow = result.getFlowIndex().isInput(flowId);
		return inputFlow ? -value : value;
	}

	/**
	 * Returns the impact category results for the given result. In contrast to
	 * the flow results, entries are also generated for 0-values.
	 */
	public List<ImpactResult> getTotalImpactResults() {
		List<ImpactResult> results = new ArrayList<>();
		for (ImpactCategoryDescriptor d : getImpactDescriptors()) {
			ImpactResult r = getTotalImpactResult(d);
			results.add(r);
		}
		return results;
	}

	public List<ImpactResult> getTotalImpactResults(NwSetTable nwset) {
		List<ImpactResult> results = new ArrayList<>();
		for (ImpactCategoryDescriptor d : getImpactDescriptors()) {
			ImpactResult r = getTotalImpactResult(d, nwset);
			results.add(r);
		}
		return results;
	}

	public ImpactResult getTotalImpactResult(
			ImpactCategoryDescriptor impact) {
		return getTotalImpactResult(impact, null);
	}

	public ImpactResult getTotalImpactResult(
			ImpactCategoryDescriptor impact, NwSetTable nwset) {
		double val = result.getTotalImpactResult(impact.getId());
		ImpactResult r = new ImpactResult();
		r.setImpactCategory(impact);
		r.setValue(val);
		if (nwset == null) {
			return r;
		}
		long impactId = impact.getId();
		r.setNormalizationFactor(nwset.getNormalisationFactor(impactId));
		r.setWeightingFactor(nwset.getWeightingFactor(impactId));
		return r;
	}
}
