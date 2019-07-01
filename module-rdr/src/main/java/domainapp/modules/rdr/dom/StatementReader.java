package domainapp.modules.rdr.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
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
                    + "FROM domainapp.modules.rdr.dom.StatementReader "),
        @javax.jdo.annotations.Query(
                name = NamedQueryConstants.QUERY_FIND_BY_NAME,
                value = "SELECT "
                        + "FROM domainapp.modules.rdr.dom.StatementReader "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="StatementReader_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED,
        objectType = "reader.StatementReader",
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class StatementReader implements Comparable<StatementReader>, WithNameAndDescription {
	
	public static final int PROPERTIES_MAX_LEN = 2000;

    @javax.jdo.annotations.Column(allowsNull = "false", length = WithName.MAX_LEN)
    @Title(prepend = "RDR ")
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
    @MemberOrder(sequence = "3")
    @Getter @Setter
    private StatementReaderType readerType;
    
    @javax.jdo.annotations.Column(allowsNull = "true", length = PROPERTIES_MAX_LEN)
	@Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(labelPosition = LabelPosition.TOP, multiLine = 4, describedAs = "Standard property file format to specify properties to be used by this statement reader")
    @MemberOrder(sequence = "4")
    @lombok.Getter @lombok.Setter
    private String properties;

    @Builder
    public StatementReader(final String name, final String description, final StatementReaderType readerType, final String properties) {
        setName(name);
        setDescription(description);
        setReaderType(readerType);
        setProperties(properties);
    }
    
    public static class CreateEvent extends ActionDomainEvent<StatementReader> {
		private static final long serialVersionUID = 1L;
    }

    @Override
    public int compareTo(final StatementReader other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName());
    }

}