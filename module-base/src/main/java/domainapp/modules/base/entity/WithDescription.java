/**
 * 
 */
package domainapp.modules.base.entity;

import org.apache.isis.applib.annotation.MemberOrder;

/**
 * Specification for entity with description
 * 
 * @author jayeshecs
 */
public interface WithDescription {

	int MAX_LEN = 4000;

	/**
	 * @return description to get
	 */
	@MemberOrder(sequence = "2")
	String getDescription();
	
	/**
	 * @param description to set
	 */
	void setDescription(String description);
}
