/**
 * 
 */
package domainapp.modules.rdr.addon.hdfc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;

import domainapp.modules.rdr.addon.AbstractStatementReader;
import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.addon.NoopStatementReaderCallback;
import domainapp.modules.rdr.addon.StatementReaderContext;
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
					context.addTotalCount(1);
					parse(record, transaction, ddMMyy);
				} catch (Exception e) {
					log.error(String.format("Parsing failed for record : %s", record));
					log.error(String.format("Error: %s", e.getMessage() == null ? e.getClass().getName() : e.getMessage()));
					context.addErrorCount(1);
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
		/**
		 *   Date,Narration,Value Dat,Debit Amount,Credit Amount,Chq/Ref Number,Closing Balance
		 */
		Matcher matcher = CSV_REGEX_PATTERN.matcher(data + ",");
		
		matcher.find(); // Date
		String date = sanitizeCsvValue(matcher.group(0));
		record.set(Field.DATE, ddMMyy.parse(date)); // dd/mm/yy
		
		matcher.find(); // narration
		String narration = sanitizeCsvValue(matcher.group(0));
		record.set(Field.NARRATION, narration);
		
		matcher.find(); // Value Date
		matcher.group(0); // skip
		
		matcher.find(); // Debit Amount
		String debitAmountStr = sanitizeCsvValue(matcher.group(0));
		BigDecimal debitAmount = new BigDecimal(debitAmountStr);
		
		matcher.find(); // Debit Amount
		String creditAmountStr = sanitizeCsvValue(matcher.group(0));
		BigDecimal creditAmount = new BigDecimal(creditAmountStr);
		
		if (debitAmount.signum() == 0) {
			record.set(Field.CREDIT, Boolean.TRUE);
			record.set(Field.AMOUNT, creditAmount);
		} else {
			record.set(Field.CREDIT, Boolean.FALSE);
			record.set(Field.AMOUNT, debitAmount);
		}
		
		matcher.find(); // reference
		String reference = sanitizeCsvValue(matcher.group(0));
		record.set(Field.REFERENCE, reference.trim());
	}
	
	public static void main(String[] args) throws IOException {
		
		String statementContent = "\r\n" + 
				"  Date     ,Narration                                                                                                                ,Value Dat,Debit Amount       ,Credit Amount      ,Chq/Ref Number   ,Closing Balance\r\n" + 
				" 03/04/14  ,NEFT CHGS INCL ST & CESS 290314                                                                                          ,03/04/14 ,          5.62     ,          0.00     ,000000000000000        ,    124138.18  \r\n" + 
				" 04/04/14  ,EAW-421424XXXXXX7648-00007359-MUMBAI                                                                                     ,04/04/14 ,       1400.00     ,          0.00     ,0000409405196587       ,    122738.18  \r\n" + 
				" 05/04/14  ,MHDF3308536362/BILLDKVODAFONEINDIAL                                                                                      ,05/04/14 ,        500.00     ,          0.00     ,0000000405154530       ,    122238.18  \r\n" + 
				" 05/04/14  ,MHDF3308543609/BILLDKVODAFONEINDIAL                                                                                      ,05/04/14 ,        700.00     ,          0.00     ,0000000405155258       ,    121538.18  \r\n" + 
				" 05/04/14  ,ATW-421424XXXXXX7648-S1ANBY92-DAHANU BRANCH                                                                              ,06/04/14 ,       4900.00     ,          0.00     ,0000000000007233       ,    116638.18  \r\n" + 
				" 08/04/14  ,FD BOOKED THROUGH NET-50300038407694                                                                                     ,08/04/14 ,       5000.00     ,          0.00     ,000000000000000        ,    111638.18  \r\n" + 
				" 12/04/14  ,CHQ PAID-MICR CTS-MUMBAI CLEAR                                                                                           ,12/04/14 ,      24959.00     ,          0.00     ,0000000000000012       ,     86679.18  \r\n" + 
				" 17/04/14  ,NWD-421424XXXXXX7648-SW002801-MUMBAI                                                                                     ,17/04/14 ,       1400.00     ,          0.00     ,0000000000003859       ,     85279.18  \r\n" + 
				" 18/04/14  ,ATW-421424XXXXXX7648-S1ANBY92-DAHANU BRANCH                                                                              ,18/04/14 ,       2400.00     ,          0.00     ,0000000000009465       ,     82879.18  \r\n" + 
				"";
		
		HDFCBankAccountStatementReader reader = new HDFCBankAccountStatementReader();
		File file = Files.createTempFile("", "HDFC.csv").toFile();
		Files.copy(new ByteArrayInputStream(statementContent.getBytes()), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		Properties config = new Properties();
		IStatementReaderContext context = StatementReaderContext.builder().id(1L).name("HDFC").file(file).config(config).build();

		reader.read(context, new NoopStatementReaderCallback());
		System.out.println(String.format("Stats [Total: %d, Filtered: %d, Error: %d, Skipped: %d]", context.getTotalCount(), context.getFilteredCount(), context.getErrorCount(), context.getSkippedCount()));
	}

}
