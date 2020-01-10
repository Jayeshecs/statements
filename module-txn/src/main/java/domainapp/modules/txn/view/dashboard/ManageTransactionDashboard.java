/**
 * 
 */
package domainapp.modules.txn.view.dashboard;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;

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
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import domainapp.modules.addon.service.AddonService;
import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.service.AbstractFilterableService;
import domainapp.modules.base.service.OrderBy;
import domainapp.modules.base.view.GenericFilter;
import domainapp.modules.base.view.Value;
import domainapp.modules.base.view.dashboard.AbstractFilterableDashboard;
import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.addon.StatementReaderContext;
import domainapp.modules.rdr.api.IStatementReader;
import domainapp.modules.rdr.dom.MailConnectionProfile;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.rdr.service.MailConnection;
import domainapp.modules.rdr.service.MailConnectionProfileService;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.StatementSourceType;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.ref.service.CategoryService;
import domainapp.modules.ref.service.SubCategoryService;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.dom.Transaction.FieldConstants;
import domainapp.modules.txn.rdr.TransactionReaderCallback;
import domainapp.modules.txn.service.StatementSourceService;
import domainapp.modules.txn.service.TransactionService;
import domainapp.modules.txn.service.TransactionService.TransactionFilterFields;
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
public class ManageTransactionDashboard extends AbstractFilterableDashboard<Transaction> {
	
	/**
	 * @return
	 */
	public String title() {
		return "Manage Transactions";
	}

	/**
	 * @return 
	 * 
	 */
	@Programmatic
	@Override
	protected GenericFilter defaultFilter() {
		GenericFilter filter = new GenericFilter();
		filter.setFilter(FieldConstants.CATEGORY + " == null && " + FieldConstants.SUB_CATEGORY + " == null");
		filter.setExclude(new HashSet<String>(getFilterFieldsToExcludeFromQueryParameter()));
		return filter;
	}
	
