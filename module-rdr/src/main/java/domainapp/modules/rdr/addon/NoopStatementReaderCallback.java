/**
 * 
 */
package domainapp.modules.rdr.addon;

import java.util.Collection;

import domainapp.modules.rdr.api.IStatementReaderCallback;
import domainapp.modules.rdr.api.IStatementRecord;
import domainapp.modules.rdr.api.IStatementRecord.Field;

/**
 * The purpose of this callback is for testing purpose only.
 * 
 * @author Prajapati
 */
public class NoopStatementReaderCallback implements IStatementReaderCallback {

	@Override
	public void process(IStatementReaderContext context, Collection<IStatementRecord> records) {
		for (IStatementRecord record : records) {
			if (record.isFiltered()) {
				context.addFilteredCount(1);
				System.out.println("Filtered: " + record.get(Field.RAWDATA));
				continue;
			}
			System.out.println(String.format("%s,%s,%.2f,%s,%s,%s", record.get(Field.CREDIT), record.get(Field.DATE), record.get(Field.AMOUNT), record.get(Field.NARRATION), record.get(Field.REFERENCE), record.get(Field.RAWDATA)));
		}
	}

}
