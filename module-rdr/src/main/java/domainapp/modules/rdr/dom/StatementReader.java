package domainapp.modules.rdr.dom;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

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
                name = "findByName",
                value = "SELECT "
                        + "FROM domainapp.modules.rdr.dom.StatementReader "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="StatementReader_name_UNQ", members = {"name"})
@DomainObject(
        auditing = Auditing.ENABLED
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"name"})
@ToString(of = {"name"})
public class StatementReader implements Comparable<StatementReader> {

    @Builder
    public StatementReader(final String name) {
        setName(name);
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
    private String notes;
    
    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.ENABLED,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    @Getter @Setter
    private StatementReaderType readerType;

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED
    )
    public StatementReader updateName(
            @Parameter(maxLength = 40)
            final String name) {
        setName(name);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    public TranslatableString validate0UpdateName(final String name) {
        return name != null && name.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.remove(this);
    }

    @Override
    public int compareTo(final StatementReader other) {
        return ObjectContracts.compare(this, other, "name");
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    TitleService titleService;

    @javax.inject.Inject
    MessageService messageService;

}