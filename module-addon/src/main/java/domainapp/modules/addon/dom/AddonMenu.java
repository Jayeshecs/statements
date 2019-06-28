/**
 * 
 */
package domainapp.modules.addon.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.DomainServiceLayout.MenuBar;

import domainapp.modules.addon.view.dashboard.ManageAddonDashboard;

import org.apache.isis.applib.annotation.NatureOfService;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.VIEW_MENU_ONLY,
		objectType = "addon.AddonMenu"
)
@DomainServiceLayout(
		menuBar = MenuBar.SECONDARY,
		named = "Addons",
		menuOrder = "12"
)
public class AddonMenu {
	
	public ManageAddonDashboard manageAddon() {
		return new ManageAddonDashboard();
	}
	
}
