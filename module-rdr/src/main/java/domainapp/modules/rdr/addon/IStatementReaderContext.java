/**
 * 
 */
package domainapp.modules.rdr.addon;

import java.io.File;
import java.util.Properties;

import domainapp.modules.base.reader.IReaderContext;

/**
 * Specification for reader context for statement reader
 * 
 * @author Prajapati
 */
public interface IStatementReaderContext extends IReaderContext<IStatementReaderContext> {

	String PARAM_STATEMENT_SOURCE = "statementSource";
	
	/**
	 * @return {@link File} input file associated with this {@link IStatementReaderContext}
	 */
	File getFile();

	/**
	 * @return configuration associated with this {@link IStatementReaderContext}
	 */
	Properties getConfig();
}
