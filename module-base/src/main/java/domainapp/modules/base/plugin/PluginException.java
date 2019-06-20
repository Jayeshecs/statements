/**
 * 
 */
package domainapp.modules.base.plugin;

/**
 * @author Prajapati
 * @see IPlugin
 *
 */
public class PluginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PluginException(String message) {
		super(message);
	}
	
	public PluginException(String message, Exception cause) {
		super(message, cause);
	}

}
