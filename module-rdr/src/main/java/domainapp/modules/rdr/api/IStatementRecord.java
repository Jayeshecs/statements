/**
 * 
 */
package domainapp.modules.rdr.api;

import domainapp.modules.base.reader.IRecord;
import domainapp.modules.rdr.api.IStatementRecord.Field;

/**
 * @author Prajapati
 *
 */
public interface IStatementRecord extends IRecord<Field> {
	
	public enum Field {
		SOURCE,
		CREDIT,
		DATE,
		AMOUNT,
		NARRATION,
		REFERENCE,
		RAWDATA
		;
	}

}
