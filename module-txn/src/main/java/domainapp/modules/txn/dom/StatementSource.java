package domainapp.modules.txn.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.entity.WithNameAndDescription;
import domainapp.modules.ref.StaticModule.ActionDomainEvent;
import domainapp.modules.ref.dom.StatementSourceType;
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
                    + "FROM domainapp.modules.txn.dom.StatementSource "),
        @javax.jdo.annotations.Query(
                name = NamedQueryConstants.QUERY_FIND_BY_NAME,
                value = "SELECT "
                        + "FROM domainapp.modules.txn.dom.StatementSource "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="StatementSource_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED,
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class StatementSource implements Comparable<StatementSource>, WithNameAndDescription {
    
    public static class CreateEvent extends ActionDomainEvent<StatementSource> {
		private static final long serialVersionUID = 1L;
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = WithName.MAX_LEN)
    @Title(prepend = "")
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
    
    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    private StatementSourceType type;
    
    @Builder
    public StatementSource(final String name, final String description, final StatementSourceType type) {
        setName(name);
        setDescription(description);
        setType(type == null ? StatementSourceType.SAVING_ACCOUNT : type);
    }

    @Override
    public int compareTo(final StatementSource other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName());
    }

}