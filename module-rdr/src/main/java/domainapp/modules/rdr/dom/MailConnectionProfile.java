package domainapp.modules.rdr.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
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

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.entity.WithNameAndDescription;
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
                    + "FROM domainapp.modules.rdr.dom.MailConnectionProfile "),
        @javax.jdo.annotations.Query(
                name = NamedQueryConstants.QUERY_FIND_BY_NAME,
                value = "SELECT "
                        + "FROM domainapp.modules.rdr.dom.MailConnectionProfile "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="MailConnectionProfile_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED,
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class MailConnectionProfile implements Comparable<MailConnectionProfile>, WithNameAndDescription {

	@javax.jdo.annotations.Column(allowsNull = "false", length = WithName.MAX_LEN)
    @Title(prepend = "MAIL ")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String name;

    @javax.jdo.annotations.Column(allowsNull = "true", length = WithDescription.MAX_LEN)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    private String description;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 128)
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String hostname;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 10)
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String port;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 128)
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private String username;

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private String password;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "7")
    private Boolean secure;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "8")
    private Boolean starttls;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "9")
    private Boolean debug;
    
    @Builder
    public MailConnectionProfile(final String name, final String description, final String hostname, final String port, final String username, final String password, final Boolean secure, final Boolean starttls, final Boolean debug) {
    	setName(name);
    	setDescription(description);
    	setHostname(hostname);
    	setPort(port);
    	setUsername(username);
    	setPassword(password);
    	setSecure(secure);
    	setStarttls(starttls);
    	setDebug(debug);
    }
    
    public static class CreateEvent extends ActionDomainEvent<MailConnectionProfile> {
		private static final long serialVersionUID = 1L;
    }

    @Override
    public int compareTo(final MailConnectionProfile other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName());
    }

}