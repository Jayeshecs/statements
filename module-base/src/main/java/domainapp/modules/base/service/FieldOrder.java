package domainapp.modules.base.service;

import lombok.Getter;

public class FieldOrder {
	
	@Getter
	private String fieldName;
	
	@Getter
	private boolean ascending;

	public FieldOrder(String fieldName, boolean ascending) {
		this.fieldName = fieldName;
		this.ascending = ascending;
	}
	
	public String getOrdering() {
		return new StringBuilder(" ").append(getFieldName()).append(" ").append(ascending ? "ascending" : "descending").toString();
	}
}