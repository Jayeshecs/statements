/**
 * 
 */
package domainapp.modules.base.entity;

/**
 * This interface is to host commonly used named query constants
 * 
 * @author jayeshecs
 */
public interface NamedQueryConstants {

	/**
	 * Named query constant for getting all records
	 */
	String QUERY_ALL = "all"; //$NON-NLS-1$
	
	/**
	 * Named query constant for getting all records that matches given name pattern
	 */
	String QUERY_FIND_BY_NAME = "findByName"; //$NON-NLS-1$
	
	/**
	 * Named query constant for getting all active or enabled records
	 */
	String QUERY_ALL_ACTIVE = "allActive"; //$NON-NLS-1$
	
}
