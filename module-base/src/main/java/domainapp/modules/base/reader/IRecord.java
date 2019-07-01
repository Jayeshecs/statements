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
	 * @return 
	 */
	<T> T get(F field);
	
	<T> IRecord<F> set(F field, T value);

}
