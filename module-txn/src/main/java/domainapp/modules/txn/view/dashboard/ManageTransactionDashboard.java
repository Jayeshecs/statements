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
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import org.apache.isis.applib.ViewModel;
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
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.hint.HintStore;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import domainapp.modules.addon.service.AddonService;
import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithDescription;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.service.OrderBy;
import domainapp.modules.base.service.SessionStoreFactory;
import domainapp.modules.base.view.GenericFilter;
import domainapp.modules.base.view.Value;
import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.addon.StatementReaderContext;
import domainapp.modules.rdr.api.IStatementReader;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.ref.datatype.StaticDataDataType;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.StatementSourceType;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.ref.service.CategoryService;
import domainapp.modules.ref.service.SubCategoryService;
import domainapp.modules.txn.datatype.TransactionDataType;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.dom.Transaction.FieldConstants;
import domainapp.modules.txn.rdr.TransactionReaderCallback;
import domainapp.modules.txn.service.StatementSourceService;
import domainapp.modules.txn.service.TransactionService;
import lombok.Getter;
import lombok.Setter;
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
public class ManageTransactionDashboard implements HintStore.HintIdProvider, ViewModel {

	private static final String PARAM_TRANSACTION_TYPE = "type";
	
	private static final String PARAM_SUB_CATEGORY = "subCategory";

	private static final String PARAM_CATEGORY = "category";

	private static final String PARAM_STATEMENT_SOURCE = "statementSource";

	private static final String SESSION_ATTRIBUTE_FILTER = "filter";

	private static final String USER_INPUT_UNCATEGORIZED = "uncategorized";

	private static final String USER_INPUT_AMOUNT_CAP = "amountCap";

	private static final String USER_INPUT_AMOUNT_FLOOR = "amountFloor";

	private static final String USER_INPUT_DATE_END = "dateEnd";

	private static final String USER_INPUT_DATE_START = "dateStart";

	@PropertyLayout(hidden = Where.EVERYWHERE)
	@Getter @Setter
	protected GenericFilter filter;
	
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
	private GenericFilter defaultFilter() {
		GenericFilter filter = new GenericFilter();
		filter.setFilter(FieldConstants.CATEGORY + " == null && " + FieldConstants.SUB_CATEGORY + " == null");
		filter.setExclude(new HashSet<String>(Arrays.asList("uncategorized")));
		return filter;
	}

	/**
	 * @return
	 */
	@Programmatic
	private String filterToJson() {
		if (getFilter() == null) {
			setFilter(defaultFilter());
		}
		String json = createGsonBuilder().toJson(getFilter());
		return Base64.getUrlEncoder().encodeToString(json.getBytes());
	}

	/**
	 * @return
	 */
	private Gson createGsonBuilder() {
		GsonBuilder builder = new GsonBuilder();
//		builder.setPrettyPrinting();
		return builder.create();
	}

