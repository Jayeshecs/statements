/**
 * 
 */
package domainapp.modules.rdr.fixture;

import java.util.List;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.service.AddonService;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.rdr.dom.StatementReaderType;
import domainapp.modules.rdr.service.StatementReaderTypeService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Builder script implementation for Category
 * 
 * @author Prajapati
 */
@Accessors(chain = true)
public class StatementReaderTypeBuilder extends BuilderScriptAbstract<StatementReaderType, StatementReaderTypeBuilder> {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String addonName;

    @Getter
    private StatementReaderType object;

	@Override
	protected void execute(ExecutionContext ec) {

        checkParam("name", ec, String.class);

        checkParam("addonName", ec, String.class);

        List<Addon> list = addonService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", addonName);
        Addon addon = null;
        if (list != null && !list.isEmpty()) {
        	for (Addon addon2 : list) {
        		if (addon2.getName().equals(addonName)) {
        			addon = addon2;
        		}
        	}
        }
        if (addon == null) {
        	throw new IllegalArgumentException("Addon with name " + addonName + " could not be found");
        }
        object = statementReaderTypeService.create(name, "", addon);
	}

    @javax.inject.Inject
    AddonService addonService;

    @javax.inject.Inject
    StatementReaderTypeService statementReaderTypeService;

}
