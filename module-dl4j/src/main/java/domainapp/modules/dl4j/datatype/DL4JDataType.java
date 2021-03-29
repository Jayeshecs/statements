package domainapp.modules.dl4j.datatype;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import domainapp.modules.base.datatype.IDataType;
import domainapp.modules.base.datatype.definition.DataTypeDefinitionRegistry;

/**
 * DL4J data type enumeration
 * 
 * @author jayeshecs
 */
public enum DL4JDataType implements IDataType {
	
	;

	@Override
	public String getName() {
		return name();
	}
	
	public static void registerDefinitions(ServiceRegistry2 registry) {
		// TODO: [JP] Add registration related to DL4J data types
	}
}
