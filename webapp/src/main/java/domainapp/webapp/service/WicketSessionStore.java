/**
 * 
 */
package domainapp.webapp.service;

import java.io.Serializable;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;

import domainapp.modules.base.service.ISessionStore;

/**
 * Implementation of {@link ISessionStore} for Wicket application
 * 
 * @author Prajapati
 */
public class WicketSessionStore implements ISessionStore {

	@Override
	public String get(String key) {
		AuthenticatedWebSession authenticatedWebSession = AuthenticatedWebSession.get();
		if (authenticatedWebSession != null) {
			Serializable value = authenticatedWebSession.getAttribute(key);
			if (value != null) {
				return String.valueOf(value);
			}
		}
		return null;
	}

	@Override
	public void set(String key, String data) {
		AuthenticatedWebSession authenticatedWebSession = AuthenticatedWebSession.get();
		if (authenticatedWebSession != null) {
			authenticatedWebSession.setAttribute(key, data);
		}
	}

}
