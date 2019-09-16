package domainapp.modules.base.datatype.definition;

/**
 * {@link IDataTypeDefinition} implementation of number i.e. Integer
 * 
 * @author jayeshecs
 */
public class NumberDataTypeDefinition extends BaseDataTypeDefinition<Integer> {

	@Override
	protected Integer stringToValue(String value) {
		return Integer.parseInt(value);
	}
}