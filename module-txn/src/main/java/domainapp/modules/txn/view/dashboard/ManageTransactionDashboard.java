/**
 * 
 */
package domainapp.modules.txn.view.dashboard;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.service.StatementSourceService;
import domainapp.modules.txn.service.TransactionService;

/**
 * @author jayeshecs
 *
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "stmt.ManageTransactionDashboard"
)
public class ManageTransactionDashboard {

	public String title() {
		return "Manage Transactions";
	}
	
	/**
	 * @return 
	 */
	public List<Transaction> getTransactions() {
		return transactionService.all();
	}
	
	/**
	 * @return 
	 */
	public List<StatementSource> getStatementSources() {
		return statementSourceService.all();
	}
	
	@Action(associateWith = "transactions", associateWithSequence = "1", semantics = SemanticsOf.SAFE, typeOf = Transaction.class)
	@ActionLayout(named = "Change Category", position = Position.RIGHT, promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard changeCategory(
			List<Transaction> selectedTransactions, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Category")
			Category category, 
			@Parameter(optionality = Optionality.MANDATORY)
			@ParameterLayout(named = "Sub-Category")
			SubCategory subCategory) {
		for (Transaction transaction : selectedTransactions) {
			transaction.setCategory(category);
			transaction.setSubCategory(subCategory);
		}
		transactionService.save(selectedTransactions);
		return this;
	}
	
	@Action(associateWith = "transactions", associateWithSequence = "2", semantics = SemanticsOf.SAFE, typeOf = Transaction.class)
	@ActionLayout(named = "Add debit", position = Position.RIGHT, promptStyle = PromptStyle.DIALOG)
	public ManageTransactionDashboard debit(
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
		transactionService.debit(statementSource, transactionDate, amount, narration, reference, "manual entry");
		return this;
	}

	@Inject
	TransactionService transactionService;

	@Inject
	StatementSourceService statementSourceService;

}
