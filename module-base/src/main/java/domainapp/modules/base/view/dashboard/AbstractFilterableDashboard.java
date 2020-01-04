/**
 * 
 */
package domainapp.modules.base.view.dashboard;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.hint.HintStore.HintIdProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.service.SessionStoreFactory;
import domainapp.modules.base.view.GenericFilter;
import domainapp.modules.base.view.Value;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Prajapati
 *
 */
public abstract class AbstractFilterableDashboard implements HintIdProvider, ViewModel {

	@PropertyLayout(hidden = Where.EVERYWHERE)
	@Getter @Setter
	protected GenericFilter filter;
	
	protected AbstractFilterableDashboard() {
		// DO NOTHING
	}

	/**
	 * @return
	 */
	private String getFilterAttribute() {
		return "filter_" + getClass().getSimpleName();
	}

	protected abstract GenericFilter defaultFilter();

	/**
	 * @return
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
	 * @param jsonUrlEncoded
	 * @return 
	 */
	@Programmatic
	private GenericFilter jsonToFilter(String jsonUrlEncoded) {
		String json = new String(Base64.getUrlDecoder().decode(jsonUrlEncoded.getBytes()));
		GenericFilter genericFilter = createGsonBuilder().fromJson(json, GenericFilter.class);
		return genericFilter;
	}
	
	protected void saveFilter() {
		SessionStoreFactory.INSTANCE.getSessionStore().set(getFilterAttribute(), filterToJson());
	}
	
	@Override
	public String hintId() {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(getFilterAttribute());
		if (filter != null) {
			setFilter(jsonToFilter(filter));
			return filter;
		}
		return filterToJson();
	}

	@Override
	public String viewModelMemento() {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(getFilterAttribute());
		if (filter != null) {
			return filter;
		}
		String json = filterToJson();
		return json;
	}

	@Override
	public void viewModelInit(String memento) {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(getFilterAttribute());
		if (filter == null) {
			filter = memento;
		}
		setFilter(jsonToFilter(filter));
	}

	/**
	 * @return
	 */
	private Gson createGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		return builder.create();
	}

	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> getUserInputValueAsList(String key) {
		GenericFilter filter = getFilter();
		if (filter != null) {
			Value value = filter.getParameters().get(key);
			if (value != null) {
				Object object = DataTypeUtil.valueToObject(value);
				if (object instanceof List) {
					return (List<T>)object;
				}
				return Arrays.asList((T)object);
			}
		}
		return null;
	}

	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getUserInputValue(String key) {
		List<Object> userInputValueAsList = getUserInputValueAsList(key);
		if (userInputValueAsList == null || userInputValueAsList.isEmpty()) {
			return null;
		}
		return (T)userInputValueAsList.get(0);
	}

}
