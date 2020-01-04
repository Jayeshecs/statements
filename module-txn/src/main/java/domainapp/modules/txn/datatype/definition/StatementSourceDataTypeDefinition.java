package domainapp.modules.txn.datatype.definition;

import javax.inject.Inject;

import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.base.datatype.definition.WithNameDataTypeDefinition;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.service.StatementSourceService;

/**
 * {@link IDataTypeDefinition} implementation for {@link StatementSource}
 * 
 * @author jayeshecs
 */
public class StatementSourceDataTypeDefinition extends WithNameDataTypeDefinition<StatementSource> {

	@Override
	protected AbstractService<StatementSource> getService() {
		return statementSourceService;
	}
	
	@Inject
	StatementSourceService statementSourceService;

}
