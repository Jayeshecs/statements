/**
 * 
 */
package domainapp.modules.rdr.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.rdr.dom.StatementReaderType;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = StatementReaderType.class
)
public class StatementReaderTypeService extends AbstractService<StatementReaderType> {

	public StatementReaderTypeService() {
		super(StatementReaderType.class);
	}

	
	@Programmatic
	public List<StatementReaderType> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public StatementReaderType create(String name, String description, String className) {
		StatementReaderType newStatementReaderType = StatementReaderType.builder().name(name).description(description).className(className).build();
		StatementReaderType statementReaderType = repositoryService.persistAndFlush(newStatementReaderType);
    	return statementReaderType;
	}
}
