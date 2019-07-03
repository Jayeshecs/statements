/**
 * 
 */
package domainapp.modules.rdr.addon.kotak;

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
public class KOTAKBankAccountStatementReader extends AbstractStatementReader {

	@Override
	public void read(IStatementReaderContext context, File inputFile, Properties config, IStatementReaderCallback readerCallback) {
		SimpleDateFormat ddMMyyyy = new SimpleDateFormat(config.getProperty("dateFormat", "dd/MM/yyyy"));
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			
			// skip all lines till (header record)
			while (!reader.readLine().contains("Chq / Ref number")) {
				Thread.yield();
			}
			Collection<IStatementRecord> batch = new ArrayList<>();
			while (reader.ready()) {
				String record = reader.readLine();
				if (record.startsWith("Opening balance") || record.startsWith("Closing balance")) {
					break;
				}
				IStatementRecord transaction = new StatementRecord();
				transaction.set(Field.RAWDATA, record);
				try {
					context.addTotalCount(1);
					parse(record, transaction, ddMMyyyy);
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
	
	private void parse(String data, IStatementRecord record, SimpleDateFormat ddMMyyyy) throws ParseException {
		/**
		 * Sl. No.,Date,Description,Chq / Ref number,Amount,Dr / Cr,Balance,Dr / Cr
		 */
		Matcher matcher = CSV_REGEX_PATTERN.matcher(data + ",");
		matcher.find(); // Sl. No.

		matcher.find(); // Date
		String dateStr = matcher.group(0);
		record.set(Field.DATE, ddMMyyyy.parse(dateStr.toString())); // dd/mm/yyyy

		matcher.find(); // Description
		String narration = matcher.group(0);
		record.set(Field.NARRATION, narration.replaceAll("\"=\"\"", "").replaceAll("\"\"\",", ""));
		
		matcher.find(); // Chq / Ref number
		String reference = matcher.group(0);
		record.set(Field.REFERENCE, reference.replaceAll("\"=\"\"", "").replaceAll("\"\"\",", ""));
		
		matcher.find(); // Amount
		String amountStr = matcher.group(0);
		BigDecimal amount = new BigDecimal(amountStr.trim().replaceAll(",", "").replaceAll("\"", ""));
		record.set(Field.AMOUNT, amount);
		
		matcher.find(); // Dr / Cr
		String creditDebit = matcher.group(0);
		record.set(Field.CREDIT, creditDebit.equalsIgnoreCase("cr") ? Boolean.TRUE : Boolean.FALSE);
	}
	
	public static void main(String[] args) throws IOException {
		
		String statementContent = ",,Account Statement,,,,,\r\n" + 
				"JOHN DOE                                                                        ,,,,,,,\r\n" + 
				"\"=\"\"SMARTSTREAM TECHNOLOGIES INDIA\"\"\",,,,Cust. Reln. No.,\"=\"\"93543954\"\"\",,\r\n" + 
				"\"=\"\"PVT LTD AVER PLAZA 2ND FLOOR\"\"\",,,,Account No.,\"=\"\"6765378901\"\"\",,\r\n" + 
				"\"=\"\"PLOT NO B 13 OFF CIII NEW LINK\"\"\",,,,Period,From 01/04/2018 To 31/03/2019,,\r\n" + 
				"Mumbai,,,,Currency,INR,,\r\n" + 
				"MAHARASHTRA,,,,Branch,MUMBAI - LOKHANDWALA,,\r\n" + 
				"INDIA,,,,Nomination Regd,Y,,\r\n" + 
				"\"=\"\"400053\"\"\",,,,Nominee Name,,,\r\n" + 
				",,,,Joint Holder(S),,,\r\n" + 
				",,,,,,,\r\n" + 
				",,,,,,,\r\n" + 
				"Sl. No.,Date,Description,Chq / Ref number,Amount,Dr / Cr,Balance,Dr / Cr\r\n" + 
				"1,29/03/2019,\"=\"\"MB:to hdfc account\"\"\",\"=\"\"000119638765\"\"\",\"8,506.00\",DR,0.53,CR\r\n" + 
				"2,29/03/2019,\"=\"\"SALARY FOR MARCH 2019\"\"\",\"=\"\"CMS-1903290006MI\"\"\",\"8,506.00\",CR,\"8,506.53\",CR\r\n" + 
				"3,28/02/2019,\"=\"\"MB:to hdfc account\"\"\",\"=\"\"000116343926\"\"\",\"2,929.00\",DR,0.53,CR\r\n" + 
				"4,28/02/2019,\"=\"\"SALARY FOR FEBRUARY 2019\"\"\",\"=\"\"CMS-1902280001TG\"\"\",\"2,929.00\",CR,\"2,929.53\",CR\r\n" + 
				"5,31/01/2019,\"=\"\"MB:to hdfc account\"\"\",\"=\"\"000113303808\"\"\",\"3,887.00\",DR,0.53,CR\r\n" + 
				"6,31/01/2019,\"=\"\"SALARY FOR JANUARY 2019\"\"\",\"=\"\"CMS-19013100038W\"\"\",\"3,714.00\",CR,\"3,887.53\",CR\r\n" + 
				"7,21/01/2019,\"=\"\"ECSICR-HP634449 084404 1701AB-SBI-1275644507 ABPS\"\"\",\"=\"\"\"\"\",173.53,CR,173.53,CR\r\n" + 
				"Opening balance,\"as on 01/04/2018   INR 2,213.88\",,,,,,\r\n" + 
				"Closing balance,as on 31/03/2019   INR 0.53,,,,,,\r\n" + 
				"Call 1800 102 6022 24 Hrs. Toll Free or email at service.bank@kotak.com,,,,,,,\r\n" + 
				"\"Write to us at Customer Contact Centre. Kotak Mahindra Bank Ltd. Post Box Number 16344, Mumbai 400 013\",,,,,,,\r\n" + 
				"";
		
		KOTAKBankAccountStatementReader reader = new KOTAKBankAccountStatementReader();
		File file = Files.createTempFile("", "KOTAK.csv").toFile();
		Files.copy(new ByteArrayInputStream(statementContent.getBytes()), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		Properties config = new Properties();
		IStatementReaderContext context = StatementReaderContext.builder().id(1L).name("KOTAK").file(file).config(config).build();

		reader.read(context, new NoopStatementReaderCallback());
		System.out.println(String.format("Stats [Total: %d, Filtered: %d, Error: %d, Skipped: %d]", context.getTotalCount(), context.getFilteredCount(), context.getErrorCount(), context.getSkippedCount()));
	}

}
