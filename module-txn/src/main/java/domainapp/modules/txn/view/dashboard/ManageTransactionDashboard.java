/**
 * 
 */
package domainapp.modules.txn.view.dashboard;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

/**
 * @author jayeshecs
 *
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "stmt.ManageTransactionDashboard"
)
public class ManageTransactionDashboard {

	public String title() {
		return "Manage Transactions";
	}
	
	

}
