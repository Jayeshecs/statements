/**
 * 
 */
package domainapp.modules.base.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.filter.FilterBuilder;
import domainapp.modules.base.view.Value;

/**
 * @author Prajapati
 *
 */
public abstract class AbstractFilterableService<T> extends AbstractService<T> {
	
	private Map<String, FilterBuilder> fieldFilterBuilders;
	
	/**
	 * Scan through annotations on given Class<T>
	 * 
	 * @param clazz
	 */
	protected AbstractFilterableService(Class<T> clazz) {
		super(clazz);
		initializeFieldFilterBuilders(clazz);
	}

	/**
	 * @param clazz
	 */
	private void initializeFieldFilterBuilders(Class<T> clazz) {
		fieldFilterBuilders = new HashMap<String, FilterBuilder>();
		registerFieldFilterBuilders();
	}
	
	protected abstract void registerFieldFilterBuilders();
	
	protected final void registerFieldFilter(String fieldName, FilterBuilder filterBuilder) {
		fieldFilterBuilders.put(fieldName, filterBuilder);
	}
	
	/**
	 * @param criteria
	 * @param parameters
	 * @return
	 */
	@Programmatic
	public String buildFilter(Map<String, Object> criteria, Map<String, Value> parameters) {
		StringBuilder filter = new StringBuilder();
		boolean[] addAnd = new boolean[] { false };
		criteria.entrySet().forEach(entry -> {
			if (entry.getValue() == null) {
				// no value then do nothing
				return ;
			}
			String fieldName = entry.getKey();
			FilterBuilder filterBuilder = fieldFilterBuilders.get(fieldName);
			String filterString = filterBuilder.buildFilterString(criteria, parameters, addAnd[0]);
			if (filterString != null) {
				filter.append(filterString);
				addAnd[0] = true;
			}
		});
		return filter.toString();
	}
	
}
