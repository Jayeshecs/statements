/**
 * 
 */
package domainapp.modules.base.plugin;

/**
 * Specification for plugins
 * 
 * @author Prajapati
 */
public interface IAddonApi {

	/**
	 * install this plugin
	 */
	void install() throws AddonException;
	
	/**
	 * remove this plugin
	 */
	void uninstall() throws AddonException;
}
