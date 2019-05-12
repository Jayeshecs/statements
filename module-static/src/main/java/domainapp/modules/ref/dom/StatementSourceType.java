/**
 * 
 */
package domainapp.modules.ref.dom;

import domainapp.modules.base.IEntityEnum;

/**
 * Type or nature of Statement source e.g. Saving account, Credit card, Cash, etc.
 * 
 * @author Prajapati
 */
public enum StatementSourceType implements IEntityEnum {

	SAVING_ACCOUNT(0),
	CREDIT_CARD(1),
	LOAN_ACCOUNT(2),
	CASH(3);
	
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
	public int id() {
		return id;
	}
	
	/**
	 * @param id identifier for which {@link StatementSourceType} is required
	 * @return {@link StatementSourceType} corresponding to given id
	 * @throws IllegalArgumentException if no {@link StatementSourceType} found for given identifier
	 */
	public static StatementSourceType byId(int id) {
		for (StatementSourceType type : values()) {
			if (type.id() == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("No StatementSourceType found for given id - " + id);
	}
	
}
