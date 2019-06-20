/**
 * 
 */
package domainapp.modules.base.plugin;

/**
 * Specification for plugins
 * 
 * @author Prajapati
 */
public interface IPlugin {

	/**
	 * install this plugin
	 */
	void install() throws PluginException;
	
	/**
	 * remove this plugin
	 */
	void uninstall() throws PluginException;
}
