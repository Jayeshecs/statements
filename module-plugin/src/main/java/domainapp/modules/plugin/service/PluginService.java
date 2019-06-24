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
import domainapp.modules.plugin.dom.Plugin;
import domainapp.modules.plugin.dom.PluginType;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = Plugin.class
)
public class PluginService extends AbstractService<Plugin> {

	public PluginService() {
		super(Plugin.class);
	}
	
	@Programmatic
	public List<Plugin> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public Plugin create(String name, String description, PluginType pluginType, String className, String library) {
		Plugin newPlugin = Plugin.builder().name(name).description(description).pluginType(pluginType).className(className).library(library).build();
		Plugin plugin = repositoryService.persistAndFlush(newPlugin);
    	return plugin;
	}
}
