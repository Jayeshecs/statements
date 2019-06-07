/**
 * 
 */
package domainapp.modules.txn.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = Transaction.class
)
public class TransactionService extends AbstractService<Transaction>{

	public TransactionService() {
		super(Transaction.class);
	}
	
	@Programmatic
	public List<Transaction> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public Transaction credit(String sourceName, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		StatementSource statementSource = statementSourceService.getOrCreate(sourceName);
		return create(TransactionType.CREDIT, statementSource, transactionDate, amount, narration, reference, rawdata);
	}

	@Programmatic
	public Transaction debit(String sourceName, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		StatementSource statementSource = statementSourceService.getOrCreate(sourceName);
		return debit(statementSource, transactionDate, amount, narration, reference, rawdata);
	}

	@Programmatic
	public Transaction debit(StatementSource statementSource, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		return create(TransactionType.DEBIT, statementSource, transactionDate, amount, narration, reference, rawdata);
	}
	
	@Programmatic
	public Transaction create(TransactionType type, StatementSource source, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		Transaction newTransaction = Transaction.builder()
				.type(type)
				.source(source)
				.amount(amount)
				.transactionDate(transactionDate)
				.narration(narration)
				.reference(reference)
				.rawdata(rawdata)
				.build();
		Transaction transaction = repositoryService.persistAndFlush(newTransaction);
    	return transaction;
	}
	
	@Inject
	StatementSourceService statementSourceService;
}
