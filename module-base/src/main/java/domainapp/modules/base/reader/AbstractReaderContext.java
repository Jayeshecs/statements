/**
 * 
 */
package domainapp.modules.base.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
	private AtomicInteger counterTotal = new AtomicInteger(0);
	private AtomicInteger counterFiltered = new AtomicInteger(0);
	private AtomicInteger counterError = new AtomicInteger(0);
	private AtomicInteger counterSkipped = new AtomicInteger(0);

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
	
	@Override
	public int addTotalCount(int quantity) {
		return counterTotal.addAndGet(quantity);
	}
	
	@Override
	public int addFilteredCount(int quantity) {
		return counterFiltered.addAndGet(quantity);
	}
	
	@Override
	public int addErrorCount(int quantity) {
		return counterError.addAndGet(quantity);
	}
	
	@Override
	public int addSkippedCount(int quantity) {
		return counterSkipped.addAndGet(quantity);
	}
	
	@Override
	public int getTotalCount() {
		return counterTotal.get();
	}
	
	@Override
	public int getFilteredCount() {
		return counterFiltered.get();
	}
	
	@Override
	public int getErrorCount() {
		return counterError.get();
	}
	
	@Override
	public int getSkippedCount() {
		return counterSkipped.get();
	}

}
