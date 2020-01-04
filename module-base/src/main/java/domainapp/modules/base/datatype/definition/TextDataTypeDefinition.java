package domainapp.modules.base.datatype.definition;

/**
 * {@link IDataTypeDefinition} implementation for String
 * 
 * @author jayeshecs
 */
public class TextDataTypeDefinition extends BaseDataTypeDefinition<String> {

	@Override
	protected String stringToValue(String value) {
		return value;
	}
	
}