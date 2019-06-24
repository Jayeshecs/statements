/**
 * 
 */
package domainapp.modules.plugin.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.plugin.dom.PluginType;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = PluginType.class
)
public class PluginTypeService extends AbstractService<PluginType> {

	public PluginTypeService() {
		super(PluginType.class);
	}
	
	@Programmatic
	public List<PluginType> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public PluginType create(String name, String description) {
		PluginType newPluginType = PluginType.builder().name(name).description(description).build();
		PluginType pluginType = repositoryService.persistAndFlush(newPluginType);
    	return pluginType;
	}
}
