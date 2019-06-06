/**
 * 
 */
package domainapp.modules.base.service;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.Activable;

/**
 * @author jayeshecs
 *
 */
public abstract class AbstractActivableService<T extends Activable> extends AbstractService<T> {

	public static final String QUERY_ALL_ACTIVE = "allActive";
	
	protected AbstractActivableService(Class<T> clazz) {
		super(clazz);
	}
	
	
	@Programmatic
	public List<T> allActive() {
		return search(AbstractActivableService.QUERY_ALL_ACTIVE);
	}


}
