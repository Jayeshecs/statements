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
	
	private DataTypeDefinitionRegistry() {
		REGISTRY = new HashMap<>();
	}
	
	/**
	 * Use this API to register {@link IDataTypeDefinition} for given {@link IDataType}
	 * 
	 * @param dataType {@link IDataType}
	 * @param dataTypeDefinition {@link IDataTypeDefinition}
	 */
	public void register(IDataType dataType, IDataTypeDefinition<?> dataTypeDefinition) {
		REGISTRY.put(dataType, dataTypeDefinition);
	}
	
	/**
	 * @param dataType {@link IDataType}
	 * @return to get {@link IDataTypeDefinition} registered with given {@link IDataType}
	 */
	@SuppressWarnings("unchecked")
	public <T> IDataTypeDefinition<T> get(DataType dataType) {
		return (IDataTypeDefinition<T>) REGISTRY.get(dataType);
	}
}