/**
 * 
 */
package domainapp.modules.txn.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.DomainServiceLayout.MenuBar;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;

import domainapp.modules.rdr.view.dashboard.ManageReaderDashboard;
import domainapp.modules.txn.view.dashboard.ManageTransactionDashboard;

/**
 * Statements menu bar
 * 
 * @author jayeshecs
 */
@DomainService(
		nature = NatureOfService.VIEW_MENU_ONLY, 
		objectType = "txn.Statements"
)
@DomainServiceLayout(
		menuBar = MenuBar.PRIMARY, 
		menuOrder = "10", 
		named = "Statements"
)
public class StatementsMenu {
	
	/**
	 * @return
	 */
	@MemberOrder(sequence = "1")
	public ManageTransactionDashboard manageTransaction() {
		return new ManageTransactionDashboard();
	}
	
	/**
	 * @return
	 */
	@MemberOrder(sequence = "3")
	public ManageReaderDashboard manageStatementReader() {
		return new ManageReaderDashboard();
	}

}