	@Property(editing = Editing.DISABLED, editingDisabledReason = "This is read-only field")
	@PropertyLayout(labelPosition = LabelPosition.TOP, named = "Filter", multiLine = 3)
	@MemberOrder(sequence = "1")
	public String getFilterDescription() {
		GenericFilter filter = getFilter();
		if (filter == null || filter.getFilter().trim().isEmpty()) {
			return "[ Show all transactions ]";
		}
		String filterDescription = filter.getFilter();
		Map<String, Value> parameters = filter.getParameters();
		if (parameters == null || parameters.isEmpty()) {
			return filterDescription;
		}
		for (Entry<String, Value> entry : parameters.entrySet()) {
			filterDescription = filterDescription.replaceAll(":" + entry.getKey(), String.valueOf(DataTypeUtil.valueToObject(entry.getValue())));
		}
		filterDescription = filterDescription.replaceAll("\\.indexOf\\(", " ~ ");
		filterDescription = filterDescription.replaceAll("\\) >= 0", " ");
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
			@ParameterLayout(named = WithName.FIELD_NAME, describedAs = "Unique name of statement source to be created")
			String name,
			@Parameter(optionality = Optionality.OPTIONAL, maxLength = WithDescription.MAX_LEN)
			@ParameterLayout(named = "Description", multiLine = 4, labelPosition = LabelPosition.TOP, describedAs = "Description of statement source to be created")
			String description
			) {
		StatementSourceType type = StatementSourceType.valueOf(sourceType);
		StatementSource statementSource = null;
		List<StatementSource> list = statementSourceService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, name);
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
			named = "Upload (Zip)",
			describedAs = "Upload more than one statements in a zip file",
			position = Position.PANEL, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard uploadBulkStatement(
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement Source", describedAs = "Select the statement source for which you are uploading zip file containing more than one statements")
			StatementSource source,
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement Reader", describedAs = "Select statement reader for reading statement files present inside zip file")
			StatementReader statementReader,
			@Parameter(optionality = Optionality.MANDATORY, fileAccept = "zip")
			@ParameterLayout(named = "Statement File (zip)", describedAs = "Select statement file (*.zip)")
			Blob statementFile
			) {
		StringBuilder sb = new StringBuilder();
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(statementFile.getBytes()))) {
			ZipEntry ze = null;
			int failureCount = 0;
            while((ze = zis.getNextEntry()) != null){
                String fileName = ze.getName();
                Path tempFile = Files.createTempFile("", fileName);
                Files.copy(zis, tempFile, StandardCopyOption.REPLACE_EXISTING);
                boolean result = loadStatement(source, statementReader, fileName, new FileInputStream(tempFile.toFile()));
                if (!result) {
                	failureCount++;
                	sb.append(failureCount + ") " + fileName + "\n");
                }
            }
            if (failureCount > 0) {
    			messageService.warnUser(TranslatableString.tr("Bulk upload of statements has failed for ${failureCount} files - ${failures}", "failureCount", failureCount, "failures", sb.toString()), getClass(), "uploadBulkStatement");
            } else {
    			messageService.informUser(TranslatableString.tr("Statement upload completed successfully"), getClass(), "uploadBulkStatement");
            }
		} catch (IOException e) {
			log.error("Error occurred while uploading statement - " + e.getMessage(), e);
			messageService.warnUser(TranslatableString.tr("Unexpected error has occurred while uploading statement"), getClass(), "uploadBulkStatement");
		}
		return this;
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
			@Parameter(optionality = Optionality.MANDATORY, fileAccept = "csv|pdf")
			@ParameterLayout(named = "Statement File", describedAs = "Select statement file (*.csv, *.pdf)")
			Blob statementFile
			) {
		boolean success = loadStatement(source, statementReader, statementFile.getName(), new ByteArrayInputStream(statementFile.getBytes()));
		if (success) {
			messageService.informUser(TranslatableString.tr("Statement upload completed successfully"), getClass(), "uploadStatement");
		} else {
			messageService.warnUser(TranslatableString.tr("Unexpected error has occurred while uploading statement"), getClass(), "uploadStatement");
		}
		return this;
	}

	/**
	 * @param source
	 * @param statementReader
	 * @param statementFile
	 * @return
	 */
	@Programmatic
	private boolean loadStatement(StatementSource source, StatementReader statementReader, String fileName, InputStream inputStream) {
		boolean success = false;
		try (InputStream is = new BufferedInputStream(inputStream)) {
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
			Path tempFile = Files.createTempFile("", fileName);
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
			success = true;
		} catch (Exception e) {
			log.error("Error occurred while uploading statement - " + e.getMessage(), e);
			success = false;
		}
		return success;
	}
	
	/**
	 * @return 
	 */
	@Collection(typeOf = Transaction.class)
	@CollectionLayout(defaultView = "table", paged = 100)
	@MemberOrder(sequence = "3")
	public List<Transaction> getTransactions() {
		GenericFilter filter = getFilter();
		if (filter != null) {
			final Map<String, Object> map = DataTypeUtil.toMapOfObject(filter.getParameters());
			filter.getExclude().forEach(param -> {
				map.remove(param);
			});
			OrderBy orderBy = new OrderBy();
			orderBy.add(Transaction.FieldConstants.TRANSACTION_DATE, true);
			return transactionService.filter(filter.getFilter(), orderBy, map);
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
			associateWithSequence = "1", 
			semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE, 
			typeOf = Transaction.class)
	@ActionLayout(
			named = "Reset", 
			position = Position.RIGHT, 
			promptStyle = PromptStyle.INLINE)
	public ManageTransactionDashboard reset() {
		Map<String, Object> criteria = new LinkedHashMap<String, Object>();
		criteria.put(TransactionService.TransactionFilterFields.UNCATEGORIZED, Boolean.FALSE);
		prepareFilter(criteria);
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
			List<TransactionType> transactionTypes, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Statement Source")
			List<StatementSource> source, 
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
			List<Category> categories, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Sub-Category")
			List<SubCategory> subCategories,
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Uncategorized", describedAs = "Exclude selected category and/or sub-category from filter criteria")
			Boolean uncategorized
			) {
		internalFilter(narration, transactionTypes, source, dateStart, dateEnd, amountFloor, amountCap, categories, subCategories, uncategorized);
		return this;
	}

	/**
	 * @param narration
	 * @param transactionTypes
	 * @param source
	 * @param dateStart
	 * @param dateEnd
	 * @param amountFloor
	 * @param amountCap
	 * @param categories
	 * @param subCategories
	 * @param uncategorized
	 * @return 
	 */
	@Programmatic
	public void internalFilter(String narration, List<TransactionType> transactionTypes, List<StatementSource> source, Date dateStart,
			Date dateEnd, BigDecimal amountFloor, BigDecimal amountCap, List<Category> categories, List<SubCategory> subCategories,
			Boolean uncategorized) {
		Map<String, Object> criteria = new LinkedHashMap<String, Object>();
		criteria.put(TransactionFilterFields.NARRATION, narration);
		criteria.put(TransactionFilterFields.TRANSACTION_DATE_MIN, dateStart);
		criteria.put(TransactionFilterFields.TRANSACTION_DATE_MAX, dateEnd);
		criteria.put(TransactionFilterFields.AMOUNT_MIN, amountFloor);
		criteria.put(TransactionFilterFields.AMOUNT_MAX, amountCap);
		criteria.put(TransactionFilterFields.STATEMENT_SOURCE, source);
		criteria.put(TransactionFilterFields.TRANSACTION_TYPE, transactionTypes);
		criteria.put(TransactionFilterFields.CATEGORY, categories);
		criteria.put(TransactionFilterFields.SUB_CATEGORY, subCategories);
		criteria.put(TransactionFilterFields.UNCATEGORIZED, uncategorized);
		
		prepareFilter(criteria);
	}

	/**
	 * @return
	 */
	@Programmatic
	@Override
	protected AbstractFilterableService<Transaction> getFilterableService() {
		return transactionService;
	}

	/**
	 * @return
	 */
	@Programmatic
	@Override
	protected List<String> getFilterFieldsToExcludeFromQueryParameter() {
		return Arrays.asList(TransactionService.TransactionFilterFields.UNCATEGORIZED, TransactionService.TransactionFilterFields.NARRATION);
	}
	
	public String default0Filter() {
		return getUserInputValue(TransactionService.TransactionFilterFields.NARRATION);
	}
	
	public List<TransactionType> default1Filter() {
		return getUserInputValueAsList(TransactionService.TransactionFilterFields.TRANSACTION_TYPE);
	}
	
	public List<StatementSource> default2Filter() {
		return getUserInputValueAsList(TransactionService.TransactionFilterFields.STATEMENT_SOURCE);
	}
	
	public List<StatementSource> choices2Filter() {
		return statementSourceService.all();
	}
	
	public List<TransactionType> choices1Filter() {
		return Arrays.asList(TransactionType.values());
	}
	
	public List<Category> choices7Filter() {
		return categoryService.all();
	}
	
	public List<SubCategory> choices8Filter() {
		return subCategoryService.all();
	}
	
	public Date default3Filter() {
		return getUserInputValue(TransactionService.TransactionFilterFields.TRANSACTION_DATE_MIN);
	}
	
	public Date default4Filter() {
		return getUserInputValue(TransactionService.TransactionFilterFields.TRANSACTION_DATE_MAX);
	}
	
	public BigDecimal default5Filter() {
		Number userInputValue = getUserInputValue(TransactionService.TransactionFilterFields.AMOUNT_MIN);
		if (userInputValue == null) {
			return null;
		}
		return new BigDecimal(String.valueOf(userInputValue));
	}
	
	public BigDecimal default6Filter() {
		Number userInputValue = getUserInputValue(TransactionService.TransactionFilterFields.AMOUNT_MAX);
		if (userInputValue == null) {
			return null;
		}
		return new BigDecimal(String.valueOf(userInputValue));
	}
	
	public List<Category> default7Filter() {
		return getUserInputValueAsList(TransactionService.TransactionFilterFields.CATEGORY);
	}
	
	public List<SubCategory> default8Filter() {
		return getUserInputValueAsList(TransactionService.TransactionFilterFields.SUB_CATEGORY);
	}
	
	public Boolean default9Filter() {
		Boolean uncategorized = getUserInputValue(TransactionService.TransactionFilterFields.UNCATEGORIZED);
		return uncategorized == null ? Boolean.FALSE : uncategorized;
	}
	
	@Action(
    		domainEvent = Transaction.CreateEvent.class,
			associateWith = "transactions", 
			associateWithSequence = "5", 
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
			associateWithSequence = "10", 
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
	
	@Action(
    		domainEvent = Transaction.CreateEvent.class,
			semantics = SemanticsOf.SAFE
	)
	@ActionLayout(
			named = "Fetch Statements From Mail",
			describedAs = "Fetch Statements From Mail and load them",
			position = Position.PANEL, 
			promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard loadStatementFromMail(
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement Source", describedAs = "Select the statement source for which you are uploading zip file containing more than one statements")
			StatementSource source,
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement Reader", describedAs = "Select statement reader for reading statement files present inside zip file")
			StatementReader statementReader,
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Mail Connection Profile")
			MailConnectionProfile mailConnectionProfile, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Folder", describedAs = "Mailbox folder in which mails will be searched")
			String folder, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Subject has words", describedAs = "Comma or space separated words that are present in the mail subject")
			String subject, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "From Address")
			String sender,
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Statement filename pattern")
			String filenamePattern
			) {
		final StringBuilder tracker = new StringBuilder();
		MailConnection connection = null;
		try {
			
			tracker.append("Connecting mailbox...");
			connection = mailConnectionProfileService.getMailConnection(mailConnectionProfile);
			
			tracker.append("Searching mail...");
			Message[] messages = connection.search(folder, subject, sender, true);
			
			tracker.append("Messages found - " + messages.length);
			tracker.append("Iterating messages...");
			for (Message message : messages) {
				tracker.append("Checking message - " + message.getMessageNumber() + " (" + message.getHeader("Message-ID") + ")");
				Object content = message.getContent();
				if (!(content instanceof Multipart)) {
					tracker.append("Skipping message " + message.getSubject() + " because it does not have attachment");
					continue;
				}
				Multipart partMessage = (Multipart)content;
				for (int i = 0; i < partMessage.getCount(); ++i) {
					BodyPart bodyPart = partMessage.getBodyPart(i);
					if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) || !bodyPart.getFileName().matches(filenamePattern)) {
						tracker.append("Skipping attachment " + bodyPart.getFileName() + " because it's not matching with filename pattern - " + filenamePattern);
						continue ;
					}
					Path path = Files.createTempFile(null, bodyPart.getFileName());
					
					tracker.append("Saving attachment at " + path.toFile().getPath());
					Files.copy(bodyPart.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					
					tracker.append("Loading saved attachment as statement... ");
					loadStatement(source, statementReader, path.toFile().getName(), new FileInputStream(path.toFile()));
				}
			}
		} catch (Exception e) {
			log.error("Exception occurred - " + e.getMessage() + ". Below is tracker:\n" + tracker.toString(), e);
			messageService.raiseError("Exception occurred - " + e.getMessage() + ". Below is tracker:\n" + tracker.toString());
		} finally {
			if (connection != null) {
				connection.closeAll();
			}
		}
		return this;
	}
	
	@Inject
	MailConnectionProfileService mailConnectionProfileService;
	
	@Inject
	CategoryService categoryService;

	@Inject
	SubCategoryService subCategoryService;

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
