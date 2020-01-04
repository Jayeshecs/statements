/**
 * 
 */
package domainapp.modules.txn.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.txn.datatype.TransactionDataType;

/**
 * @author jayeshecs
 *
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class TransactionModuleInit {

	public TransactionModuleInit() {
		// DO NOTHING
	}
	
	@PostConstruct
	public void init() {
		TransactionDataType.registerDefinitions(serviceRegistry);
	}
	
	@Inject
	ServiceRegistry2 serviceRegistry;
}
