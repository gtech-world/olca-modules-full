package org.openlca.core.model.descriptors;

import org.openlca.core.model.ModelType;
import org.openlca.core.model.ProcessType;

public class ProcessDescriptor extends CategorizedDescriptor {

	public ProcessType processType;
	public boolean infrastructureProcess;
	public Long location;
	public Long quantitativeReference; // TODO: is this field used?, why?

	public ProcessDescriptor() {
		this.type = ModelType.PROCESS;
	}

	@Override
	public ProcessDescriptor copy() {
		var copy = new ProcessDescriptor();
		copyFields(this, copy);
		copy.processType = processType;
		copy.infrastructureProcess = infrastructureProcess;
		copy.location = location;
		copy.quantitativeReference = quantitativeReference;
		return copy;
	}

}
