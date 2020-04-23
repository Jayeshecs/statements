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
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
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
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = NamedQueryConstants.QUERY_ALL,
            value = "SELECT "
                    + "FROM domainapp.modules.rdr.dom.MessageIdempotentStore "),
    @javax.jdo.annotations.Query(
            name = MessageIdempotentStore.QUERY_FIND_BY_MESSAGE_ID,
            value = "SELECT "
                    + "FROM domainapp.modules.rdr.dom.MessageIdempotentStore "
                    + "WHERE messageId.indexOf(:messageId) >= 0 ")
})
@javax.jdo.annotations.Unique(name="MessageIdempotentStore_messageId_UNQ", members = {"messageId"})
@DomainObject(
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED,
        objectType = "reader.MessageIdempotentStore",
        bounded = true
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"messageId"})
@ToString(of = {"messageId"})
public class MessageIdempotentStore implements Comparable<MessageIdempotentStore> {
	
	public static final String QUERY_FIND_BY_MESSAGE_ID = "findByMessageId"; //$NON-NLS-1$

	@javax.jdo.annotations.Column(allowsNull = "false", length = 1000)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String messageId;

	@javax.jdo.annotations.Column(length = 2000)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String error;
    
    @Builder
    public MessageIdempotentStore(final String messageId, final String error) {
    	setMessageId(messageId);
    	setError(error);
    }
    
    public MessageIdempotentStore(MessageIdempotentStore messageIdempotentStore) {
		this(
				messageIdempotentStore.getMessageId(),
				messageIdempotentStore.getError()
		);	
	}

	public static class CreateEvent extends ActionDomainEvent<MessageIdempotentStore> {
		private static final long serialVersionUID = 1L;
    }
    
    public static class UpdateEvent extends ActionDomainEvent<MessageIdempotentStore> {
		private static final long serialVersionUID = 1L;
    }

    @Override
    public int compareTo(final MessageIdempotentStore other) {
    	if (other == null) {
    		return -1;
    	}
    	return getMessageId().compareTo(other.getMessageId());
    }

}