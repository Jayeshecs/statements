package domainapp.modules.ref.datatype.definition;

import domainapp.modules.base.datatype.definition.BaseDataTypeDefinition;
import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.ref.dom.StatementSourceType;

/**
 * {@link IDataTypeDefinition} implementation for {@link StatementSourceType}
 * 
 * @author jayeshecs
 */
public class StatementSourceTypeDataTypeDefinition extends BaseDataTypeDefinition<StatementSourceType> {

	@Override
	protected StatementSourceType stringToValue(String value) {
		return StatementSourceType.valueOf(value);
	}
	
	@Override
	protected String valueToString(StatementSourceType value) {
		return value.getName();
	}

}
