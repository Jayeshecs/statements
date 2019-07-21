package domainapp.modules.base.service;

import java.util.ArrayList;
import java.util.Collection;

public class OrderBy {
	
	private Collection<FieldOrder> fieldOrderList = new ArrayList<>();
	
	public OrderBy() {
		// DO NOTHING
	}
	
	public void add(String fieldName, boolean ascending) {
		add(new FieldOrder(fieldName, ascending));
	}
	
	public void add(FieldOrder fieldOrder) {
		fieldOrderList.add(fieldOrder);
	}
	
	public String getOrdering() {
		if (fieldOrderList.isEmpty()) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		fieldOrderList.stream().forEach(fo -> {
			sb.append(fo.getOrdering());
		});
		return sb.toString();
	}
}