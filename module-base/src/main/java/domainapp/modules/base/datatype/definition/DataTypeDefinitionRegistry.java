package domainapp.modules.base.datatype.definition;

import java.util.HashMap;
import java.util.Map;

import domainapp.modules.base.datatype.DataType;
import domainapp.modules.base.datatype.IDataType;

/**
 * Factory of {@link IDataTypeDefinition} corresponding to each {@link DataType}
 * 
 * @author jayeshecs
 */
public class DataTypeDefinitionRegistry {
	
	public static final DataTypeDefinitionRegistry INSTANCE = new DataTypeDefinitionRegistry();
	
	private Map<IDataType, IDataTypeDefinition<?>> REGISTRY;
	
	private Map<String, IDataType> mapDataType;
	
	private DataTypeDefinitionRegistry() {
		REGISTRY = new HashMap<>();
		mapDataType = new HashMap<>();
	}
	
	/**
	 * Use this API to register {@link IDataTypeDefinition} for given {@link IDataType}
	 * 
	 * @param dataType {@link IDataType}
	 * @param dataTypeDefinition {@link IDataTypeDefinition}
	 */
	public void register(IDataType dataType, IDataTypeDefinition<?> dataTypeDefinition) {
		REGISTRY.put(dataType, dataTypeDefinition);
		mapDataType.put(dataType.getName(), dataType);
	}
	
	/**
	 * @param dataTypeName {@link String}
	 * @return to get {@link IDataType} registered with given dataTypeName
	 */
	public IDataType getDataType(String dataTypeName) {
		return mapDataType.get(dataTypeName);
	}
	
	/**
	 * @param dataTypeName {@link String}
	 * @return to get {@link IDataTypeDefinition} registered with {@link IDataType} corresponding to given dataTypeName
	 */
	public <T> IDataTypeDefinition<T> get(String dataTypeName) {
		IDataType dataType = getDataType(dataTypeName);
		if (dataType == null) {
			return null;
		}
		return get(dataType);
	}
	
	/**
	 * @param dataType {@link IDataType}
	 * @return to get {@link IDataTypeDefinition} registered with given {@link IDataType}
	 */
	@SuppressWarnings("unchecked")
	public <T> IDataTypeDefinition<T> get(IDataType dataType) {
		return (IDataTypeDefinition<T>) REGISTRY.get(dataType);
	}
}