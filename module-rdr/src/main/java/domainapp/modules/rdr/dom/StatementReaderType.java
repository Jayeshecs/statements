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
                    + "FROM domainapp.modules.rdr.dom.StatementReaderType "),
        @javax.jdo.annotations.Query(
                name = NamedQueryConstants.QUERY_FIND_BY_NAME,
                value = "SELECT "
                        + "FROM domainapp.modules.rdr.dom.StatementReaderType "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="StatementReaderType_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED,
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class StatementReaderType implements Comparable<StatementReaderType>, WithNameAndDescription {
	
	public static final int CLASSNAME_MAX_LEN = 256;
	
    @Builder
    public StatementReaderType(final String name, final String description, final String className) {
        setName(name);
        setDescription(description);
        setClassName(className);
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = WithName.MAX_LEN)
    @Title(prepend = "RTYP ")
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

    @javax.jdo.annotations.Column(allowsNull = "false", length = CLASSNAME_MAX_LEN)
    @Property(
    		editing = Editing.ENABLED,
    		command = CommandReification.ENABLED,
    		publishing = Publishing.ENABLED
    )
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String className;
    
    public static class CreateEvent extends ActionDomainEvent<StatementReaderType> {
		private static final long serialVersionUID = 1L;
    }

    @Override
    public int compareTo(final StatementReaderType other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName());
    }

}