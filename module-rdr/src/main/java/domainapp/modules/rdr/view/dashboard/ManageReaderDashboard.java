/**
 * 
 */
package domainapp.modules.rdr.view.dashboard;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Password;

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.addon.service.AddonService;
import domainapp.modules.addon.service.AddonTypeService;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.rdr.dom.MailConnectionProfile;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.rdr.dom.StatementReaderType;
import domainapp.modules.rdr.service.MailConnectionProfileService;
import domainapp.modules.rdr.service.StatementReaderService;
import domainapp.modules.rdr.service.StatementReaderTypeService;

/**
 * Manage readers and their types
 * 
 * @author jayeshecs
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "rdr.ManageReaderDashboard"
)
public class ManageReaderDashboard {

	public String title() {
		return "Manage Statement Reader";
	}

	@CollectionLayout(defaultView = "table")
	public List<MailConnectionProfile> getMailConnectionProfiles() {
		if (mailConnectionProfileService == null) {
			return Collections.EMPTY_LIST;
		}
		return mailConnectionProfileService.all();
	}

	@CollectionLayout(defaultView = "table")
	public List<StatementReaderType> getStatementReaderTypes() {
		return statementReaderTypeService.all();
	}

	@CollectionLayout(defaultView = "table")
	public List<StatementReader> getStatementReaders() {
		return statementReaderService.all();
	}
	
	@Action(associateWith = "statementReaderTypes", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = StatementReaderType.class)
	@ActionLayout(named = "Delete")
	public ManageReaderDashboard deleteStatementReaderTypes(List<StatementReaderType> statementReaderTypes) {
		statementReaderTypeService.delete(statementReaderTypes);
		return this;
	}
	
	@Action(associateWith = "statementReaders", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = StatementReader.class)
	@ActionLayout(named = "Delete")
	public ManageReaderDashboard deleteStatementReaders(List<StatementReader> statementReaders) {
		statementReaderService.delete(statementReaders);
		return this;
	}
	
	@Action(associateWith = "mailConnectionProfiles", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = MailConnectionProfile.class)
	@ActionLayout(named = "Delete")
	public ManageReaderDashboard deleteMailConnectionProfiles(List<MailConnectionProfile> mailConnectionProfiles) {
		mailConnectionProfileService.delete(mailConnectionProfiles);
		return this;
	}
    
    @Action(
    		domainEvent = StatementReaderType.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = StatementReaderType.class,
    		associateWith = "statementReaderTypes"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new reader type"
    )
    public ManageReaderDashboard createStatementReaderType(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new reader type to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new reader type that will be created")
    		final String description,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Addon", describedAs = "Select addon for new reader type that will be created")
    		final Addon addon
    		) {
    	StatementReaderType statementReaderType = statementReaderTypeService.create(name, description, addon);
    	Objects.requireNonNull(statementReaderType, "Reader type could not be created, check log for more detail");
    	return this;
    }
    
    @SuppressWarnings("unchecked")
    @Programmatic
	public List<Addon> choice2CreateStatementReaderType() {
    	List<AddonType> list = addonTypeService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", StatementReaderType.ADDON_TYPE_NAME);
    	if (list == null || list.isEmpty()) {
    		messageService.raiseError(TranslatableString.tr("Required addon type '${addonType}' not found for statement reader", "addonType", StatementReaderType.ADDON_TYPE_NAME), getClass(), "choice2CreateStatementReaderType");
    		return Collections.EMPTY_LIST;
    	}
    	AddonType addonType = null;
    	for (AddonType type : list) {
    		if (type.getName().equals(StatementReaderType.ADDON_TYPE_NAME)) {
    			addonType = type;
    			break;
    		}
    	}
    	if (addonType == null) {
    		messageService.raiseError(TranslatableString.tr("Required addon type '${addonType}' not found for statement reader", "addonType", StatementReaderType.ADDON_TYPE_NAME), getClass(), "choice2CreateStatementReaderType");
    		return Collections.EMPTY_LIST;
    	}
    	List<Addon> addonList = addonService.search(Addon.QUERY_FIND_BY_ADDON_TYPE, "addonType", addonType);
    	return addonList;
    }
    
    @Action(
    		domainEvent = StatementReader.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = StatementReader.class,
    		associateWith = "statementReaders"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new reader"
    )
    public ManageReaderDashboard createStatementReader(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new reader to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL, maxLength = WithDescription.MAX_LEN)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new reader that will be created")
    		final String description,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Class Name", describedAs = "Enter class name of new reader type that will be created")
    		final StatementReaderType readerType,
    		@Parameter(optionality = Optionality.OPTIONAL, maxLength = StatementReader.PROPERTIES_MAX_LEN)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Properties", multiLine = 4, describedAs = "Enter properties of new reader that will be created")
    		final String properties
    		) {
    	StatementReader statementReader = statementReaderService.create(name, description, readerType, properties);
    	Objects.requireNonNull(statementReader, "Reader could not be created, check log for more detail");
    	return this;
    }

    /**
     * @return default value for 4th parameter of action "Create new reader"
     */
    public String default3CreateStatementReader() {
    	return "#dateFormat=dd/MM/yyyy";
    }
    
    @Action(
    		domainEvent = MailConnectionProfile.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = MailConnectionProfile.class,
    		associateWith = "mailConnectionProfiles",
			associateWithSequence = "1" 
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new mail connection profile"
    )
    public ManageReaderDashboard createMailConnectionProfile(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new reader type to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new reader type that will be created")
    		final String description,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Hostname", describedAs = "Enter hostname of mail server e.g. imap.gmail.com")
    		final String hostname,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Port", describedAs = "Enter port of mail server e.g. 993")
    		final String port,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Username", describedAs = "Enter username of mail account")
    		final String username,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Password", describedAs = "Enter password of mail account")
    		final Password password,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Secure connection", describedAs = "Check this if mail connection is secure")
    		final Boolean secure,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Enable starttls", describedAs = "Check this if mail connection need starttls enabled")
    		final Boolean starttls,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Enable debug logging", describedAs = "Check this to enable mail debug logging")
    		final Boolean debug
    		) {
    	MailConnectionProfile mailConnectionProfile = mailConnectionProfileService.create(name, description, hostname, port, username, password.getPassword(), secure, starttls, debug);
    	Objects.requireNonNull(mailConnectionProfile, "Mail connection profile could not be created, check log for more detail");
    	return this;
    }
    
	@Action(
    		domainEvent = MailConnectionProfile.UpdateEvent.class,
			associateWith = "mailConnectionProfiles", 
			associateWithSequence = "1", 
			semantics = SemanticsOf.SAFE, 
			typeOf = MailConnectionProfile.class)
	@ActionLayout(
			named = "Change Password", 
    		describedAs = "Change password of given mail connection profile",
			position = Position.RIGHT, 
			promptStyle = PromptStyle.DIALOG)
    public ManageReaderDashboard changePassword(
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Mail Connection Profile")
    		MailConnectionProfile mailConnectionProfile,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Password")
    		Password password) {
    	mailConnectionProfileService.changePassword(mailConnectionProfile, password.getPassword());
    	return this;
    }
	
	@Inject
	MailConnectionProfileService mailConnectionProfileService;
	
	@Inject
	StatementReaderTypeService statementReaderTypeService;
	
	@Inject
	StatementReaderService statementReaderService;
    
    @Inject
    MessageService messageService;
    
    @Inject
    AddonService addonService;
    
    @Inject
    AddonTypeService addonTypeService;
}
