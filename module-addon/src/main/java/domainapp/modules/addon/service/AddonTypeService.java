/**
 * 
 */
package domainapp.modules.addon.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = AddonType.class
)
public class AddonTypeService extends AbstractService<AddonType> {

	public AddonTypeService() {
		super(AddonType.class);
	}
	
	@Programmatic
	public List<AddonType> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public AddonType create(String name, String description) {
		AddonType newPluginType = AddonType.builder().name(name).description(description).build();
		AddonType pluginType = repositoryService.persistAndFlush(newPluginType);
    	return pluginType;
	}
}
