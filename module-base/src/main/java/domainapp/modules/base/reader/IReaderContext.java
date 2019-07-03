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
	
	/**
	 * Below APIs for tracking counters
	 */
	
	/**
	 * Increment total record counts by given quantity. Please note that if quantity is negative then it will decrement the total count.
	 * 
	 * @param quantity
	 * @return
	 */
	int addTotalCount(int quantity);
	
	
	/**
	 * Increment Filtered record counts by given quantity. Please note that if quantity is negative then it will decrement the Filtered count.
	 * 
	 * @param quantity
	 * @return
	 */
	int addFilteredCount(int quantity);
	
	/**
	 * Increment Error record counts by given quantity. Please note that if quantity is negative then it will decrement the Error count.
	 * 
	 * @param quantity
	 * @return
	 */
	int addErrorCount(int quantity);
	
	/**
	 * Increment Skipped record counts by given quantity. Please note that if quantity is negative then it will decrement the Skipped count.
	 * 
	 * @param quantity
	 * @return
	 */
	int addSkippedCount(int quantity);
	
	/**
	 * @return total record count
	 */
	int getTotalCount();
	
	/**
	 * @return filtered record count
	 */
	int getFilteredCount();
	
	/**
	 * @return error record count
	 */
	int getErrorCount();
	
	/**
	 * @return skipped record count
	 */
	int getSkippedCount();
}
