/**
 * 
 */
package domainapp.modules.rdr.api;

import domainapp.modules.base.plugin.IAddonApi;
import domainapp.modules.base.reader.IReader;
import domainapp.modules.rdr.addon.IStatementReaderContext;

/**
 * Specification for statement reader
 * 
 * @author jayeshecs
 * @see IStatementReaderCallback
 */
public interface IStatementReader extends IAddonApi, IReader<IStatementReaderContext, IStatementRecord, IStatementReaderCallback> {

}
