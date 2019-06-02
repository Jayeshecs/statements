/**
 * 
 */
package domainapp.modules.rdr.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.service.AbstractService;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.rdr.dom.StatementReaderType;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = StatementReader.class
)
public class StatementReaderService extends AbstractService<StatementReader> {

	public StatementReaderService() {
		super(StatementReader.class);
	}

	
	@Programmatic
	public List<StatementReader> all() {
		return search(StatementReader.QUERY_ALL);
	}

	@Programmatic
	public StatementReader create(String name, String description, StatementReaderType readerType) {
		StatementReader newStatementReader = StatementReader.builder().name(name).description(description).readerType(readerType).build();
		StatementReader statementReader = repositoryService.persistAndFlush(newStatementReader);
    	return statementReader;
	}
}
