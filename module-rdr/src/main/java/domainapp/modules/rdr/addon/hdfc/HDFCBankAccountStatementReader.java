/**
 * 
 */
package domainapp.modules.rdr.addon.hdfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import domainapp.modules.rdr.addon.AbstractStatementReader;
import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.api.IStatementReader;
import domainapp.modules.rdr.api.IStatementReaderCallback;
import domainapp.modules.rdr.api.IStatementRecord;
import domainapp.modules.rdr.api.IStatementRecord.Field;
import domainapp.modules.rdr.api.StatementRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link IStatementReader} implementation for reading HDFC Bank Account statement file having delimited format
 * 
 * @author Prajapati
 */
@Slf4j
public class HDFCBankAccountStatementReader extends AbstractStatementReader {

	@Override
	public void read(IStatementReaderContext context, File inputFile, Properties config, IStatementReaderCallback readerCallback) {
		SimpleDateFormat ddMMyy = new SimpleDateFormat(config.getProperty("dateFormat", "dd/MM/yy"));
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			// skip first line (header record)
			/* String line = */reader.readLine();
			Collection<IStatementRecord> batch = new ArrayList<>();
			reader.readLine(); // skip header line
			while (reader.ready()) {
				String record = reader.readLine();
				IStatementRecord transaction = new StatementRecord();
				transaction.set(Field.RAWDATA, record);
				try {
					parse(record, transaction, ddMMyy);
				} catch (Exception e) {
					log.error(String.format("Parsing failed for record : %s", record));
					log.error(String.format("Error: %s", e.getMessage() == null ? e.getClass().getName() : e.getMessage()));
					continue ;
				}
				batch.add(transaction);
				if (batch.size() > 100) {
					readerCallback.process(context, batch);
					batch = new ArrayList<>();
				}
			}
			
			if (!batch.isEmpty()) {
				readerCallback.process(context, batch);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void parse(String data, IStatementRecord record, SimpleDateFormat ddMMyy) throws ParseException {
		String[] values = data.split(",");
		record.set(Field.DATE, ddMMyy.parse(values[0].toString())); // dd/mm/yy
		record.set(Field.NARRATION, values[1]);
		BigDecimal debitAmount = new BigDecimal(values[3].trim());
		BigDecimal creditAmount = new BigDecimal(values[4].trim());
		if (debitAmount.signum() == 0) {
			record.set(Field.CREDIT, Boolean.TRUE);
			record.set(Field.AMOUNT, creditAmount);
		} else {
			record.set(Field.CREDIT, Boolean.FALSE);
			record.set(Field.AMOUNT, debitAmount);
		}
		record.set(Field.REFERENCE, values[5]);
	}

}
