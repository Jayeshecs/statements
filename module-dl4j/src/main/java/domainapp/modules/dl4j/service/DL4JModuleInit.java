/**
 * 
 */
package domainapp.modules.dl4j.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.dl4j.datatype.DL4JDataType;

/**
 * @author jayeshecs
 *
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class DL4JModuleInit {

	public DL4JModuleInit() {
		// DO NOTHING
	}
	
	@PostConstruct
	public void init() {
		DL4JDataType.registerDefinitions(serviceRegistry);
	}
	
	@Inject
	ServiceRegistry2 serviceRegistry;
}
