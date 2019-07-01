/**
 * 
 */
package domainapp.modules.rdr.addon;

import java.io.File;
import java.util.Properties;

import domainapp.modules.base.plugin.AddonException;
import domainapp.modules.rdr.api.IStatementReader;
import domainapp.modules.rdr.api.IStatementReaderCallback;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract implementation of {@link IStatementReader}
 * 
 * @author Prajapati
 */
@Slf4j
public abstract class AbstractStatementReader implements IStatementReader {

	@Override
	public void install() throws AddonException {
		// DO NOTHING
	}

	@Override
	public void uninstall() throws AddonException {
		// DO NOTHING
	}
	
	@Override
	public void read(IStatementReaderContext context, IStatementReaderCallback readerCallback) {
		log.info(String.format("Statement reader for %s (%d)", context.getName(), context.getId()));
		File inputFile = context.getFile();
		Properties config = context.getConfig();
		read(context, inputFile, config, readerCallback);
	}
	
	/**
	 * Subclass to provide {@link #read(IStatementReaderContext, File, Properties, IStatementReaderCallback)}
	 * 
	 * @param context {@link IStatementReaderContext}
	 * @param inputFile {@link File}
	 * @param config {@link Properties}
	 * @param callback {@link IStatementReaderCallback}
	 */
	protected abstract void read(IStatementReaderContext context, File inputFile, Properties config, IStatementReaderCallback callback);

}
