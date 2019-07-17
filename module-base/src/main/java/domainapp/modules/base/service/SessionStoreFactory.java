/**
 * 
 */
package domainapp.modules.base.service;

/**
 * @author Prajapati
 *
 */
public class SessionStoreFactory {
	
	public static final SessionStoreFactory INSTANCE = new SessionStoreFactory();
	
	private ISessionStore sessionStore;
	
	private SessionStoreFactory() {
		// DO NOTHING
	}
	
	public void registerSessionStore(ISessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}
	
	public ISessionStore getSessionStore() {
		return this.sessionStore;
	}

}
