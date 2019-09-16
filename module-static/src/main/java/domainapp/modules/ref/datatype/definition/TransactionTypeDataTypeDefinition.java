package domainapp.modules.ref.datatype.definition;

import domainapp.modules.base.datatype.definition.BaseDataTypeDefinition;
import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.ref.dom.TransactionType;

/**
 * {@link IDataTypeDefinition} implementation for {@link TransactionType}
 * 
 * @author jayeshecs
 */
public class TransactionTypeDataTypeDefinition extends BaseDataTypeDefinition<TransactionType> {

	@Override
	protected TransactionType stringToValue(String value) {
		return TransactionType.valueOf(value);
	}
	
	@Override
	protected String valueToString(TransactionType value) {
		return value.getName();
	}

}
