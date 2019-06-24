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
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.service.SubCategoryService;
import lombok.AllArgsConstructor;

/**
 * @author Prajapati
 *
 */
@AllArgsConstructor
public enum SubCategory_persona implements PersonaWithBuilderScript<SubCategory, SubCategoryBuilder>, PersonaWithFinder<SubCategory> {
	
	// A/C to A/C
	TRANSFER("Transfer"),
	// Credit/Debut Waiver
	FUEL("Fuel"),
	BANK_FEE("Bank Fees"),
	INTEREST("Interest"),
	
	// Loan
	HOME_LOAN("Home Loan"),
	CAR_LOAN("Car Loan"),
	CREDITCARD_LOAN("Creditcard Loan"),
	FINANCE_LOAN("Finance Loan"),
	OTHER_LOAN("Other Loan"),
	
	// Food
	COFFEE("Coffee"),
	SNACKS("Snacks"),
	DINNING("Dinning"),
	GROSSORIES("Grossories"),
	
	// Medical
	MEDICINE("Medicine"),
	VACCINE("Vaccine"),
	PERSONAL_HIGENE("Personal Higene"),
	
	// Travel
	//FUEL("Fuel"),
	TAXI_CAB("Taxi/Cab"),
	BUS("Bus"),
	AUTO("Auto"),
	TRAIN("Train"),
	FLIGHT("Flight"),
	OTHER_TRAVEL("Other Travel"),
	HOTEL("Hotel"),
	PARKING("Parking"),
	
	// Entertainment
	MOVIE("Movie"),
	THEME_PARK("Theme Park"),
	GIFT("Gift"),
	PUB_NIGHTCLUB("Pub/Nighclub"),
	
	// BILL PAYMENTS
	CREDITCARD("Creditcard"),
	MOBILE("Mobile"),
	INTERNET("Internet"),
	ELECTRICITY("Electricity"),
	MILK("Milk"),
	GAS("Gas"),
	
	// Income
	SALARY("Salary"),
	//INTEREST("Interest"),
	DIVIDEND("Dividend"),
	POLICY_MATURITY("Policy Maturity"),
	SURRENDER_FUND("Surrender Fund"),
	INCOME_TAX_CLAIM("Income Tax Claim"),
	
	// Cash
	ATM("ATM"),
	
	// Education
	SCHOOL_FEES("School Fees"),
	SCHOOL_BOOKS("School Books"),
	SCHOOL_UNIFORMS("School Uniforms"),
	SCHOOL_ACTIVITIES("School Activities"),
	
	// Insurance
	LIC_PREMIUM("Life Insurance Premium"),
	CAR_PREMIUM("Car Insurance Premium"),
	HOME_PREMIUM("Home Insurance Premium"),
	TERM_PREMIUM("Term Insurance Premium"),
	OTHER_PREMIUM("Other Insurance Premium"),
	
	// Investment
	RECURRING_DEPOSITES("Recurring Deposites"),
	FIXED_DEPOSITES("Fixed Deposites"),
	MUTUAL_FUND("Mutual Fund"),
	STOCK_MARKET("Stock Market"),
	PROPERTY("Property"),
	CRYPTO_CURRENCY("Crypto Currency"),
	GOLD("Gold"),
	
	// Maintenance
	PROPERTY_TAX("Property Tax"),
	HOME_MAINTENANCE("Home Maintenance"),
	CAR_MAINTENANCE("Car Maintenance"),
	
	// Membership
	HOLIDAY("Holiday"),
	
	// Miscellaneious
	MISCELLANEOUS("Miscellaneous"),
	
	// Shopping
	CLOTHES("Clothers"),
	TOYS("Toys"),
	ELECTRONICS("Electronics"),
	JWELLERIES("Jwelleries"),
	;

	private final String name;

	public SubCategoryBuilder builder() {
	    return new SubCategoryBuilder().setName(name);
	}

	@Override
	public SubCategory findUsing(ServiceRegistry2 serviceRegistry) {
		SubCategoryService subCategoryService = serviceRegistry.lookupService(SubCategoryService.class);
		List<SubCategory> list = subCategoryService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
		return list == null || list.isEmpty() ? null : list.iterator().next();
	}
	
	public static class PersistAll
	        extends PersonaEnumPersistAll<SubCategory_persona, SubCategory, SubCategoryBuilder> {
	    public PersistAll() {
	        super(SubCategory_persona.class);
	    }
	}
}
