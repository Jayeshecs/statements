/**
 * 
 */
package domainapp.modules.base.entity;

/**
 * Specification for entity that can be enabled/disabled
 * 
 * @author jayeshecs
 */
public interface Activable {

	/**
	 * @return true if this entity is active else false
	 */
	Boolean isActive();
	
	/**
	 * @return true | false
	 * @see #setActive(Boolean)
	 */
	Boolean getActive();
	
	/**
	 * @param active to set
	 */
	void setActive(Boolean active);
}
