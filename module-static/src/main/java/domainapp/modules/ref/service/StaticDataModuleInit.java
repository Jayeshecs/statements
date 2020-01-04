/**
 * 
 */
package domainapp.modules.ref.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.ref.datatype.StaticDataDataType;

/**
 * @author jayeshecs
 *
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class StaticDataModuleInit {

	public StaticDataModuleInit() {
		// DO NOTHING
	}
	
	@PostConstruct
	public void init() {
		StaticDataDataType.registerDefinitions(serviceRegistry);
	}
	
	@Inject
	ServiceRegistry2 serviceRegistry;
}
