/**
 * 
 */
package domainapp.modules.rdr.service;

import java.util.List;

import javax.mail.Message;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
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
		MailConnectionProfile newMailConnectionProfile = MailConnectionProfile.builder().name(name).description(description).hostname(hostname).port(port).username(username).password(password).secure(secure).starttls(starttls).debug(debug).build();
		MailConnectionProfile mailConnectionProfile = repositoryService.persistAndFlush(newMailConnectionProfile);
    	return mailConnectionProfile;
	}
	
	@Programmatic
	public MailConnection getMailConnection(MailConnectionProfile mailConnectionProfile) {
		MailConnection connection = new MailConnection(mailConnectionProfile);
		return connection;
	}
}
