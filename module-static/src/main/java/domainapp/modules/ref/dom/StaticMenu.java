/**
 * 
 */
package domainapp.modules.ref.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.DomainServiceLayout.MenuBar;
import org.apache.isis.applib.annotation.NatureOfService;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.VIEW_MENU_ONLY,
		objectType = "ref.StaticMenu"
)
@DomainServiceLayout(
		menuBar = MenuBar.PRIMARY,
		named = "Static Data",
		menuOrder = "11"
)
public class StaticMenu {

	public List<Category> categories() {
		// TODO
		return new ArrayList<>();
	};

	public List<SubCategory> subCategories() {
		// TODO
		return new ArrayList<>();
	};
	
	public List<TransactionType> transactionTypes() {
		return Arrays.asList(TransactionType.values());
	};
	
	public List<StatementSourceType> statementSourceTypes() {
		return Arrays.asList(StatementSourceType.values());
	};
	
}
