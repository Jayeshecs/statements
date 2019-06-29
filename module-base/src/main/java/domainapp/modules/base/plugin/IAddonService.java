/**
 * 
 */
package domainapp.modules.base.plugin;

/**
 * @author Prajapati
 *
 */
public interface IAddonService {

	/**
	 * Purpose of this API is to get instance of IAddonApi which is registered against given addonName
	 * 
	 * @param <T> implementation of IAddonApi
	 * @param addonName name of addon with which IAddonApi is registered
	 * @return instance of IAddonApi corresponding to given addonName<br>Null if not found
	 * @throws AddonException
	 */
	<T extends IAddonApi> T get(String addonName) throws AddonException;
}
