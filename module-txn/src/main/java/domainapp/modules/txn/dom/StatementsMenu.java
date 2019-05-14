/**
 * 
 */
package domainapp.modules.txn.dom;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.DomainServiceLayout.MenuBar;
import org.apache.isis.applib.annotation.NatureOfService;

/**
 * Statements menu bar
 * 
 * @author jayeshecs
 */
@DomainService(nature = NatureOfService.VIEW_MENU_ONLY, objectType = "txn.StatementsMenu")
@DomainServiceLayout(menuBar = MenuBar.PRIMARY, menuOrder = "10", named = "Statements")
public class StatementsMenu {
	
	public List<Transaction> listAll() {
		// TODO
		return new ArrayList<>();
	}

}
