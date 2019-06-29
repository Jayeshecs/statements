/**
 * 
 */
package domainapp.modules.rdr.fixture;

import java.util.List;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.rdr.service.StatementReaderService;
import lombok.AllArgsConstructor;

/**
 * @author Prajapati
 *
 */
@AllArgsConstructor
public enum StatementReader_persona implements PersonaWithBuilderScript<StatementReader, StatementReaderBuilder>, PersonaWithFinder<StatementReader> {
	
	HDFC_BANK("HDFC Bank Account", StatementReaderType_persona.HDFC_BANK.getName()),
	HDFC_CREDITCARD("HDFC Creditcard", StatementReaderType_persona.HDFC_CREDITCARD.getName())
	;

	private final String name;

	private final String readerType;

	public StatementReaderBuilder builder() {
	    return new StatementReaderBuilder().setName(name).setReaderType(readerType);
	}

	@Override
	public StatementReader findUsing(ServiceRegistry2 serviceRegistry) {
		StatementReaderService statementReaderService = serviceRegistry.lookupService(StatementReaderService.class);
		List<StatementReader> list = statementReaderService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		return list == null || list.isEmpty() ? null : list.iterator().next();
	}
	
	public static class PersistAll
	        extends PersonaEnumPersistAll<StatementReader_persona, StatementReader, StatementReaderBuilder> {
	    public PersistAll() {
	        super(StatementReader_persona.class);
	    }
	}
}
