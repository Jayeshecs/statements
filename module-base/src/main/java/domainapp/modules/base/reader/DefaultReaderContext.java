/**
 * 
 */
package domainapp.modules.base.reader;

import lombok.Builder;

/**
 * Implementation of {@link IReaderContext}<br>
 * Usage is as below:<br>
 * <code>
 * IReaderContext context = DefaultReaderContext.builder()<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.id(100L)<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.name("context name")<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.build();<br>
 * context<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.set("date", new Date())<br>
 * 	&nbsp;&nbsp;&nbsp;&nbsp;.set("name", "Some string");
 * </code>
 * 
 * @author Prajapati
 */
public class DefaultReaderContext extends AbstractReaderContext<DefaultReaderContext> {

	@Builder
	public DefaultReaderContext(Long id, String name) {
		super(id, name);
	}

}
