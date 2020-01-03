/**
 * 
 */
package domainapp.modules.txn.service;

import java.util.Map;

import domainapp.modules.base.datatype.DataType;
import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.filter.FilterBuilder;
import domainapp.modules.base.view.Value;

/**
 * @author Prajapati
 *
 */
public class UncategorizedFilterBuilder extends FilterBuilder {
	
	public UncategorizedFilterBuilder(String fieldName) {
		super(fieldName);
	}

	@Override
	public String buildFilterString(Map<String, Object> criteria, Map<String, Value> parameters, boolean addAnd) {
		String fieldName = getFieldName();
		
		Boolean criteriaValue = (Boolean) criteria.get(fieldName);
		if (criteriaValue == null || !criteriaValue) {
			// no value available and hence there is nothing to be done
			return null;
		}
		StringBuilder filter = new StringBuilder();
		ensureAndPrefixed(filter, addAnd);
		//parameters.put(fieldName, DataTypeUtil.createValue(DataType.YESNO, Boolean.TRUE));
		filter.append("(category == null && subCategory == null) ");
		addAnd = true;
		return filter.toString();
	}

}
