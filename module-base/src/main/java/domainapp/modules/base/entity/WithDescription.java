/**
 * 
 */
package domainapp.modules.base.entity;

import org.apache.isis.applib.annotation.MemberOrder;
import org.isisaddons.wicket.summernote.cpt.applib.SummernoteEditor;

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
	@SummernoteEditor(height = 100, maxHeight = 300)
	String getDescription();
	
	/**
	 * @param description to set
	 */
	void setDescription(String description);
}
