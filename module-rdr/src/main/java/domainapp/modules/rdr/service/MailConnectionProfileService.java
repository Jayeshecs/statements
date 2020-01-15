/**
 * 
 */
package domainapp.modules.rdr.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.base.service.EncryptionService;
import domainapp.modules.rdr.dom.MailConnectionProfile;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = MailConnectionProfile.class
)
public class MailConnectionProfileService extends AbstractService<MailConnectionProfile> {

	public MailConnectionProfileService() {
		super(MailConnectionProfile.class);
	}
	
	@Programmatic
	public List<MailConnectionProfile> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public MailConnectionProfile create(String name, String description, String hostname, String port, String username, String password, Boolean secure, Boolean starttls, Boolean debug) {
		password = encryptionService.encrypt(password);
		MailConnectionProfile newMailConnectionProfile = MailConnectionProfile.builder().name(name).description(description).hostname(hostname).port(port).username(username).password(password).secure(secure).starttls(starttls).debug(debug).build();
		MailConnectionProfile mailConnectionProfile = repositoryService.persistAndFlush(newMailConnectionProfile);
    	return mailConnectionProfile;
	}
	
	@Programmatic
	public void changePassword(MailConnectionProfile mailConnectionProfile, String password) {
		password = encryptionService.encrypt(password);
		List<MailConnectionProfile> list = search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", mailConnectionProfile.getName());
		MailConnectionProfile mailConnectionProfilePersisted = null;
		if (list != null && !list.isEmpty()) {
			Optional<MailConnectionProfile> first = list.stream().filter(mcp -> {
				return (mcp.getName().equals(mailConnectionProfile.getName()));
			}).findFirst();
			if (first.isPresent()) {
				mailConnectionProfilePersisted = first.get();
			}
		}
		if (mailConnectionProfilePersisted == null) {
			throw new IllegalArgumentException("No mail connection profile found with name - " + mailConnectionProfile.getName());
		}
		mailConnectionProfilePersisted.setPassword(password);
		save(Arrays.asList(mailConnectionProfilePersisted));
	}
	
	@Programmatic
	public MailConnection getMailConnection(MailConnectionProfile mailConnectionProfile) {
		MailConnectionProfile mailConnectionProfileCopy = new MailConnectionProfile(mailConnectionProfile);
		mailConnectionProfileCopy.setPassword(encryptionService.decrypt(mailConnectionProfile.getPassword()));
		MailConnection connection = new MailConnection(mailConnectionProfileCopy);
		return connection;
	}
	
	@Inject
	EncryptionService encryptionService;
}
