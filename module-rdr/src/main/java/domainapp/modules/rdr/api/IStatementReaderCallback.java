/**
 * 
 */
package domainapp.modules.rdr.api;

import java.util.Collection;

import domainapp.modules.base.reader.IReaderCallback;
import domainapp.modules.rdr.addon.IStatementReaderContext;

/**
 * Specification for reader callback where a collection of records <T> are made
 * available to {@link #process(Collection)} API
 * 
 * @author jayeshecs
 *
 */
public interface IStatementReaderCallback extends IReaderCallback<IStatementReaderContext, IStatementRecord> {

}
