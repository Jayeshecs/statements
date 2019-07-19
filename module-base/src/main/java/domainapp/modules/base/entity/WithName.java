/**
 * 
 */
package domainapp.modules.base.entity;

import org.apache.isis.applib.annotation.MemberOrder;

/**
 * Specification for Entity with name
 * 
 * @author jayeshecs
 * @see WithName_updateName
 *
 */
public interface WithName {
	
	String FIELD_NAME = "name";
	
	int MAX_LEN = 40;

	/**
	 * @return name to get
	 */
	@MemberOrder(sequence = "1")
	String getName();
	
	/**
	 * @param name to set
	 */
	void setName(String name);

}
