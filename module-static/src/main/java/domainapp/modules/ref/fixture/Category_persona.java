/**
 * 
 */
package domainapp.modules.ref.fixture;

import java.util.List;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.fixturescripts.setup.PersonaEnumPersistAll;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.service.CategoryService;
import lombok.AllArgsConstructor;

/**
 * @author Prajapati
 *
 */
@AllArgsConstructor
public enum Category_persona implements PersonaWithBuilderScript<Category, CategoryBuilder>, PersonaWithFinder<Category> {
	
	AC_TO_AC("A/C to A/C"),
	CREDIT_DEBIT_WAIVER("Credit/Debit Waiver"),
	EMI("EMI"),
	LOAN("Loan"),
	FOOD("Food"),
	MEDICAL("Medical"),
	TRAVEL("Travel"),
	ENTERTAINMENT("Entertainment"),
	BILL_PAYMENT("Utilities Bill"),
	INCOME("Income"),
	CASH("Cash"),
	EDUCATION("Education"),
	INSURANCE("Insurance"),
	INVESTMENT("Investment"),
	MAINTENANCE("Maintenance"),
	MEMBERSHIP("Membership"),
	MISCELLANEOUS("Miscellaneous"),
	SHOPPING("Shopping"),
	;

	private final String name;

	public CategoryBuilder builder() {
	    return new CategoryBuilder().setName(name);
	}

	@Override
	public Category findUsing(ServiceRegistry2 serviceRegistry) {
		CategoryService categoryService = serviceRegistry.lookupService(CategoryService.class);
		List<Category> list = categoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		return list == null || list.isEmpty() ? null : list.iterator().next();
	}
	
	public static class PersistAll
	        extends PersonaEnumPersistAll<Category_persona, Category, CategoryBuilder> {
	    public PersistAll() {
	        super(Category_persona.class);
	    }
	}
}
