package domainapp.modules.base.datatype;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.datatype.definition.AmountDataTypeDefinition;
import domainapp.modules.base.datatype.definition.DataTypeDefinitionRegistry;
import domainapp.modules.base.datatype.definition.DateDataTypeDefinition;
import domainapp.modules.base.datatype.definition.NumberDataTypeDefinition;
import domainapp.modules.base.datatype.definition.TextDataTypeDefinition;

/**
 * Common data type enumeration
 * 
 * @author jayeshecs
 */
public enum DataType implements IDataType {
	TEXT,
	DATE,
	AMOUNT,
	NUMBER;

	@Override
	public String getName() {
		return name();
	}
	
	public static void registerDefinitions(ServiceRegistry2 registry) {
        DataTypeDefinitionRegistry.INSTANCE.register(TEXT, new TextDataTypeDefinition());
        DataTypeDefinitionRegistry.INSTANCE.register(DATE, new DateDataTypeDefinition());
        DataTypeDefinitionRegistry.INSTANCE.register(AMOUNT, new AmountDataTypeDefinition());
        DataTypeDefinitionRegistry.INSTANCE.register(NUMBER, new NumberDataTypeDefinition());
	}
}