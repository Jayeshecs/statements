/**
 * 
 */
package domainapp.modules.base.service;

/**
 * Specification for session store
 * 
 * @author Prajapati
 */
public interface ISessionStore {

	/**
	 * @param key
	 * @return
	 */
	String get(String key);
	
	/**
	 * @param key
	 * @param data
	 */
	void set(String key, String data);
}
