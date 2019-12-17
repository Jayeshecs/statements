/**
 * 
 */
package domainapp.modules.txn.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.datatype.DataType;
import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.base.service.OrderBy;
import domainapp.modules.base.view.Value;
import domainapp.modules.ref.datatype.StaticDataDataType;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.StatementSourceType;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.txn.datatype.TransactionDataType;
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
	@Programmatic
	public List<Transaction> search(String narration, Date transactionDateStart, Date transactionDateEnd, BigDecimal amountMin, BigDecimal amountMax, List<StatementSource> source, List<TransactionType> transactionTypes, List<Category> categories, List<SubCategory> subCategories, Boolean uncategorized) {
		Map<String, Value> parameters = new HashMap<>();
		String filter = buildFilter(narration, transactionDateStart, transactionDateEnd, amountMin, amountMax, transactionTypes, source, categories, subCategories, uncategorized, parameters);
		parameters.remove("uncategorized");
		OrderBy orderBy = new OrderBy();
		orderBy.add("transactionDate", true);
		return filter(filter.toString(), orderBy, DataTypeUtil.toMapOfObject(parameters));
	}

	public static final Pattern CSV_REGEX_PATTERN = Pattern.compile("(\"(?:[^\"]|\"\")*\"|[^,\"\\n\\r]*)(,|\\r?\\n|\\r)");
	
	/**
	 * Remove enclosing quotes and ending delimiter.<br>
	 * "Paid For Order", => Paid For Order<br>
	 * 
	 * @param text
	 * @return
	 */
	private static final String sanitizeCsvValue(String text) {
		if (text == null) {
			return null;
		}
		text = text.trim();
		if (text.charAt(0) == '"' && text.endsWith("\",")) {
			return text.substring(1, text.length() - 2).trim();
		} else if (text.charAt(text.length() - 1) == ',') {
			return text.substring(0, text.length() - 1).trim();
		}
		return text.trim();
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
	 * @param transactionTypes
	 * @param parameters
	 * @return
	 */
	@Programmatic
	public String buildFilter(String narration, Date transactionDateStart, Date transactionDateEnd, BigDecimal amountMin, BigDecimal amountMax, 
			List<TransactionType> transactionTypes, List<StatementSource> sources, List<Category> categories, List<SubCategory> subCategories, Boolean uncategorized,
			Map<String, Value> parameters) {
		StringBuilder filter = new StringBuilder();
		boolean addAnd = false;
		if (narration != null && !narration.isEmpty()) {
			Matcher matcher = CSV_REGEX_PATTERN.matcher(narration + ",");
			boolean prefixOr = false;
			filter.append("(");
			while (matcher.find()) {
				if (prefixOr) {
					filter.append(" || ");
				}
				String narration2 = sanitizeCsvValue(matcher.group(0));
				filter.append("narration.indexOf('" + narration2 + "') >= 0");
				prefixOr = true;
			}
			filter.append(")");
			addAnd = true;
			parameters.put("narration", DataTypeUtil.createValue(DataType.TEXT, Arrays.asList(narration)));
		}
		if (transactionDateStart != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("transactionDate >= :transactionDateStart");
			parameters.put("transactionDateStart", DataTypeUtil.createValue(DataType.DATE, Arrays.asList(transactionDateStart)));
			addAnd = true;
		}
		if (transactionDateEnd != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("transactionDate <= :transactionDateEnd");
			parameters.put("transactionDateEnd", DataTypeUtil.createValue(DataType.DATE, Arrays.asList(transactionDateEnd)));
			addAnd = true;
		}
		if (amountMin != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("amount >= :amountMin");
			parameters.put("amountMin", DataTypeUtil.createValue(DataType.AMOUNT, Arrays.asList(amountMin)));
			addAnd = true;
		}
		if (amountMax != null) {
			ensureAndPrefixed(filter, addAnd);
			filter.append("amount <= :amountMax");
			parameters.put("amountMax", DataTypeUtil.createValue(DataType.AMOUNT, Arrays.asList(amountMax)));
			addAnd = true;
		}
		if (sources != null && !sources.isEmpty()) {
			ensureAndPrefixed(filter, addAnd);
			filter.append(":statementSource.contains(source)");
			parameters.put("statementSource", DataTypeUtil.createValue(TransactionDataType.STATEMENT_SOURCE, sources));
			addAnd = true;
		}
		if (transactionTypes != null && !transactionTypes.isEmpty()) {
			ensureAndPrefixed(filter, addAnd);
			filter.append(":type.contains(type)");
			parameters.put("type", DataTypeUtil.createValue(StaticDataDataType.TRANSACTION_TYPE, transactionTypes));
			addAnd = true;
		}
		if ((uncategorized == null || uncategorized == false)) {
			if (categories != null) {
				ensureAndPrefixed(filter, addAnd);
				filter.append(":category.contains(category)");
				parameters.put("category", DataTypeUtil.createValue(StaticDataDataType.CATEGORY, categories));
				addAnd = true;
			}
			if (subCategories != null) {
				ensureAndPrefixed(filter, addAnd);
				filter.append(":subCategory.contains(subCategory)");
				parameters.put("subCategory", DataTypeUtil.createValue(StaticDataDataType.SUB_CATEGORY, subCategories));
				addAnd = true;
			}
		} 
		if (uncategorized != null && uncategorized == true) {
			ensureAndPrefixed(filter, addAnd);
			parameters.put("uncategorized", DataTypeUtil.createValue(DataType.YESNO, Arrays.asList(Boolean.TRUE)));
			filter.append("(");
			filter.append("(category == null && subCategory == null) ");
			addAnd = true;
			if ((categories != null && !categories.isEmpty()) || (subCategories != null && !subCategories.isEmpty())) {
				filter.append(" || (");
			}
			if (categories != null && !categories.isEmpty()) {
				filter.append(":category.contains(category)");
				parameters.put("category", DataTypeUtil.createValue(StaticDataDataType.CATEGORY, categories));
				addAnd = true;
			}
			if (subCategories != null && !subCategories.isEmpty()) {
				if (categories != null) {
					ensureAndPrefixed(filter, true);
				}
				filter.append(":subCategory.contains(subCategory)");
				parameters.put("subCategory", DataTypeUtil.createValue(StaticDataDataType.SUB_CATEGORY, subCategories));
				addAnd = true;
			}
			if ((categories != null && !categories.isEmpty()) || (subCategories != null && !subCategories.isEmpty())) {
				filter.append(" ) ");
			}
			filter.append(")");
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
