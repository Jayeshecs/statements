/**
 * 
 */
package domainapp.modules.rdr.api;

import java.util.Collection;

/**
 * Specification for reader callback where a collection of records <T> are made
 * available to {@link #process(Collection)} API
 * 
 * @author jayeshecs
 *
 */
public interface IReaderCallback<T> {

	/**
	 * Process given records
	 * 
	 * @param records
	 */
	void process(Collection<T> records);

}
