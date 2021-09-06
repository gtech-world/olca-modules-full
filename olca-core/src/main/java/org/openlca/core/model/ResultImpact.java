package org.openlca.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_result_impacts")
public class ResultImpact {

	@OneToOne
	@JoinColumn(name = "f_impact_category")
	public ImpactCategory indicator;

	@Column(name = "amount")
	public double amount;

	@Column(name = "description")
	public String description;

	@Override
	protected ResultImpact clone() {
		var clone = new ResultImpact();
		clone.indicator = indicator;
		clone.amount = amount;
		clone.description = description;
		return clone;
	}
}
