/**
 * 
 */
package domainapp.modules.base.view.dashboard;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.hint.HintStore.HintIdProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.filter.TextFilterBuilder;
import domainapp.modules.base.service.AbstractFilterableService;
import domainapp.modules.base.service.ISessionStore;
import domainapp.modules.base.service.SessionStoreFactory;
import domainapp.modules.base.view.GenericFilter;
import domainapp.modules.base.view.Value;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract implementation of filterable dashboard
 * 
 * @author Prajapati
 * @see ManageTransactionDashboard
 */
public abstract class AbstractFilterableDashboard<T> implements HintIdProvider, ViewModel {

	@PropertyLayout(hidden = Where.EVERYWHERE)
	@Getter @Setter
	protected GenericFilter filter;
	
	@PropertyLayout(hidden = Where.EVERYWHERE)
	@Getter @Setter
	private GenericFilter previousFilter;
	
	/**
	 * Vanilla constructor
	 */
	protected AbstractFilterableDashboard() {
		// DO NOTHING
	}

	/**
	 * @return
	 */
	private String getFilterAttribute() {
		return "filter_" + getClass().getSimpleName();
	}
	
	@Programmatic
	public final void backupFilter() {
		this.previousFilter = filter;
	}
	
	@Programmatic
	public final void restoreFilter() {
		if (previousFilter != null) {
			return ;
		}
		this.filter = previousFilter;
		saveFilter();
	}

	/**
	 * @return instance of {@link GenericFilter} with default criteria
	 * @see ManageTransactionDashboard#defaultFilter()
	 */
	protected abstract GenericFilter defaultFilter();
	
	/**
	 * @return {@link AbstractFilterableService} instance corresponding to <T>
	 */
	protected abstract AbstractFilterableService<T> getFilterableService();
	
	/**
	 * @return list of filter fields that are supplying values explicitly in the filter string and must be excluded from query parameters
	 * @see TextFilterBuilder
	 */
	protected abstract List<String> getFilterFieldsToExcludeFromQueryParameter();	
	
	/**
	 * @param criteria
	 */
	@Programmatic
	protected void prepareFilter(Map<String, Object> criteria) {
		GenericFilter filter = new GenericFilter();
		filter.setExclude(new HashSet<String>(getFilterFieldsToExcludeFromQueryParameter()));
		Map<String, Value> parameters = filter.getParameters();
		filter.setFilter(getFilterableService().buildFilter(criteria, parameters));
		setFilter(filter);
		saveFilter();
	}
	
	/**
	 * Convert {@link GenericFilter} to JSON text and encode it with Base64 URL encoder
	 * 
	 * @return JSON text encoded with Base64 URL encoder
	 */
	@Programmatic
	private String filterToJson() {
		if (getFilter() == null) {
			setFilter(defaultFilter());
		}
		String json = createGsonBuilder().toJson(getFilter());
		return Base64.getUrlEncoder().encodeToString(json.getBytes());
	}

	/**
	 * Convert given JSON (Base64 URL encoded) text to {@link GenericFilter}
	 * 
	 * @param jsonUrlEncoded JSON text encoded with Base64 URL encoder
	 * @return {@link GenericFilter}
	 */
	@Programmatic
	private GenericFilter jsonToFilter(String jsonUrlEncoded) {
		String json = new String(Base64.getUrlDecoder().decode(jsonUrlEncoded.getBytes()));
		GenericFilter genericFilter = createGsonBuilder().fromJson(json, GenericFilter.class);
		return genericFilter;
	}
	
	/**
	 * This API save current filter in {@link ISessionStore}
	 */
	protected void saveFilter() {
		SessionStoreFactory.INSTANCE.getSessionStore().set(getFilterAttribute(), filterToJson());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.isis.applib.services.hint.HintStore.HintIdProvider#hintId()
	 */
	@Override
	public String hintId() {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(getFilterAttribute());
		if (filter != null) {
			setFilter(jsonToFilter(filter));
			return filter;
		}
		return filterToJson();
	}

	/* (non-Javadoc)
	 * @see org.apache.isis.applib.ViewModel#viewModelMemento()
	 */
	@Override
	public String viewModelMemento() {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(getFilterAttribute());
		if (filter != null) {
			return filter;
		}
		String json = filterToJson();
		return json;
	}

	/* (non-Javadoc)
	 * @see org.apache.isis.applib.ViewModel#viewModelInit(java.lang.String)
	 */
	@Override
	public void viewModelInit(String memento) {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(getFilterAttribute());
		if (filter == null) {
			filter = memento;
		}
		setFilter(jsonToFilter(filter));
	}

	/**
	 * Make use of {@link GsonBuilder} to create new instance of {@link Gson}
	 * 
	 * @return {@link Gson}
	 * TODO: Move this as static API in appropriate utility class
	 */
	private Gson createGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		return builder.create();
	}

	/**
	 * To get filter value as {@link List} which is stored against given key or field name
	 * 
	 * @param key or fieldname
	 * @return List<DT>
	 */
	@SuppressWarnings("unchecked")
	protected <DT> List<DT> getUserInputValueAsList(String key) {
		GenericFilter filter = getFilter();
		if (filter != null) {
			Value value = filter.getParameters().get(key);
			if (value != null) {
				Object object = DataTypeUtil.valueToObject(value);
				if (object instanceof List) {
					return (List<DT>)object;
				}
				return Arrays.asList((DT)object);
			}
		}
		return null;
	}

	/**
	 * To get filter value as DT which is stored against given key or field name
	 * 
	 * @param key
	 * @return DT
	 */
	@SuppressWarnings("unchecked")
	protected <DT> DT getUserInputValue(String key) {
		List<Object> userInputValueAsList = getUserInputValueAsList(key);
		if (userInputValueAsList == null || userInputValueAsList.isEmpty()) {
			return null;
		}
		return (DT)userInputValueAsList.get(0);
	}

}
