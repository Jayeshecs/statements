/**
 * 
 */
package domainapp.modules.base.reader;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation of {@link IReaderContext}
 * 
 * @author Prajapati
 * @see DefaultReaderContext
 */
public abstract class AbstractReaderContext<RC extends IReaderContext<RC>> implements IReaderContext<RC> {

	private Long id;
	private String name;
	private Map<String, Object> parameters;

	/**
	 * @param id
	 * @param name
	 */
	public AbstractReaderContext(Long id, String name) {
		this.id = id;
		this.name = name;
		this.parameters = new HashMap<String, Object>();
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) parameters.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> RC set(String key, T value) {
		parameters.put(key, value);
		return (RC)this;
	}

}
