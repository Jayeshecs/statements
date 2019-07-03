/**
 * 
 */
package domainapp.modules.rdr.addon.paytm;

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
public class PayTMWalletStatementReader extends AbstractStatementReader {

	@Override
	public void read(IStatementReaderContext context, File inputFile, Properties config, IStatementReaderCallback readerCallback) {
		SimpleDateFormat ddMMyy = new SimpleDateFormat(config.getProperty("dateFormat", "dd/MM/yyyy HH:mm:ss"));
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			// skip first line (header record)
			/* String line = */reader.readLine();
			Collection<IStatementRecord> batch = new ArrayList<>();
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
		 * "Date","Activity","Source/Destination","Wallet Txn ID","Comment","Debit","Credit","Transaction Breakup","Status"
		 */
		Matcher matcher = CSV_REGEX_PATTERN.matcher(data + ",");
		
		matcher.find(); // Date
		String date = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Activity
		String activity = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Source/Destination
		String sourceDestination = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Wallet Txn ID
		String walletTxnId = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Comment
		String comment = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Debit
		String debitAmountStr = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Credit
		String creditAmountStr = sanitizeCsvValue(matcher.group(0));
		
		matcher.find(); // Transaction Breakup
		matcher.group(0); // skip
		
		matcher.find(); // Status
		String status = sanitizeCsvValue(matcher.group(0));
		
		if (status.equals("REFUNDED_BACK") || activity.equals("On Hold For Order") || activity.equals("Refunded Back")) {
			record.markAsFiltered();
		}
		
		record.set(Field.DATE, ddMMyy.parse(date)); // dd/mm/yy
		record.set(Field.NARRATION, sourceDestination);
		BigDecimal debitAmount = new BigDecimal(debitAmountStr.isEmpty() ? "0" : debitAmountStr);
		BigDecimal creditAmount = new BigDecimal(creditAmountStr.isEmpty() ? "0" : creditAmountStr);
		
		if (debitAmount.signum() == 0) {
			record.set(Field.CREDIT, Boolean.TRUE);
			record.set(Field.AMOUNT, creditAmount);
		} else {
			record.set(Field.CREDIT, Boolean.FALSE);
			record.set(Field.AMOUNT, debitAmount);
		}

		record.set(Field.REFERENCE, walletTxnId + (comment.isEmpty() ? "" : (' ' + comment)));
	}
	
	public static void main(String[] args) throws IOException {
		
		String statementContent = "\"Date\",\"Activity\",\"Source/Destination\",\"Wallet Txn ID\",\"Comment\",\"Debit\",\"Credit\",\"Transaction Breakup\",\"Status\"\r\n" + 
				"\"29/06/2019 13:59:30\",\"Paid For Order\",\"Zomato Order #ZTD-19-2CC4CC6865733\",\"25207881135\",\"\",\"223.50\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"20/06/2019 14:32:44\",\"Added To Paytm Account\",\"Paytm Order #8547068237\",\"25071560945\",\"\",\"\",\"2000\",\"\",\"SUCCESS\"\r\n" + 
				"\"20/06/2019 07:14:03\",\"Paid For Order\",\"Radha Krishna Dairy Order #201906200714030022\",\"25065427656\",\"\",\"75\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"19/06/2019 09:40:53\",\"Paid For Order\",\"Paytm Order #8536253463\",\"25051986786\",\"\",\"500\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"19/06/2019 09:39:49\",\"Added To Paytm Account\",\"Paytm Order #8536237331\",\"25051974379\",\"\",\"\",\"1000\",\"\",\"SUCCESS\"\r\n" + 
				"\"16/06/2019 21:24:43\",\"Paid For Order\",\"Zomato Order #ZTD-19-F71496D5EC995\",\"25019419416\",\"\",\"201.50\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"16/06/2019 13:35:11\",\"Paid For Order\",\"Zomato Order #ZTD-19-AFDA2823E54A3\",\"25012419723\",\"\",\"144.75\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"13/06/2019 17:57:06\",\"Money Sent\",\"Order #SM_F4FE4F0EBF980F90\",\"24971025371\",\"From: JAYESH MANILAL PRAJAPATI To: Ansel Tixeira\",\"432\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"14/07/2018 13:26:46\",\"Paid For Order\",\"Zomato Order #ZTD182EF26918DF54233\",\"19679168584\",\"\",\"560\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"14/07/2018 13:26:46\",\"Refunded Back\",\"Order #ZTD182EF26918DF54233\",\"19679168579\",\"\",\"\",\"560\",\"\",\"SUCCESS\"\r\n" + 
				"\"14/07/2018 13:26:45\",\"On Hold For Order\",\"Order #ZTD-18-E6F97C291B769\",\"19679168538\",\"\",\"560\",\"\",\"\",\"REFUNDED_BACK\"\r\n" + 
				"\"14/07/2018 13:25:04\",\"Paid For Order\",\"Zomato Order #ZTD18D48F34E99C5E51E\",\"19679146368\",\"\",\"311.45\",\"\",\"\",\"SUCCESS\"\r\n" + 
				"\"14/07/2018 13:25:04\",\"Refunded Back\",\"Order #ZTD18D48F34E99C5E51E\",\"19679146365\",\"\",\"\",\"311.45\",\"\",\"SUCCESS\"\r\n" + 
				"\"14/07/2018 13:25:03\",\"On Hold For Order\",\"Order #ZTD-18-F5A40AAE3D885\",\"19679146295\",\"\",\"311.45\",\"\",\"\",\"REFUNDED_BACK\"\r\n" + 
				"";
		
		PayTMWalletStatementReader reader = new PayTMWalletStatementReader();
		File file = Files.createTempFile("", "PAYTM.csv").toFile();
		Files.copy(new ByteArrayInputStream(statementContent.getBytes()), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		Properties config = new Properties();
		IStatementReaderContext context = StatementReaderContext.builder().id(1L).name("PAYTM").file(file).config(config).build();

		reader.read(context, new NoopStatementReaderCallback());
		System.out.println(String.format("Stats [Total: %d, Filtered: %d, Error: %d, Skipped: %d]", context.getTotalCount(), context.getFilteredCount(), context.getErrorCount(), context.getSkippedCount()));
	}

}
