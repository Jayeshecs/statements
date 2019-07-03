/**
 * 
 */
package domainapp.modules.base.reader;

/**
 * Specification for record to be used by reader framework.
 * 
 * @author Prajapati
 * @param <F> enum template for fields of record
 */
public interface IRecord<F extends Enum<?>> {
	
	/**
	 * @param <T>
	 * @param field
	 * @return value <T> to return for given field <F> 
	 */
	<T> T get(F field);
	
	/**
	 * @param <T>
	 * @param field
	 * @param value
	 * @return value <T> to set for given field <F>
	 */
	<T> IRecord<F> set(F field, T value);
	
	/**
	 * @return true if marked as filtered
	 */
	Boolean isFiltered();
	
	/**
	 * Mark this record as filtered
	 */
	void markAsFiltered();

}
