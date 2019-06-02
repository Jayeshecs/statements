/**
 * 
 */
package domainapp.modules.txn.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.service.AbstractService;
import domainapp.modules.txn.dom.StatementSource;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = StatementSource.class
)
public class StatementSourceService extends AbstractService<StatementSource>{

	public StatementSourceService() {
		super(StatementSource.class);
	}
	
	@Programmatic
	public List<StatementSource> all() {
		return search(StatementSource.QUERY_ALL);
	}

	@Programmatic
	public StatementSource create(String name, String description) {
		StatementSource newStatementSource = StatementSource.builder().name(name).description(description).build();
		StatementSource statementSource = repositoryService.persistAndFlush(newStatementSource);
    	return statementSource;
	}
}
