package domainapp.modules.base.datatype.definition;

import java.util.ArrayList;
import java.util.List;

import domainapp.modules.base.datatype.DataType;

/**
 * Base implementation of {@link IDataTypeDefinition} for common {@link DataType}
 * @author jayeshecs
 *
 * @param <T>
 * @see TextDataTypeDefinition
 * @see DateDataTypeDefinition
 * @see AmountDataTypeDefinition
 * @see NumberDataTypeDefinition
 */
public abstract class BaseDataTypeDefinition<T> implements IDataTypeDefinition<T> {

	@Override
	public List<T> parse(String values) {
		if (values == null) {
			return null;
		}
		List<T> result = new ArrayList<>();
		for (String value : values.split(VALUE_DELIMITER)) {
			result.add(stringToValue(value));
		}
		return result;
	}

	/**
	 * The purpose of this method is to get instance of <T> based on given String value
	 * 
	 * @param value
	 * @return <T>
	 */
	protected abstract T stringToValue(String value);

	@Override
	public String format(List<T> values) {
		if (values == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		values.forEach(value -> {
			result.append(valueToString(value)).append(VALUE_DELIMITER);
		});
		result.setLength(result.length() - VALUE_DELIMITER.length());
		return result.toString();
	}

	/**
	 * By default it takes the result of toString() however it is recommended to override this method in sub-class
	 * 
	 * @param value
	 * @return String representation of given value
	 */
	protected String valueToString(T value) {
		return String.valueOf(value);
	}
}