/**
 * 
 */
package domainapp.modules.txn.view.dashboard;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.hint.HintStore;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import domainapp.modules.addon.service.AddonService;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.view.GenericlFilter;
import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.addon.StatementReaderContext;
import domainapp.modules.rdr.api.IStatementReader;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.StatementSourceType;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.rdr.TransactionReaderCallback;
import domainapp.modules.txn.service.StatementSourceService;
import domainapp.modules.txn.service.TransactionService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jayeshecs
 *
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "stmt.ManageTransactionDashboard"
)
@Slf4j
public class ManageTransactionDashboard implements HintStore.HintIdProvider{
	
	@PropertyLayout(hidden = Where.EVERYWHERE)
	private GenericlFilter filter;
	
	/**
	 * @return
	 */
	public String title() {
		return "Manage Transactions";
	}
	
	@Override
	public String hintId() {
		return getFilterDescription();
	}
	
	@Property(editing = Editing.DISABLED, editingDisabledReason = "This is read-only field")
	@PropertyLayout(labelPosition = LabelPosition.TOP, named = "Filter", multiLine = 4)
	@MemberOrder(sequence = "1")
	public String getFilterDescription() {
		if (filter == null) {
			return "[ Show all transactions ]";
		}
		String filterDescription = filter.getFilter();
		Map<String, Object> parameters = filter.getParameters();
		if (parameters == null || parameters.isEmpty()) {
			return filterDescription;
		}
		for (Entry<String, Object> entry : parameters.entrySet()) {
			filterDescription = filterDescription.replaceAll(":" + entry.getKey(), String.valueOf(entry.getValue()));
		}
		return filterDescription;
	}
	
	/**
	 * @return 
	 */
	@Collection(typeOf = StatementSource.class)
	@CollectionLayout(defaultView = "table")
	@MemberOrder(sequence = "2")
	public List<StatementSource> getStatementSources() {
		return statementSourceService.all();
	}
	