	/**
	 * @param jsonUrlEncoded
	 * @return 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Programmatic
	private GenericFilter jsonToFilter(String jsonUrlEncoded) {
		String json = new String(Base64.getUrlDecoder().decode(jsonUrlEncoded.getBytes()));
		GenericFilter genericFilter = createGsonBuilder().fromJson(json, GenericFilter.class);
		Map<String, Value> parameters = genericFilter.getParameters();
		if (parameters.containsKey(PARAM_TRANSACTION_TYPE)) {
			Value value = null;
			Object sourceObj = parameters.get(PARAM_TRANSACTION_TYPE);
			if (sourceObj instanceof Value) {
				value = (Value) sourceObj;
			}
			if (value == null) { // this means sourceObj is not an instance of Value so we need to create new Value instance
				value = DataTypeUtil.createValue(StaticDataDataType.TRANSACTION_TYPE, Arrays.asList(TransactionType.valueOf(String.valueOf(sourceObj))));
			}
			parameters.put(PARAM_TRANSACTION_TYPE, value);
		}
		if (parameters.containsKey(PARAM_STATEMENT_SOURCE)) {
			Object sourceObj = parameters.get(PARAM_STATEMENT_SOURCE);
			if (sourceObj instanceof List) {
				List<String> statementSourceList = new ArrayList<>();
				((List<Map<String, Object>>)sourceObj).stream().forEach(m -> {
					String sourceName = (String) ((Map)m).get(WithName.FIELD_NAME);
					List<StatementSource> list = statementSourceService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, sourceName);
					Optional<StatementSource> result = list.stream().filter(t -> {
						return t.getName().equals(sourceName);
					}).findFirst();
					if (result.isPresent()) {
						statementSourceList.add(result.get().getName());
					}
				});
				parameters.remove(PARAM_STATEMENT_SOURCE);
				if (!statementSourceList.isEmpty()) {
					parameters.put(PARAM_STATEMENT_SOURCE, DataTypeUtil.createValue(TransactionDataType.STATEMENT_SOURCE, statementSourceList));
				}
			}
		}
		if (parameters.containsKey(PARAM_CATEGORY)) {
			Object sourceObj = parameters.get(PARAM_CATEGORY);
			if (sourceObj instanceof List) {
				List<String> categoryList = new ArrayList<>();
				((List<Map<String, Object>>)sourceObj).stream().forEach(m -> {
					String categoryName = (String) ((Map)m).get(WithName.FIELD_NAME);
					List<Category> list = categoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, categoryName);
					Optional<Category> result = list.stream().filter(t -> {
						return t.getName().equals(categoryName);
					}).findFirst();
					if (result.isPresent()) {
						categoryList.add(result.get().getName());
					}
				});
				parameters.remove(PARAM_CATEGORY);
				if (!categoryList.isEmpty()) {
					parameters.put(PARAM_CATEGORY, DataTypeUtil.createValue(StaticDataDataType.CATEGORY, categoryList));
				}
			}
			if (sourceObj instanceof Map) {
				String sourceName = (String) ((Map)sourceObj).get(WithName.FIELD_NAME);
				List<Category> list = categoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, sourceName);
				Optional<Category> result = list.stream().filter(t -> {
					return t.getName().equals(sourceName);
				}).findFirst();
				parameters.remove(PARAM_CATEGORY);
				if (result.isPresent()) {
					parameters.put(PARAM_CATEGORY, DataTypeUtil.createValue(StaticDataDataType.CATEGORY, Arrays.asList(result.get())));
				}
			}
		}
		if (parameters.containsKey(PARAM_SUB_CATEGORY)) {
			Object sourceObj = parameters.get(PARAM_SUB_CATEGORY);
			if (sourceObj instanceof List) {
				List<String> subCategoryList = new ArrayList<>();
				((List<Map<String, Object>>)sourceObj).stream().forEach(m -> {
					String subCategoryName = (String) ((Map)m).get(WithName.FIELD_NAME);
					List<SubCategory> list = subCategoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, subCategoryName);
					Optional<SubCategory> result = list.stream().filter(t -> {
						return t.getName().equals(subCategoryName);
					}).findFirst();
					if (result.isPresent()) {
						subCategoryList.add(result.get().getName());
					}
				});
				parameters.remove(PARAM_SUB_CATEGORY);
				if (!subCategoryList.isEmpty()) {
					parameters.put(PARAM_SUB_CATEGORY, DataTypeUtil.createValue(StaticDataDataType.SUB_CATEGORY, subCategoryList));
				}
			}
			if (sourceObj instanceof Map) {
				String sourceName = (String) ((Map)sourceObj).get(WithName.FIELD_NAME);
				List<SubCategory> list = subCategoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, sourceName);
				Optional<SubCategory> result = list.stream().filter(t -> {
					return t.getName().equals(sourceName);
				}).findFirst();
				parameters.remove(PARAM_SUB_CATEGORY);
				if (result.isPresent()) {
					parameters.put(PARAM_SUB_CATEGORY, DataTypeUtil.createValue(StaticDataDataType.SUB_CATEGORY, Arrays.asList(result.get())));
				}
			}
		}
		return genericFilter;
	}
	
	@Override
	public String hintId() {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(SESSION_ATTRIBUTE_FILTER);
		if (filter != null) {
			setFilter(jsonToFilter(filter));
			return filter;
		}
		return filterToJson();
	}

	@Override
	public String viewModelMemento() {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(SESSION_ATTRIBUTE_FILTER);
		if (filter != null) {
			return filter;
		}
		String json = filterToJson();
		return json;
	}

	@Override
	public void viewModelInit(String memento) {
		String filter = SessionStoreFactory.INSTANCE.getSessionStore().get(SESSION_ATTRIBUTE_FILTER);
		if (filter == null) {
			filter = memento;
		}
		setFilter(jsonToFilter(filter));
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
			filterDescription = filterDescription.replaceAll(":" + entry.getKey(), entry.getValue().getValue());
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
			orderBy.add("transactionDate", true);
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
		GenericFilter filter = new GenericFilter();
		filter.setExclude(new HashSet<String>(Arrays.asList("uncategorized")));
		Map<String, Value> parameters = filter.getParameters();
		filter.setFilter(transactionService.buildFilter(null, null, null, null, null, null, null, null, null, Boolean.FALSE, parameters));
		setFilter(filter);
		SessionStoreFactory.INSTANCE.getSessionStore().set(SESSION_ATTRIBUTE_FILTER, filterToJson());
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
		SessionStoreFactory.INSTANCE.getSessionStore().set(SESSION_ATTRIBUTE_FILTER, filterToJson());
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
	public ManageTransactionDashboard internalFilter(String narration, List<TransactionType> transactionTypes, List<StatementSource> source, Date dateStart,
			Date dateEnd, BigDecimal amountFloor, BigDecimal amountCap, List<Category> categories, List<SubCategory> subCategories,
			Boolean uncategorized) {
		GenericFilter filter = new GenericFilter();
		filter.setExclude(new HashSet<String>(Arrays.asList("uncategorized")));
		Map<String, Value> parameters = filter.getParameters();
		filter.setFilter(transactionService.buildFilter(narration, dateStart, dateEnd, amountFloor, amountCap, transactionTypes, source, categories, subCategories, uncategorized, parameters));
		setFilter(filter);
		return this;
	}

	/**
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T getUserInputValue(String key) {
		GenericFilter filter = getFilter();
		if (filter != null) {
			Value value = filter.getParameters().get(key);
			if (value != null) {
				return (T) DataTypeUtil.valueToObject(value);
			}
		}
		return null;
	}
	
	public String default0Filter() {
		return getUserInputValue(FieldConstants.NARRATION);
	}
	
	public List<TransactionType> default1Filter() {
		return getUserInputValue(FieldConstants.TYPE);
	}

	/**
	 * @param name
	 * @param list
	 */
	private <T extends WithName> T matchingExactName(final String name, List<T> list) {
		if (list != null && !list.isEmpty()) {
			Optional<T> result = list.stream().filter(t -> {
				return t.getName().equals(name);
			}).findFirst();
			if (result.isPresent()) {
				return result.get();
			}
		}
		return null;
	}
	
