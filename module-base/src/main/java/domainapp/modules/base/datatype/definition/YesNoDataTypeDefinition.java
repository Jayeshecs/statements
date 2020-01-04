package domainapp.modules.base.datatype.definition;

/**
 * {@link IDataTypeDefinition} implementation of number i.e. Integer
 * 
 * @author jayeshecs
 */
public class YesNoDataTypeDefinition extends BaseDataTypeDefinition<Boolean> {

	@Override
	protected Boolean stringToValue(String value) {
		return Boolean.parseBoolean(value);
	}
}