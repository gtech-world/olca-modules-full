package org.openlca.ilcd.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		"dataSetInfo",
})
public class ModelInfo {

	@XmlElement(name = "dataSetInformation")
	public DataSetInfo dataSetInfo;

}
