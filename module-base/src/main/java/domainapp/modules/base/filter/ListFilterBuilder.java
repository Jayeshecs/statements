/**
 * 
 */
package domainapp.modules.base.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.datatype.IDataType;
import domainapp.modules.base.view.Value;

/**
 * @author Prajapati
 *
 */
public class ListFilterBuilder extends FilterBuilder {

	private IDataType dataType;

	public ListFilterBuilder(String fieldName, IDataType dataType) {
		super(fieldName);
		this.dataType = dataType;
	}

	@Override
	public String buildFilterString(Map<String, Object> criteria, Map<String, Value> parameters, boolean addAnd) {
		String fieldName = getFieldName();
		List<?> value = null;
		Object criteriaValue = criteria.get(fieldName);
		if (criteriaValue == null) {
			// no value available and hence there is nothing to be done
			return null;
		}
		if (!(criteriaValue instanceof List)) {
			value = Arrays.asList(criteriaValue);
		} else {
			value = (List<?>)criteriaValue;
		}
		if (value.isEmpty()) {
			// no value available and hence there is nothing to be done
			return null;
		}
		StringBuilder filter = new StringBuilder();
		ensureAndPrefixed(filter, addAnd);
		filter.append(":" + fieldName + ".contains(" + fieldName + ")");
		parameters.put(fieldName, DataTypeUtil.createValue(dataType, value));
		return filter.toString();
	}

}
