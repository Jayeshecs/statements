package domainapp.modules.base.datatype.definition;

import java.util.List;

/**
 * Specification for definition of generic type T
 * 
 * @author jayeshecs
 * @param <T>
 * @see TextDataTypeDefinition
 * @see DateDataTypeDefinition
 * @see AmountDataTypeDefinition
 * @see NumberDataTypeDefinition
 * @see WithNameDataTypeDefinition
 */
public interface IDataTypeDefinition<T> {
	
	String VALUE_DELIMITER = "#_#";
	
	/**
	 * @param value
	 * @return T parsed from given value
	 */
	T parse(String value);
	
	/**
	 * @param values
	 * @return List of T parsed from given values
	 */
	List<T> parseAsList(String values);
	
	/**
	 * @param values
	 * @return Comma separated values in String format
	 */
	String format(T value);
	
	/**
	 * @param values
	 * @return Comma separated values in String format
	 */
	String format(List<T> values);
	
	/**
	 * @param values
	 * @return Comma separated values in String format
	 */
	String[] formatAsArray(List<T> values);
	
}