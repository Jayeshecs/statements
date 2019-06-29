/**
 * 
 */
package domainapp.modules.rdr.fixture.addon;

import java.util.List;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.addon.service.AddonTypeService;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.rdr.dom.StatementReaderType;
import lombok.AllArgsConstructor;

/**
 * @author Prajapati
 *
 */
@AllArgsConstructor
public enum StatementReaderAddonType_persona implements PersonaWithBuilderScript<AddonType, StatementReaderAddonTypeBuilder>, PersonaWithFinder<AddonType> {
	
	STATEMENT_READER(StatementReaderType.ADDON_TYPE_NAME)
	;

	private final String name;

	public StatementReaderAddonTypeBuilder builder() {
	    return new StatementReaderAddonTypeBuilder().setName(name);
	}

	@Override
	public AddonType findUsing(ServiceRegistry2 serviceRegistry) {
		AddonTypeService statementReaderTypeService = serviceRegistry.lookupService(AddonTypeService.class);
		List<AddonType> list = statementReaderTypeService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		return list == null || list.isEmpty() ? null : list.iterator().next();
	}
	
	public static class PersistAll
	        extends PersonaEnumPersistAll<StatementReaderAddonType_persona, AddonType, StatementReaderAddonTypeBuilder> {
	    public PersistAll() {
	        super(StatementReaderAddonType_persona.class);
	    }
	}
}
