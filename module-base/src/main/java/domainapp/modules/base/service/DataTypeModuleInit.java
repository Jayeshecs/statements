/**
 * 
 */
package domainapp.modules.base.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.datatype.DataType;

/**
 * @author jayeshecs
 *
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class DataTypeModuleInit {

	public DataTypeModuleInit() {
		// DO NOTHING
	}
	
	@PostConstruct
	public void init() {
		DataType.registerDefinitions(serviceRegistry);
	}
	
	@Inject
	ServiceRegistry2 serviceRegistry;
}
