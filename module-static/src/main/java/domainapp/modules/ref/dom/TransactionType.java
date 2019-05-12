/**
 * 
 */
package domainapp.modules.ref.dom;

import domainapp.modules.base.IEntityEnum;

/**
 * Type or nature of transaction e.g. Credit or Debit
 * 
 * @author Prajapati
 */
public enum TransactionType implements IEntityEnum {

	CREDIT(1),
	DEBIT(2);
	
	private int id;

	/**
	 * Constructor of {@link TransactionType}
	 * 
	 * @param id
	 */
	TransactionType(int id) {
		this.id = id;
	}
	
	/**
	 * @return identifier for this {@link TransactionType}
	 */
	@Override
	public int id() {
		return id;
	}
	
	/**
	 * @param id identifier for which {@link TransactionType} is required
	 * @return {@link TransactionType} corresponding to given id
	 * @throws IllegalArgumentException if no {@link TransactionType} found for given identifier
	 */
	public static TransactionType byId(int id) {
		for (TransactionType type : values()) {
			if (type.id() == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("No TransactionType found for given id - " + id);
	}
	
}
