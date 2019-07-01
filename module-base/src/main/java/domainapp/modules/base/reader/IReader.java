/**
 * 
 */
package domainapp.modules.base.reader;

/**
 * Specification for reader
 *  
 * @author Prajapati
 */
public interface IReader<RC extends IReaderContext<RC>, T, C extends IReaderCallback<RC, T>> {

	/**
	 * Read from inputfile and feedback transformed object to given {@link IStatementReaderCallback}
	 * 
	 * @param inputFile
	 * @param config
	 * @param readerCallback
	 */
	void read(RC context, C readerCallback);

}
