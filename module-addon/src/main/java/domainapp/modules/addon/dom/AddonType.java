package domainapp.modules.addon.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import domainapp.modules.addon.AddonModule.ActionDomainEvent;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.entity.WithNameAndDescription;
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
		@javax.jdo.annotations.Query(name = NamedQueryConstants.QUERY_ALL, value = "SELECT "
				+ "FROM domainapp.modules.addon.dom.AddonType "),
		@javax.jdo.annotations.Query(name = NamedQueryConstants.QUERY_ALL_ACTIVE, value = "SELECT "
				+ "FROM domainapp.modules.addon.dom.AddonType "),
		@javax.jdo.annotations.Query(name = NamedQueryConstants.QUERY_FIND_BY_NAME, value = "SELECT "
				+ "FROM domainapp.modules.addon.dom.AddonType " + "WHERE name.indexOf(:name) >= 0 ") })
@javax.jdo.annotations.Unique(name="AddonType_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED, 
        objectType = "addon.AddonType",
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@DomainObjectLayout(
		named = "Addon Type",
		plural = "Addon Types",
		describedAs = "Nature of Addon"
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class AddonType implements Comparable<AddonType>, WithNameAndDescription {
	
	@javax.jdo.annotations.Column(allowsNull = "false", length = WithName.MAX_LEN)
    @Title
    @Property(
    		editing = Editing.DISABLED, 
    		editingDisabledReason = "Name cannot change after set while creating category"
    )
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

    @Builder
    public AddonType(final String name, final String description) {
        setName(name);
        setDescription(description);
    }
    
    public static class CreateEvent extends ActionDomainEvent<AddonType> {
		private static final long serialVersionUID = 1L;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AddonType other) {
    	if (other == null) {
    		return -1;
    	}
    	return getName().compareTo(other.getName()); // name must not be empty
    }
}