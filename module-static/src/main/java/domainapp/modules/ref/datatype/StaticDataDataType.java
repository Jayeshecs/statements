package domainapp.modules.ref.datatype;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.datatype.IDataType;
import domainapp.modules.base.datatype.definition.DataTypeDefinitionRegistry;
import domainapp.modules.ref.datatype.definition.CategoryDataTypeDefinition;
import domainapp.modules.ref.datatype.definition.StatementSourceTypeDataTypeDefinition;
import domainapp.modules.ref.datatype.definition.SubCategoryDataTypeDefinition;
import domainapp.modules.ref.datatype.definition.TransactionTypeDataTypeDefinition;

/**
 * Common data type enumeration
 * 
 * @author jayeshecs
 */
public enum StaticDataDataType implements IDataType {
	
	TRANSACTION_TYPE,
	CATEGORY,
	SUB_CATEGORY,
	STATEMENT_SOURCE_TYPE;

	@Override
	public String getName() {
		return name();
	}
	
	public static void registerDefinitions(ServiceRegistry2 registry) {
        DataTypeDefinitionRegistry.INSTANCE.register(StaticDataDataType.TRANSACTION_TYPE, registry.injectServicesInto(new TransactionTypeDataTypeDefinition()));
        DataTypeDefinitionRegistry.INSTANCE.register(StaticDataDataType.STATEMENT_SOURCE_TYPE, registry.injectServicesInto(new StatementSourceTypeDataTypeDefinition()));
        DataTypeDefinitionRegistry.INSTANCE.register(StaticDataDataType.CATEGORY, registry.injectServicesInto(new CategoryDataTypeDefinition()));
        DataTypeDefinitionRegistry.INSTANCE.register(StaticDataDataType.SUB_CATEGORY, registry.injectServicesInto(new SubCategoryDataTypeDefinition()));
	}
}