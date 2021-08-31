/**
 * 
 */
package domainapp.modules.rdr.addon.hdfc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import domainapp.modules.rdr.addon.AbstractStatementReader;
import domainapp.modules.rdr.addon.IStatementReaderContext;
import domainapp.modules.rdr.api.IStatementReaderCallback;
import domainapp.modules.rdr.api.IStatementRecord;
import domainapp.modules.rdr.api.IStatementRecord.Field;
import domainapp.modules.rdr.api.StatementRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Prajapati
 *
 */
@Slf4j
public class HDFCCreditcardStatementReader extends AbstractStatementReader {

	@Override
	protected void read(IStatementReaderContext context, File inputFile, Properties config, IStatementReaderCallback readerCallback) {
		String[] passwords = config.getProperty("passwords", "").split(",");
		SimpleDateFormat ddMMyyyy = new SimpleDateFormat(config.getProperty("dateFormat", "dd/MM/yyyy"));
		try (PDDocument document = open(inputFile, passwords);) {
			if (document == null) {
				throw new IllegalStateException(String.format("Failed to open '%s' as PDF file", inputFile));
			}
			/**
			 * Disable all securities
			 */
			document.setAllSecurityToBeRemoved(true);
			/**
			 * Initialize PDFTextStripper 
			 * Initialize StringWriter
			 */
			PDFTextStripper textStripper = new PDFTextStripper();
			StringWriter writer = new StringWriter();
			/**
			 * Extract all text from document
			 */
			textStripper.writeText(document, writer);
//			textStripper.writeText(document, fileWriter);
			String content = writer.getBuffer().toString();
			int indexOfStatementSource = content.indexOf("Card No: ");
			String statementSource = content.substring(indexOfStatementSource, indexOfStatementSource + 29);
			
			/**
			 * Make use of regex to locate transaction
			 */
			String regexTransaction = "(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d.*";
			int indexStartOfTransactions = content.indexOf("Date  Transaction Description Amount");
			log.info("content.indexOf(\"Date  Transaction Description Amount\") = " + indexStartOfTransactions);
			if (indexStartOfTransactions == -1) {
				indexStartOfTransactions = content.indexOf("Date  Transaction Description");
				log.info("content.indexOf(\"Date Transaction Description Amount\") = " + indexStartOfTransactions);
			}
			log.info("content.substring(indexStartOfTransactions).length() = " + content.substring(indexStartOfTransactions).length());
			BufferedReader reader = new BufferedReader(new StringReader(content.substring(indexStartOfTransactions)));
			Collection<IStatementRecord> batch = new ArrayList<>();
			while (reader.ready()) {
				String record = reader.readLine();
				if (record == null) {
					// string reader is empty
					break;
				}
				record = record.trim();
				if (record.matches(regexTransaction)) {
					/**
					 * Parsing logic
					 * DEBIT :> 18/06/2018 IGST-VPS1817063905247-RATE 18.0 -27 (Ref# 09999999980618009935453) 18.00
					 * CREDIT:> 08/07/2018 NETBANKING TRANSFER (Ref# 00000000000708013285133) 37,092.00 Cr
					 */
					IStatementRecord transaction = new StatementRecord();
					transaction.set(Field.SOURCE, statementSource);
					transaction.set(Field.RAWDATA, record);
					try {
						parse(record, transaction, ddMMyyyy);
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
			}
			
			if (!batch.isEmpty()) {
				readerCallback.process(context, batch);
			}
		} catch (IOException e) {
			log.error(String.format("I/O error occurred while opening '%s' as PDF file", inputFile), e);
			throw new IllegalStateException(String.format("I/O error occurred while opening '%s' as PDF file", inputFile));
		}
	}

	private static void parse(String line, IStatementRecord record, SimpleDateFormat ddMMyyyy) throws ParseException {
		Date date = ddMMyyyy.parse(line.substring(0, 10));
		record.set(Field.DATE, date);
		line = line.substring(11);
		record.set(Field.CREDIT, Boolean.FALSE);
		if (line.endsWith("Cr")) {
			record.set(Field.CREDIT, Boolean.TRUE);
			line = line.substring(0, line.length() - " Cr".length());
		}
		/**
		 * 
		 */
		int indexOfAmount = line.lastIndexOf(' ');
		BigDecimal amount = new BigDecimal(line.substring(indexOfAmount).trim().replace(",", ""));
		record.set(Field.AMOUNT, amount);
		record.set(Field.NARRATION, line = line.substring(0, indexOfAmount));
		int indexOfReference = line.indexOf("Ref#");
		if (indexOfReference != -1) {
			record.set(Field.REFERENCE, line.substring(indexOfReference + 5, line.lastIndexOf(')')));
		}
	}

	/**
	 * @param inputFile PDF file that may be password protected
	 * @param passwords 
	 * @return null if given file PDF is password protected
	 * @throws IOException
	 */
	private PDDocument open(File inputFile, String[] passwords) throws IOException {
		/**
		 * First try to open without password
		 */
		try {
			PDDocument document = PDDocument.load(inputFile);
			return document;
		} catch (InvalidPasswordException e) {
			// DO NOTHING
			// password protected
		}
		/**
		 * Second try to open with available passwords
		 */
		for (int i = 0; i < passwords.length; ++i) {
			try {
				PDDocument document = PDDocument.load(inputFile, passwords[i]);
				return document;
			} catch (InvalidPasswordException e) {
				// DO NOTHING
				// failed to open with given password
			}
		}
		/**
		 * Unable to open given file as PDF document with/without passwords
		 */
		return null;
	}

}
