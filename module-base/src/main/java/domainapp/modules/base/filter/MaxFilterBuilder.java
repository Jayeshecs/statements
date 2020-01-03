/**
 * 
 */
package domainapp.modules.base.filter;

import java.util.Map;

import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.datatype.IDataType;
import domainapp.modules.base.view.Value;

/**
 * @author Prajapati
 *
 */
public class MaxFilterBuilder extends FilterBuilder {
	
	private IDataType dataType;

	public MaxFilterBuilder(String fieldName, IDataType dataType) {
		super(fieldName);
		this.dataType = dataType;
	}

	@Override
	public String buildFilterString(Map<String, Object> criteria, Map<String, Value> parameters, boolean addAnd) {
		String fieldName = getFieldName();
		String parameterName = fieldName + "Max";
		
		Object criteriaValue = criteria.get(parameterName);
		if (criteriaValue == null) {
			// no value available and hence there is nothing to be done
			return null;
		}
		StringBuilder filter = new StringBuilder();
		ensureAndPrefixed(filter, addAnd);
		filter.append(fieldName + " <= :" + parameterName);
		parameters.put(parameterName, DataTypeUtil.createValue(dataType, criteriaValue));
		return filter.toString();
	}

}
