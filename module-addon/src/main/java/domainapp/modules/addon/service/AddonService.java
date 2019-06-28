/**
 * 
 */
package domainapp.modules.addon.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = Addon.class
)
public class AddonService extends AbstractService<Addon> {

	public AddonService() {
		super(Addon.class);
	}
	
	@Programmatic
	public List<Addon> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public Addon create(String name, String description, AddonType addonType, String className, String library) {
		Addon newPlugin = Addon.builder().name(name).description(description).addonType(addonType).className(className).library(library).build();
		Addon plugin = repositoryService.persistAndFlush(newPlugin);
    	return plugin;
	}
}
