/**
 * 
 */
package domainapp.modules.txn.service;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.ref.dom.StatementSourceType;
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
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public StatementSource create(String name, String description, StatementSourceType type) {
		StatementSource newStatementSource = StatementSource.builder().name(name).description(description).type(type).build();
		StatementSource statementSource = repositoryService.persistAndFlush(newStatementSource);
    	return statementSource;
	}

	@Programmatic
	public StatementSource getOrCreate(String sourceName, StatementSourceType type) {
		List<StatementSource> statementSourceList = search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", sourceName);
		if (statementSourceList == null || statementSourceList.isEmpty()) {
			StatementSource statementSource = create(sourceName, "", type);
			statementSourceList = Arrays.asList(statementSource);
		}
		if (statementSourceList.size() == 1) {
			return statementSourceList.iterator().next();
		}
		for (StatementSource statementSource : statementSourceList) {
			if (statementSource.getName().equals(sourceName)) {
				return statementSource;
			}
		}
		return create(sourceName, "", type); // this means there are no statement source with exact same name, so create it
	}
}
