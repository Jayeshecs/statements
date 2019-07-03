/**
 * 
 */
package domainapp.modules.rdr.fixture.addon;

import java.util.List;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.service.AddonService;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.plugin.IAddonApi;
import domainapp.modules.rdr.addon.hdfc.HDFCBankAccountStatementReader;
import domainapp.modules.rdr.addon.hdfc.HDFCCreditcardStatementReader;
import domainapp.modules.rdr.addon.kotak.KOTAKBankAccountStatementReader;
import domainapp.modules.rdr.addon.paytm.PayTMWalletStatementReader;
import domainapp.modules.rdr.dom.StatementReaderType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Prajapati
 *
 */
@AllArgsConstructor
public enum StatementReaderAddon_persona implements PersonaWithBuilderScript<Addon, StatementReaderAddonBuilder>, PersonaWithFinder<Addon> {
	
	HDFC_BANK_ACCOUNT("HDFC Bank Account reader", StatementReaderType.ADDON_TYPE_NAME, HDFCBankAccountStatementReader.class),
	HDFC_CREDITCARD("HDFC Creditcard reader", StatementReaderType.ADDON_TYPE_NAME, HDFCCreditcardStatementReader.class),
	KOTAK_BANK_ACCOUNT("KOTAK Bank Account reader", StatementReaderType.ADDON_TYPE_NAME, KOTAKBankAccountStatementReader.class),
	PAYTM_WALLET("PayTM Wallet reader", StatementReaderType.ADDON_TYPE_NAME, PayTMWalletStatementReader.class)
	;

	@Getter
	private final String name;

	private final String addonTypeName;

	private final Class<? extends IAddonApi> addonClass;

	public StatementReaderAddonBuilder builder() {
	    return new StatementReaderAddonBuilder().setName(name).setAddonTypeName(addonTypeName).setClassName(addonClass.getName());
	}

	@Override
	public Addon findUsing(ServiceRegistry2 serviceRegistry) {
		AddonService addonService = serviceRegistry.lookupService(AddonService.class);
		List<Addon> list = addonService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		return list == null || list.isEmpty() ? null : list.iterator().next();
	}
	
	public static class PersistAll
	        extends PersonaEnumPersistAll<StatementReaderAddon_persona, Addon, StatementReaderAddonBuilder> {
	    public PersistAll() {
	        super(StatementReaderAddon_persona.class);
	    }
	}
}
