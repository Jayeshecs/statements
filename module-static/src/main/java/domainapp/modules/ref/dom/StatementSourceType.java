/**
 * 
 */
package domainapp.modules.ref.dom;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Nature;

import com.google.gson.annotations.SerializedName;

import domainapp.modules.base.IEntityEnum;

/**
 * Type or nature of Statement source e.g. Saving account, Credit card, Cash, etc.
 * 
 * @author Prajapati
 */
@DomainObject(
		nature = Nature.INMEMORY_ENTITY,
		objectType = "ref.StatementSourceType",
		bounded = true
)
@DomainObjectLayout(
		named = "Statement source type", 
		plural = "Statement source types", 
		describedAs = "Statement source types e.g. saving account, load account, credit card, etc.")
public enum StatementSourceType implements IEntityEnum {

	@SerializedName("0")
	SAVING_ACCOUNT(0),
	@SerializedName("1")
	CREDIT_CARD(1),
	@SerializedName("2")
	LOAN_ACCOUNT(2),
	@SerializedName("3")
	E_WALLET(3),
	@SerializedName("4")
	CASH(4);
	
	private int id;

	/**
	 * Constructor of {@link StatementSourceType}
	 * 
	 * @param id
	 */
	StatementSourceType(int id) {
		this.id = id;
	}
	
	/**
	 * @return identifier for this {@link StatementSourceType}
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return name for this {@link StatementSourceType}
	 */
	@Override
	public String getName() {
		return name();
	}
	
	/**
	 * @param id identifier for which {@link StatementSourceType} is required
	 * @return {@link StatementSourceType} corresponding to given id
	 * @throws IllegalArgumentException if no {@link StatementSourceType} found for given identifier
	 */
	public static StatementSourceType byId(int id) {
		for (StatementSourceType type : values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("No StatementSourceType found for given id - " + id);
	}
	
}
