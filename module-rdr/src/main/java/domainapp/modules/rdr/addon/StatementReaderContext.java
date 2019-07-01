/**
 * 
 */
package domainapp.modules.rdr.addon;

import java.io.File;
import java.util.Properties;

import domainapp.modules.base.reader.AbstractReaderContext;
import domainapp.modules.base.reader.IReaderContext;
import lombok.Builder;

/**
 * Implementation of {@link IReaderContext}<br>
 * Usage is as below:<br>
 * <code>
 * IReaderContext context = StatementReaderContext.builder()<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.id(100L)<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.name("context name")<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.file(new File("data.csv"))<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.build();<br>
 * context<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.set("date", new Date())<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.set("name", "Some string");
 * </code>
 * 
 * @author Prajapati
 */
public class StatementReaderContext extends AbstractReaderContext<IStatementReaderContext> implements IStatementReaderContext {

	private static final String FILE = "file";
	private static final String CONFIG = "config";

	@Builder
	public StatementReaderContext(Long id, String name, File file, Properties config) {
		super(id, name);
		super.set(FILE, file);
		super.set(CONFIG, config);
	}
	
	/**
	 * @return {@link File} file to return
	 */
	public File getFile() {
		return get(FILE);
	}
	
	/**
	 * @return {@link Properties} config to return
	 */
	@Override
	public Properties getConfig() {
		return get(CONFIG);
	}
}
