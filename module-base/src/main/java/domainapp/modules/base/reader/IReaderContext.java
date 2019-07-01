/**
 * 
 */
package domainapp.modules.base.reader;

/**
 * Specification for reader context
 * 
 * @author Prajapati
 */
public interface IReaderContext<RC extends IReaderContext<RC>> {
	
	/**
	 * @return ID associated with this reader context
	 */
	Long getId();
	
	/**
	 * @return name of this reader context
	 */
	String getName();

	/**
	 * @param key
	 * @return value <T> for given key else null
	 */
	<T> T get(String key);
	
	/**
	 * @param key
	 * @param value <T> to set for given key
	 */
	<T> RC set(String key, T value);
}
