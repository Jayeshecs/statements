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

import domainapp.modules.base.datatype.DataType;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.filter.ListFilterBuilder;
import domainapp.modules.base.filter.MaxFilterBuilder;
import domainapp.modules.base.filter.MinFilterBuilder;
import domainapp.modules.base.filter.TextFilterBuilder;
import domainapp.modules.base.service.AbstractFilterableService;
import domainapp.modules.ref.datatype.StaticDataDataType;
import domainapp.modules.ref.dom.StatementSourceType;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.txn.datatype.TransactionDataType;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.dom.Transaction.FieldConstants;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = Transaction.class
)
public class TransactionService extends AbstractFilterableService<Transaction> {
	
	public interface TransactionFilterFields {
		String NARRATION = FieldConstants.NARRATION;
		
		String CATEGORY = FieldConstants.CATEGORY;
		String SUB_CATEGORY = FieldConstants.SUB_CATEGORY;
		String UNCATEGORIZED = "uncategorized";
		
		String AMOUNT_MIN = FieldConstants.AMOUNT + "Min";
		String AMOUNT_MAX = FieldConstants.AMOUNT + "Max";
		
		String TRANSACTION_DATE_MIN = FieldConstants.TRANSACTION_DATE + "Min";
		String TRANSACTION_DATE_MAX = FieldConstants.TRANSACTION_DATE + "Max";
		
		String TRANSACTION_TYPE = FieldConstants.TYPE;
		String STATEMENT_SOURCE = FieldConstants.SOURCE;
	}
	
	public TransactionService() {
		super(Transaction.class);
	}
	
	@Override
	protected void registerFieldFilterBuilders() {
		
		registerFieldFilter(TransactionFilterFields.NARRATION, new TextFilterBuilder(FieldConstants.NARRATION));
		
		registerFieldFilter(TransactionFilterFields.AMOUNT_MIN, new MinFilterBuilder(FieldConstants.AMOUNT, DataType.AMOUNT));
		registerFieldFilter(TransactionFilterFields.AMOUNT_MAX, new MaxFilterBuilder(FieldConstants.AMOUNT, DataType.AMOUNT));
		registerFieldFilter(TransactionFilterFields.TRANSACTION_DATE_MIN, new MinFilterBuilder(FieldConstants.TRANSACTION_DATE, DataType.DATE));
		registerFieldFilter(TransactionFilterFields.TRANSACTION_DATE_MAX, new MaxFilterBuilder(FieldConstants.TRANSACTION_DATE, DataType.DATE));
		
		registerFieldFilter(TransactionFilterFields.TRANSACTION_TYPE, new ListFilterBuilder(FieldConstants.TYPE, StaticDataDataType.TRANSACTION_TYPE));
		registerFieldFilter(TransactionFilterFields.CATEGORY, new ListFilterBuilder(FieldConstants.CATEGORY, StaticDataDataType.CATEGORY));
		registerFieldFilter(TransactionFilterFields.SUB_CATEGORY, new ListFilterBuilder(FieldConstants.SUB_CATEGORY, StaticDataDataType.SUB_CATEGORY));
		
		registerFieldFilter(TransactionFilterFields.UNCATEGORIZED, new UncategorizedFilterBuilder(TransactionFilterFields.UNCATEGORIZED));
		
		registerFieldFilter(TransactionFilterFields.STATEMENT_SOURCE, new ListFilterBuilder(FieldConstants.SOURCE, TransactionDataType.STATEMENT_SOURCE));
	}
	
	@Programmatic
	public List<Transaction> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public Transaction credit(String sourceName, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		StatementSource statementSource = statementSourceService.getOrCreate(sourceName, StatementSourceType.SAVING_ACCOUNT); // by default it is Saving account if not found
		return create(TransactionType.CREDIT, statementSource, transactionDate, amount, narration, reference, rawdata, 1);
	}

	@Programmatic
	public Transaction credit(StatementSource statementSource, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		return create(TransactionType.CREDIT, statementSource, transactionDate, amount, narration, reference, rawdata, 1);
	}

	@Programmatic
	public Transaction debit(String sourceName, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		StatementSource statementSource = statementSourceService.getOrCreate(sourceName, StatementSourceType.SAVING_ACCOUNT); // by default it is Saving account if not found
		return debit(statementSource, transactionDate, amount, narration, reference, rawdata);
	}

	@Programmatic
	public Transaction debit(StatementSource statementSource, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata) {
		return create(TransactionType.DEBIT, statementSource, transactionDate, amount, narration, reference, rawdata, 1);
	}
	
	@Programmatic
	public Transaction create(TransactionType type, StatementSource source, Date transactionDate, BigDecimal amount, String narration, String reference, String rawdata, Integer rawdataSequence) {
		Transaction newTransaction = createNoSave(type, source, transactionDate, amount, narration, reference, rawdata, rawdataSequence);
		Transaction transaction = repositoryService.persistAndFlush(newTransaction);
    	return transaction;
	}

	/**
	 * @param type
	 * @param source
	 * @param transactionDate
	 * @param amount
	 * @param narration
	 * @param reference
	 * @param rawdata
	 * @return
	 */
	@Programmatic
	public Transaction createNoSave(TransactionType type, StatementSource source, Date transactionDate,
			BigDecimal amount, String narration, String reference, String rawdata, Integer rawdataSequence) {
		Transaction newTransaction = Transaction.builder()
				.type(type)
				.source(source)
				.amount(amount)
				.transactionDate(transactionDate)
				.narration(narration)
				.reference(reference)
				.rawdata(rawdata)
				.rawdataSequence(rawdataSequence)
				.build();
		return newTransaction;
	}
	
	@Inject
	StatementSourceService statementSourceService;
}
