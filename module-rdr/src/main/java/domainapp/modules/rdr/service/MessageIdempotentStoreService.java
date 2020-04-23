/**
 * 
 */
package domainapp.modules.rdr.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.rdr.dom.MessageIdempotentStore;

/**
 * Idempotent store for message ids<br>
 * Optionally error can also be stored indicating any processing error for given message id.
 * 
 * @author jayeshecs
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = MessageIdempotentStore.class
)
public class MessageIdempotentStoreService extends AbstractService<MessageIdempotentStore> {

	public MessageIdempotentStoreService() {
		super(MessageIdempotentStore.class);
	}
	
	@Programmatic
	public List<MessageIdempotentStore> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public MessageIdempotentStore create(String messageId, String error) {
		MessageIdempotentStore newMessageIdempotentStore = MessageIdempotentStore.builder().messageId(messageId).error(error).build();
		MessageIdempotentStore MessageIdempotentStore = repositoryService.persistAndFlush(newMessageIdempotentStore);
    	return MessageIdempotentStore;
	}
	
	@Programmatic
	public MessageIdempotentStore find(String messageId) {
		List<MessageIdempotentStore> list = search(MessageIdempotentStore.QUERY_FIND_BY_MESSAGE_ID, "messageId", messageId);
		if (list == null || list.isEmpty()) {
			return null; // nothing found
		}
		for (MessageIdempotentStore store : list) {
			if (store.getMessageId().equals(messageId)) {
				return store; // found
			}
		}
		return null; // exact match not found
	}
	
	@Programmatic
	public void clear(String messageId) {
		List<MessageIdempotentStore> list = search(MessageIdempotentStore.QUERY_FIND_BY_MESSAGE_ID, "messageId", messageId);
		if (list == null || list.isEmpty()) {
			return ; // nothing found
		}
		delete(list);
	}
}
