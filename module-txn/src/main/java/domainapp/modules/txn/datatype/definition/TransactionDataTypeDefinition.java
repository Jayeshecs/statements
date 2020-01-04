package domainapp.modules.txn.datatype.definition;

import javax.inject.Inject;

import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.adapter.oid.Oid;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;

import domainapp.modules.base.datatype.definition.BaseDataTypeDefinition;
import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.txn.dom.Transaction;

/**
 * {@link IDataTypeDefinition} implementation for {@link Transaction}
 * 
 * @author jayeshecs
 */
public class TransactionDataTypeDefinition extends BaseDataTypeDefinition<Transaction> {

	@Override
	protected Transaction stringToValue(String value) {
		Oid oid = RootOid.deStringEncoded(value);
		return (Transaction) adapterManager.getAdapterFor(oid);
	}
	
	@Override
	protected String valueToString(Transaction value) {
		return adapterManager.getAdapterFor(value).getOid().enString();
	}

	@Inject
	AdapterManager adapterManager;
}
