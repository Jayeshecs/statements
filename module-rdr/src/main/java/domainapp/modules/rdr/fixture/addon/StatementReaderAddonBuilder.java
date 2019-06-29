/**
 * 
 */
package domainapp.modules.rdr.fixture.addon;

import java.util.HashMap;
import java.util.List;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import domainapp.modules.addon.dom.Addon;
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
public class StatementReaderAddonBuilder extends BuilderScriptAbstract<Addon, StatementReaderAddonBuilder> {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String addonTypeName;

    @Getter @Setter
    private String className;

    @Getter
    private Addon object;

	@Override
	protected void execute(ExecutionContext ec) {

        checkParam("name", ec, String.class);
        
        AddonType addonType = null;
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", addonTypeName);
		List<AddonType> list = addonTypeService.filter("name == :name", parameters);
		if (list != null && !parameters.isEmpty()) {
			addonType = list.iterator().next();
		}
		
		if (addonType == null) {
			throw new IllegalArgumentException("Addon type with name '" + addonTypeName + "' could not be found");
		}
        object = addonService.create(name, "", addonType, className, null, true);
	}

    @javax.inject.Inject
    AddonService addonService;

    @javax.inject.Inject
    AddonTypeService addonTypeService;

}
