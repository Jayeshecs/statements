/**
 * 
 */
package domainapp.modules.ref.dom;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Nature;

import domainapp.modules.base.IEntityEnum;

/**
 * Type or nature of transaction e.g. Credit or Debit
 * 
 * @author Prajapati
 */
@DomainObject(
		nature = Nature.EXTERNAL_ENTITY,
		objectType = "ref.TransactionType"
)
@DomainObjectLayout(named = "Transaction type", plural = "Transaction types", describedAs = "Transaction types e.g. Debit and Credit")
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
	public int getId() {
		return id;
	}
	
	/**
	 * @return name for this {@link TransactionType}
	 */
	@Override
	public String getName() {
		return name();
	}
	
	/**
	 * @param id identifier for which {@link TransactionType} is required
	 * @return {@link TransactionType} corresponding to given id
	 * @throws IllegalArgumentException if no {@link TransactionType} found for given identifier
	 */
	public static TransactionType byId(int id) {
		for (TransactionType type : values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("No TransactionType found for given id - " + id);
	}
	
}
