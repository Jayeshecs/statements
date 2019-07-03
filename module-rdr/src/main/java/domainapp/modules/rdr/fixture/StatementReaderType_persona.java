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
import domainapp.modules.rdr.dom.StatementReaderType;
import domainapp.modules.rdr.fixture.addon.StatementReaderAddon_persona;
import domainapp.modules.rdr.service.StatementReaderTypeService;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fixture data for {@link StatementReaderType}
 * 
 * @author Prajapati
 */
@AllArgsConstructor
public enum StatementReaderType_persona implements PersonaWithBuilderScript<StatementReaderType, StatementReaderTypeBuilder>, PersonaWithFinder<StatementReaderType> {
	
	HDFC_BANK("HDFC Bank Account", StatementReaderAddon_persona.HDFC_BANK_ACCOUNT.getName()),
	HDFC_CREDITCARD("HDFC Creditcard", StatementReaderAddon_persona.HDFC_CREDITCARD.getName()),
	KOTAK_BANK("KOTAK Bank Account", StatementReaderAddon_persona.KOTAK_BANK_ACCOUNT.getName()),
	PAYTM_WALLET("PayTM Wallet", StatementReaderAddon_persona.PAYTM_WALLET.getName())
	;

	@Getter
	private final String name;

	private final String addonName;

	public StatementReaderTypeBuilder builder() {
	    return new StatementReaderTypeBuilder().setName(name).setAddonName(addonName);
	}

	@Override
	public StatementReaderType findUsing(ServiceRegistry2 serviceRegistry) {
		StatementReaderTypeService statementReaderTypeService = serviceRegistry.lookupService(StatementReaderTypeService.class);
		List<StatementReaderType> list = statementReaderTypeService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		return list == null || list.isEmpty() ? null : list.iterator().next();
	}
	
	public static class PersistAll
	        extends PersonaEnumPersistAll<StatementReaderType_persona, StatementReaderType, StatementReaderTypeBuilder> {
	    public PersistAll() {
	        super(StatementReaderType_persona.class);
	    }
	}
}
