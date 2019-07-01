/**
 * 
 */
package domainapp.modules.base.reader;

import java.util.Collection;

/**
 * @author Prajapati
 *
 */
public interface IReaderCallback<RC extends IReaderContext<RC>, T> {

	/**
	 * Process given records
	 * 
	 * @param records
	 */
	void process(RC context, Collection<T> records);

}
