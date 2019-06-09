/**
 * 
 */
package domainapp.modules.txn.rdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.rdr.api.IReaderCallback;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.service.TransactionService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link IReaderCallback} interface for {@link Transaction}
 * 
 * @author jayeshecs
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		objectType = "txn.rdr.TransactionReaderCallback"
)
@Slf4j
public class TransactionReaderCallback implements IReaderCallback<Transaction> {

	@Override
	public void process(Collection<Transaction> records) {
		int existingCount = 0;
		List<Transaction> transactionToSave = new ArrayList<>();
		for (Transaction record : records) {
			if (!repositoryService.isPersistent(record)) {
				List<Transaction> result = transactionService.search(Transaction.QUERY_FIND_BY_RAWDATA, "rawdata", record.getRawdata());
				if (result != null && !result.isEmpty()) {
					// skip transaction because it is already present
					existingCount++;
					continue ;
				}
				transactionToSave.add(record);
			}
		}
		transactionService.save(transactionToSave);
		log.info(String.format("Inserted %d of %d transactions", (records.size() - existingCount), records.size()));
	}
	
	@Inject
	RepositoryService repositoryService;
	
	@Inject
	TransactionService transactionService;

}
