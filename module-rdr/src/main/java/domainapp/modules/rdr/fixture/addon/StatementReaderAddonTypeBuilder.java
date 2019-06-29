/**
 * 
 */
package domainapp.modules.rdr.fixture.addon;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.addon.service.AddonService;
import domainapp.modules.addon.service.AddonTypeService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Builder script implementation for Category
 * 
 * @author Prajapati
 */
@Accessors(chain = true)
public class StatementReaderAddonTypeBuilder extends BuilderScriptAbstract<AddonType, StatementReaderAddonTypeBuilder> {

    @Getter @Setter
    private String name;

    @Getter
    private AddonType object;

	@Override
	protected void execute(ExecutionContext ec) {

        checkParam("name", ec, String.class);

        object = addonTypeService.create(name, "");
	}

    @javax.inject.Inject
    AddonService addonService;

    @javax.inject.Inject
    AddonTypeService addonTypeService;

}
