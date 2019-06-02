package domainapp.modules.ref.dom;

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
            name = SubCategory.QUERY_ALL,
            value = "SELECT "
                    + "FROM domainapp.modules.ref.dom.SubCategory"),
        @javax.jdo.annotations.Query(
                name = SubCategory.QUERY_FIND_BY_NAME,
                value = "SELECT "
                        + "FROM domainapp.modules.ref.dom.SubCategory "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="SubCategory_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED, 
        objectType = "stmt.SubCategory",
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class SubCategory implements Comparable<SubCategory>, WithNameAndDescription {

	public static final String QUERY_ALL = "all";

	public static final String QUERY_FIND_BY_NAME = "findByName";

    @Builder
    public SubCategory(final String name, final String description) {
        setName(name);
        setDescription(description);
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Title(prepend = "")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String name;

    @javax.jdo.annotations.Column(allowsNull = "true", length = 4000)
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    private String description;
    
    public static class CreateEvent extends ActionDomainEvent<SubCategory> {
		private static final long serialVersionUID = 1L;
    }

    @Override
    public int compareTo(final SubCategory other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName()); // name must not be empty
    }

}