	public List<StatementSource> default2Filter() {
		String name = getUserInputValue(FieldConstants.SOURCE);
		if (name == null) {
			return null;
		}
		List<StatementSource> result = new ArrayList<StatementSource>();
		for (String source : name.split(";")) {
			if (source.trim().length() == 0) {
				continue ;
			}
			List<StatementSource> list = statementSourceService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, source);
			StatementSource statementSource = matchingExactName(source, list);
			if (statementSource != null) {
				result.add(statementSource);
			}
		}
		return result;
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
		return getUserInputValue(USER_INPUT_DATE_START);
	}
	
	public Date default4Filter() {
		return getUserInputValue(USER_INPUT_DATE_END);
	}
	
	public BigDecimal default5Filter() {
		Number userInputValue = getUserInputValue(USER_INPUT_AMOUNT_FLOOR);
		if (userInputValue == null) {
			return null;
		}
		return new BigDecimal(String.valueOf(userInputValue));
	}
	
	public BigDecimal default6Filter() {
		Number userInputValue = getUserInputValue(USER_INPUT_AMOUNT_CAP);
		if (userInputValue == null) {
			return null;
		}
		return new BigDecimal(String.valueOf(userInputValue));
	}
	
	public List<Category> default7Filter() {
		String name = getUserInputValue(FieldConstants.CATEGORY);
		if (name == null) {
			return null;
		}
		List<Category> result = new ArrayList<>();
		for (String source : name.split(";")) {
			if (source.trim().length() == 0) {
				continue ;
			}
			List<Category> list = categoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, source);
			Category category = matchingExactName(source, list);
			if (category != null) {
				result.add(category);
			}
		}
		return result;
	}
	
	public List<SubCategory> default8Filter() {
		String name = getUserInputValue(FieldConstants.SUB_CATEGORY);
		if (name == null) {
			return null;
		}
		List<SubCategory> result = new ArrayList<>();
		for (String source : name.split(";")) {
			if (source.trim().length() == 0) {
				continue ;
			}
			List<SubCategory> list = subCategoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, source);
			SubCategory subCategory = matchingExactName(source, list);
			if (subCategory != null) {
				result.add(subCategory);
			}
		}
		return result;
	}
	
	public Boolean default9Filter() {
		Boolean uncategorized = getUserInputValue(USER_INPUT_UNCATEGORIZED);
		return uncategorized == null ? Boolean.TRUE : uncategorized;
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
