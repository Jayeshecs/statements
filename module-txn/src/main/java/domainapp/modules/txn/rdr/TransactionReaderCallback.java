/**
 * 
 */
package domainapp.modules.txn.rdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.api.IStatementReaderCallback;
import domainapp.modules.rdr.api.IStatementRecord;
import domainapp.modules.rdr.api.IStatementRecord.Field;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.service.TransactionService;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link IStatementReaderCallback} interface for {@link Transaction}
 * 
 * @author jayeshecs
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		objectType = "txn.rdr.TransactionReaderCallback"
)
@Slf4j
public class TransactionReaderCallback implements IStatementReaderCallback {

	@Programmatic
	@Override
	public void process(IStatementReaderContext context, Collection<IStatementRecord> records) {
		/**
		 * Get statement source from context
		 */
		StatementSource statementSource = context.get(IStatementReaderContext.PARAM_STATEMENT_SOURCE);
		Map<String, Transaction> transactionByRawdataKey = context.get("transactionByRawdataKey");
		if (transactionByRawdataKey == null) {
			// TODO: synchronization
			context.set("transactionByRawdataKey", transactionByRawdataKey = new HashMap<String, Transaction>());
		}
		
		int existingCount = 0;
		List<Transaction> transactionToSave = new ArrayList<>();
		for (IStatementRecord record : records) {
			if (record.isFiltered()) {
				context.addFilteredCount(1);
				continue;
			}
			Integer rawdataSequenceCount = 1;
			String rawdata = record.get(Field.RAWDATA);
			String rawdataKey = statementSource.getName() + rawdata + rawdataSequenceCount;
			while (transactionByRawdataKey.get(rawdataKey) != null) {
				rawdataSequenceCount++;
			}
			if (rawdata != null && !rawdata.trim().isEmpty()) {
				List<Transaction> result = transactionService.search(Transaction.QUERY_FIND_BY_RAWDATA, Transaction.FieldConstants.SOURCE, statementSource,Transaction.FieldConstants.RAWDATA, rawdata, Transaction.FieldConstants.RAWDATA_SEQUENCE, rawdataSequenceCount);
				if (result != null && !result.isEmpty()) {
					// skip transaction because it is already present
					existingCount++;
					context.addSkippedCount(1);
					transactionByRawdataKey.put(rawdataKey, result.get(0));
					continue ;
				}
			}
			Boolean credit = record.get(Field.CREDIT);
			
			Transaction transaction = transactionService.createNoSave(
					credit ? TransactionType.CREDIT : TransactionType.DEBIT, 
					statementSource, 
					record.get(Field.DATE), 
					record.get(Field.AMOUNT), 
					record.get(Field.NARRATION), 
					record.get(Field.REFERENCE), 
					rawdata,
					rawdataSequenceCount);
			transactionByRawdataKey.put(rawdataKey, transaction);
			transactionToSave.add(transaction);
		}
		try {
			transactionService.save(transactionToSave);
		} catch (Exception e) {
			log.error(e.getMessage() + "\n" + transactionToSave.toString());
			context.addErrorCount(transactionToSave.size());
		}
		log.info(String.format("Inserted %d of %d transactions", (records.size() - existingCount), records.size()));
	}
	
	@Inject
	RepositoryService repositoryService;
	
	@Inject
	TransactionService transactionService;

}
