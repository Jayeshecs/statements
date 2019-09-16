package domainapp.modules.base.datatype.definition;

import java.math.BigDecimal;

/**
 * {@link IDataTypeDefinition} implementation for Amount i.e. BigDecimal
 * 
 * @author jayeshecs
 */
public class AmountDataTypeDefinition extends BaseDataTypeDefinition<BigDecimal> {

	@Override
	protected BigDecimal stringToValue(String value) {
		return new BigDecimal(value);
	}
	
	@Override
	protected String valueToString(BigDecimal value) {
		return value.toPlainString();
	}
}