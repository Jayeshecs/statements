/**
 * 
 */
package domainapp.modules.base;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;

/**
 * @author Prajapati
 *
 */
public interface IEntityEnum {
	
	/**
	 * @return identifier of given entity enum
	 */
	
	@Property(editing = Editing.DISABLED, editingDisabledReason = "Cannot edit in-memory entities")
	@PropertyLayout(named = "ID")
	int getId();
	
	/**
	 * @return name of this entity enum
	 */
	
	@Property(editing = Editing.DISABLED, editingDisabledReason = "Cannot edit in-memory entities")
	@PropertyLayout(named = "Name")
	String getName();

}
