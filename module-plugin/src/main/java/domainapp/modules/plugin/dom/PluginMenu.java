/**
 * 
 */
package domainapp.modules.plugin.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.DomainServiceLayout.MenuBar;
import org.apache.isis.applib.annotation.NatureOfService;

import domainapp.modules.plugin.view.dashboard.ManagePluginDashboard;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.VIEW_MENU_ONLY,
		objectType = "plugin.PluginMenu"
)
@DomainServiceLayout(
		menuBar = MenuBar.SECONDARY,
		named = "Plugins",
		menuOrder = "12"
)
public class PluginMenu {
	
	public ManagePluginDashboard managePlugins() {
		return new ManagePluginDashboard();
	}
	
}
