package domainapp.modules.base.datatype;

import domainapp.modules.base.datatype.definition.DataTypeDefinitionRegistry;
import domainapp.modules.base.datatype.definition.IDataTypeDefinition;

/**
 * Specification for data type
 * 
 * @see IDataTypeDefinition
 * @see DataTypeDefinitionRegistry
 */
public interface IDataType {

	String getName();
}
