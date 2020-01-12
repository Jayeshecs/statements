/**
 * 
 */
package domainapp.modules.txn.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.rdr.dom.MailConnectionProfile;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.txn.dom.MailStatementProfile;
import domainapp.modules.txn.dom.StatementSource;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = MailStatementProfile.class
)
public class MailStatementProfileService extends AbstractService<MailStatementProfile>{

	public MailStatementProfileService() {
		super(MailStatementProfile.class);
	}
	
	@Programmatic
	public List<MailStatementProfile> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public MailStatementProfile create(String name, String description, MailConnectionProfile mailConnectionProfile, StatementSource source, StatementReader reader, String folderName, String subjectWords, String fromAddress, String fileNamePattern) {
		MailStatementProfile newMailStatementProfile = MailStatementProfile.builder()
				.name(name).description(description)
				.mailConnectionProfile(mailConnectionProfile)
				.source(source).reader(reader)
				.folderName(folderName).subjectWords(subjectWords).fromAddress(fromAddress)
				.fileNamePattern(fileNamePattern)
				.build();
		MailStatementProfile mailStatementProfile = repositoryService.persistAndFlush(newMailStatementProfile);
    	return mailStatementProfile;
	}
}
