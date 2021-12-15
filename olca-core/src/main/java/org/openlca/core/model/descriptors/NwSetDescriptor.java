package org.openlca.core.model.descriptors;

import org.openlca.core.model.ModelType;

public class NwSetDescriptor extends Descriptor {

	public String weightedScoreUnit;

	public NwSetDescriptor() {
		this.type = ModelType.NW_SET;
	}

	@Override
	public NwSetDescriptor copy() {
		var copy = new NwSetDescriptor();
		copyFields(this, copy);
		copy.weightedScoreUnit = weightedScoreUnit;
		return copy;
	}

}
