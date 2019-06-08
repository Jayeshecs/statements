/**
 * 
 */
package domainapp.modules.txn.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.SubCategory;
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
	
	/**
	 * DO NOT USE THIS INSTEAD USE COMBINATION OF {@link #buildFilter(String, Date, Date, BigDecimal, BigDecimal, TransactionType, StatementSource, Category, SubCategory, Map)} and {@link #search(String, Object...)}
	 */
	public List<Transaction> search(String narration, Date transactionDateStart, Date transactionDateEnd, BigDecimal amountMin, BigDecimal amountMax, StatementSource source, TransactionType type, Category category, SubCategory subCategory) {
		Map<String, Object> parameters = new HashMap<>();		
		String filter = buildFilter(narration, transactionDateStart, transactionDateEnd, amountMin, amountMax, type, source, category, subCategory, parameters);
		return filter(filter.toString(), parameters);
	}

	/**
	 * This API is useful for ViewModel where filter string is used as ID of view model
	 * 
	 * @param narration
	 * @param transactionDateStart
	 * @param transactionDateEnd
	 * @param amountMin
	 * @param amountMax
	 * @param source
	 * @param type
	 * @param parameters
	 * @return
	 */
	@Programmatic
	public String buildFilter(String narration, Date transactionDateStart, Date transactionDateEnd, BigDecimal amountMin, BigDecimal amountMax, 
			TransactionType type, StatementSource source, Category category, SubCategory subCategory, 
			Map<String, Object> parameters) {
		StringBuilder filter = new StringBuilder();
		boolean addAnd = false;
		if (narration != null && !narration.isEmpty()) {
			filter.append("narration.indexOf('" + narration + "') >= 0");
			addAnd = true;
		}
		if (transactionDateStart != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("transactionDate > :transactionDateStart");
			parameters.put("transactionDateStart", transactionDateStart);
			addAnd = true;
		}
		if (transactionDateEnd != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("transactionDate < :transactionDateEnd");
			parameters.put("transactionDateEnd", transactionDateEnd);
			addAnd = true;
		}
		if (amountMin != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("amount > :amountMin");
			parameters.put("amountMin", amountMin);
			addAnd = true;
		}
		if (amountMax != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("amount < :amountMax");
			parameters.put("amountMax", amountMax);
			addAnd = true;
		}
		if (source != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("source == :statementSource");
			parameters.put("statementSource", source);
			addAnd = true;
		}
		if (type != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("type == :type");
			parameters.put("type", type);
			addAnd = true;
		}
		if (category != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("category == :category");
			parameters.put("category", category);
			addAnd = true;
		}
		if (subCategory != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("subCategory == :subCategory");
			parameters.put("subCategory", subCategory);
			addAnd = true;
		}
		return filter.toString();
	}

	private void ensureAndPrefixed(StringBuilder filter, boolean addAnd) {
		if (addAnd) {
			filter.append(" && ");
		}
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
