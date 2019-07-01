package domainapp.modules.base.reader;

import java.util.HashMap;
import java.util.Map;

/**
 * Statement reader record
 */
public class AbstractRecord<F extends Enum<?>> implements IRecord<F> {
	
	private Map<F, Object> values;
	
	public AbstractRecord() {
		values = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(F field) {
		return (T) values.get(field);
	}
	
	public <T> IRecord<F> set(F field, T value) {
		values.put(field, value);
		return this;
	}
}