/**
 * 
 */
package domainapp.modules.base.datatype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domainapp.modules.base.datatype.definition.DataTypeDefinitionRegistry;
import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.base.view.Value;

/**
 * @author jayeshecs
 *
 */
public class DataTypeUtil {

	/**
	 * @param map
	 * @return
	 */
	public static Map<String, Object> toMapOfObject(Map<String, Value> map) {
		Map<String, Object> result = new HashMap<>();
		map.entrySet().forEach(entry -> {
			result.put(entry.getKey(), valueToObject(entry.getValue()));
		});
		return result;
	}

	public static Object valueToObject(Value value) {
		IDataTypeDefinition<Object> definition = DataTypeDefinitionRegistry.INSTANCE.get(value.getDataType());
		return definition.parse(
				(value.isList() 
						? toDelimitedValue(value.getList()) 
								: value.getValue()));
	}

	/**
	 * @param list
	 * @return
	 */
	private static String toDelimitedValue(List<String> list) {
		StringBuilder sb = new StringBuilder();
		list.forEach(val -> {
			sb.append(val).append(IDataTypeDefinition.VALUE_DELIMITER);
		});
		return sb.substring(0, sb.length() - IDataTypeDefinition.VALUE_DELIMITER.length());
	}
	
	/**
	 * @param dataType
	 * @param values
	 * @return
	 */
	public static <T> Value createValue(IDataType dataType, List<T> values) {
		IDataTypeDefinition<T> dataTypeDefinition = DataTypeDefinitionRegistry.INSTANCE.get(dataType);
		return new Value(dataType.getName(), dataTypeDefinition.format(values));
	}
}
