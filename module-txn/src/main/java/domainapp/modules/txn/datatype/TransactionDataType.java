package domainapp.modules.txn.datatype;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.datatype.IDataType;
import domainapp.modules.base.datatype.definition.DataTypeDefinitionRegistry;
import domainapp.modules.txn.datatype.definition.StatementSourceDataTypeDefinition;
import domainapp.modules.txn.datatype.definition.TransactionDataTypeDefinition;

/**
 * Common data type enumeration
 * 
 * @author jayeshecs
 */
public enum TransactionDataType implements IDataType {
	
	STATEMENT_SOURCE,
	TRANSACTION;

	@Override
	public String getName() {
		return name();
	}
	
	public static void registerDefinitions(ServiceRegistry2 registry) {
        DataTypeDefinitionRegistry.INSTANCE.register(TransactionDataType.STATEMENT_SOURCE, registry.injectServicesInto(new StatementSourceDataTypeDefinition()));
        DataTypeDefinitionRegistry.INSTANCE.register(TransactionDataType.TRANSACTION, registry.injectServicesInto(new TransactionDataTypeDefinition()));
	}
}