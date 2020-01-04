/**
 * 
 */
package domainapp.modules.base.filter;

import java.util.Map;

import domainapp.modules.base.view.Value;
import lombok.Getter;

/**
 * Abstract implementation for filter builder
 * 
 * @author Prajapati
 */
public abstract class FilterBuilder {

	@Getter
	private String fieldName;

	/**
	 * @param fieldName
	 */
	public FilterBuilder(String fieldName) {
		this.fieldName = fieldName;
	}
	
	/**
	 * @param criteria
	 * @param parameters
	 * @param addAnd
	 * @return
	 */
	public abstract String buildFilterString(Map<String, Object> criteria, Map<String, Value> parameters, boolean addAnd);

	protected void ensureAndPrefixed(StringBuilder filter, boolean addAnd) {
		if (addAnd) {
			filter.append(" && ");
		}
	}
	
}