	@Action(
    		domainEvent = StatementSource.CreateEvent.class,
			associateWith = "statementSources", 
			associateWithSequence = "1", 
			semantics = SemanticsOf.SAFE, 
			typeOf = StatementSource.class
	)
	@ActionLayout(
			named = "Create",
			describedAs = "Create new statement source",
			position = Position.PANEL, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard createStatementSource(
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Source Type", describedAs = "Description of statement source to be created")
			String sourceType,
			@Parameter(optionality = Optionality.MANDATORY, maxLength = WithName.MAX_LEN)
			@ParameterLayout(named = "Name", describedAs = "Unique name of statement source to be created")
			String name,
			@Parameter(optionality = Optionality.OPTIONAL, maxLength = WithDescription.MAX_LEN)
			@ParameterLayout(named = "Description", multiLine = 4, labelPosition = LabelPosition.TOP, describedAs = "Description of statement source to be created")
			String description
			) {
		StatementSourceType type = StatementSourceType.valueOf(sourceType);
		StatementSource statementSource = null;
		List<StatementSource> list = statementSourceService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		if (list != null && !list.isEmpty()) {
			for (StatementSource ss : list) {
				if (ss.getName().equals(name)) {
					statementSource = ss;
					break;
				}
			}
		}
		if (statementSource != null) {
			messageService.warnUser(TranslatableString.tr("A statement source with same name '${statementSourceName}' already exists", "statementSourceName", name), getClass(), "createStatementSource");
			return this;
		}
		statementSource = statementSourceService.create(name, description, type);
		if (statementSource == null) {
			messageService.warnUser(TranslatableString.tr("Statement source could not be created with detail provided"), getClass(), "createStatementSource");
		}
		return this;
	}
	
	public List<String> choices0CreateStatementSource() {
		List<String> result = new ArrayList<>();
		for (StatementSourceType type :StatementSourceType.values()) {
			result.add(type.getName());
		}
		return result;
	}
	
	@Action(
    		domainEvent = Transaction.CreateEvent.class,
			semantics = SemanticsOf.SAFE
	)
	@ActionLayout(
			named = "Upload",
			describedAs = "Upload statement",
			position = Position.PANEL, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard uploadStatement(
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement Source", describedAs = "Select the statement source corresponding to selected statement file")
			StatementSource source,
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement Reader", describedAs = "Select statement reader for reading statement file")
			StatementReader statementReader,
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement File", describedAs = "Select statement file")
			Blob statementFile
			) {
		try (InputStream is = new BufferedInputStream(new ByteArrayInputStream(statementFile.getBytes()))) {
			/**
			 * Load custom properties/config associated with given StatementReader
			 */
			Properties config = new Properties();
			String properties = statementReader.getProperties();
			if (properties != null && !properties.trim().isEmpty()) {
				config.load(new StringReader(properties));
			}
			/**
			 * Save file to temporary location for reading
			 */
			Path tempFile = Files.createTempFile("", statementFile.getName());
			Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
			/**
			 * Create statement reader context
			 */
			StatementReaderContext readerContext = StatementReaderContext.builder().id(1L).name(source.getName()).config(config).file(tempFile.toFile()).build();
			// specify the target source of statement reader
			readerContext.set(IStatementReaderContext.PARAM_STATEMENT_SOURCE, source);
			/**
			 * Prepare reader API and start reading
			 */
			IStatementReader reader = addonService.get(statementReader.getReaderType().getAddon().getName());
			reader.read(readerContext, transactionReaderCallback);
			messageService.informUser(TranslatableString.tr("Statement upload completed successfully"), getClass(), "uploadStatement");
		} catch (Exception e) {
			log.error("Error occurred while uploading statement - " + e.getMessage(), e);
			messageService.warnUser(TranslatableString.tr("Unexpected error has occurred while uploading statement"), getClass(), "uploadStatement");
		}
		return this;
	}
	
	/**
	 * @return 
	 */
	@Collection(typeOf = Transaction.class)
	@CollectionLayout(defaultView = "table", paged = 100)
	@MemberOrder(sequence = "3")
	public List<Transaction> getTransactions() {
		if (filter != null) {
			return transactionService.filter(filter.getFilter(), filter.getParameters());
		}
		return transactionService.all();
	}
	
	@Action(
			associateWith = "transactions", 
			associateWithSequence = "1", 
			semantics = SemanticsOf.SAFE, 
			typeOf = Transaction.class)
	@ActionLayout(
			named = "Change Category", 
			position = Position.RIGHT, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard changeCategory(
			List<Transaction> selectedTransactions, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Category")
			Category category, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Sub-Category")
			SubCategory subCategory) {
		/**
		 * Update all transaction records with given category and sub-category
		 */
		for (Transaction transaction : selectedTransactions) {
			transaction.setCategory(category);
			transaction.setSubCategory(subCategory);
		}
		/**
		 * save all transaction records
		 */
		transactionService.save(selectedTransactions);
		return this;
	}
	
	@Action(
			associateWith = "transactions", 
			associateWithSequence = "2", 
			semantics = SemanticsOf.SAFE, 
			typeOf = Transaction.class)
	@ActionLayout(
			named = "Filter", 
			position = Position.RIGHT, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard filter(
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Narration contains")
			String narration, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Transaction type")
			TransactionType type, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Statement Source")
			StatementSource source, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Start date")
			Date dateStart, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "End date")
			Date dateEnd, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Minimum")
			BigDecimal amountFloor, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Maximum")
			BigDecimal amountCap, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Category")
			Category category, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Sub-Category")
			SubCategory subCategory) {
		filter = new GenericlFilter();
		filter.setFilter(transactionService.buildFilter(narration, dateStart, dateEnd, amountFloor, amountCap, type, source, category, subCategory, filter.getParameters()));
		return this;
	}
	
	@Action(
    		domainEvent = Transaction.CreateEvent.class,
			associateWith = "transactions", 
			associateWithSequence = "2", 
			semantics = SemanticsOf.SAFE, 
			typeOf = Transaction.class
	)
	@ActionLayout(
			named = "Add Expense", 
			position = Position.PANEL_DROPDOWN, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard expense(
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Source")
			String statementSource, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Transaction date")
			Date transactionDate, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Amount")
			BigDecimal amount, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Narration")
			String narration, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Reference")
			String reference) {
		String rawdata = String.format("%tF,%.2f,\"%s\",\"%s\"", transactionDate, amount, narration, reference);
		transactionService.debit(statementSource, transactionDate, amount, narration, reference, rawdata);
		return this;
	}
	
	@Action(
    		domainEvent = Transaction.CreateEvent.class,
			associateWith = "transactions", 
			associateWithSequence = "3", 
			semantics = SemanticsOf.SAFE, 
			typeOf = Transaction.class
	)
	@ActionLayout(
			named = "Add Income", 
			position = Position.PANEL_DROPDOWN, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard income(
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Source")
			String statementSource, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Transaction date")
			Date transactionDate, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Amount")
			BigDecimal amount, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Narration")
			String narration, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Reference")
			String reference) {
		String rawdata = String.format("%tF,%.2f,\"%s\",\"%s\"", transactionDate, amount, narration, reference);
		transactionService.credit(statementSource, transactionDate, amount, narration, reference, rawdata);
		return this;
	}

	@Inject
	TransactionService transactionService;

	@Inject
	StatementSourceService statementSourceService;
	
	@Inject
	MessageService messageService;
	
	@Inject
	TransactionReaderCallback transactionReaderCallback;

	@Inject
	AddonService addonService;

}
