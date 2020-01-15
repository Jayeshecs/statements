package domainapp.modules.txn.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.entity.WithNameAndDescription;
import domainapp.modules.rdr.dom.MailConnectionProfile;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.ref.StaticModule.ActionDomainEvent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "statements"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = NamedQueryConstants.QUERY_ALL,
            value = "SELECT "
                    + "FROM domainapp.modules.txn.dom.MailStatementProfile "),
    @javax.jdo.annotations.Query(
            name = NamedQueryConstants.QUERY_FIND_BY_NAME,
            value = "SELECT "
                    + "FROM domainapp.modules.txn.dom.MailStatementProfile "
                    + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="MailStatementProfile_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED,
        objectType = "txn.MailStatementProfile",
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class MailStatementProfile implements Comparable<MailStatementProfile>, WithNameAndDescription {

	@javax.jdo.annotations.Column(allowsNull = "false", length = WithName.MAX_LEN)
    @Title(prepend = "MAIL ")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    @MemberOrder(sequence="1")
    private String name;

    @javax.jdo.annotations.Column(allowsNull = "true", length = WithDescription.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence="2")
    private String description;

    @javax.jdo.annotations.Column(allowsNull = "false", name = "mailConnectionProfileId")
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter @NotNull
    @MemberOrder(sequence="3")
    private MailConnectionProfile mailConnectionProfile;

    @javax.jdo.annotations.Column(allowsNull = "false", name = "sourceId")
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter @NotNull
    @MemberOrder(sequence="4")
    private StatementSource source;

    @javax.jdo.annotations.Column(allowsNull = "false", name = "readerId")
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter @NotNull
    @MemberOrder(sequence="5")
    private StatementReader reader;
    
    @javax.jdo.annotations.Column(allowsNull = "false", length = 255)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter @NotNull
    @MemberOrder(sequence="6")
    private String folderName;
    
    @javax.jdo.annotations.Column(allowsNull = "false", length = 255)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter @NotNull
    @MemberOrder(sequence="7")
    private String subjectWords;
    
    @javax.jdo.annotations.Column(allowsNull = "true", length = 255)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence="8")
    private String fromAddress;
    
    @javax.jdo.annotations.Column(allowsNull = "true", length = 128)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence="9")
    private String fileNamePattern;
    
    @Builder
    public MailStatementProfile(final String name, final String description, final MailConnectionProfile mailConnectionProfile, final StatementSource source, final StatementReader reader, final String folderName, final String subjectWords, final String fromAddress, final String fileNamePattern) {
    	setName(name);
    	setDescription(description);
    	setMailConnectionProfile(mailConnectionProfile);
    	setSource(source);
    	setReader(reader);
    	setFolderName(folderName);
    	setSubjectWords(subjectWords);
    	setFromAddress(fromAddress);
    	setFileNamePattern(fileNamePattern);
    }

	public static class CreateEvent extends ActionDomainEvent<MailStatementProfile> {
		private static final long serialVersionUID = 1L;
    }
    
    public static class UpdateEvent extends ActionDomainEvent<MailStatementProfile> {
		private static final long serialVersionUID = 1L;
    }

    @Override
    public int compareTo(final MailStatementProfile other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName());
    }

}