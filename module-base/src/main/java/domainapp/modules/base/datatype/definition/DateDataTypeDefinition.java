package domainapp.modules.base.datatype.definition;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * {@link IDataTypeDefinition} implementation for {@link Date}
 * 
 * @author jayeshecs
 */
public class DateDataTypeDefinition extends BaseDataTypeDefinition<Date> {
	
	private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("uuuu-MM-dd").withZone(ZoneId.systemDefault());

	@Override
	protected Date stringToValue(String value) {
		// Refer: https://stackoverflow.com/questions/37672012/how-to-create-java-time-instant-from-pattern
		return Date.from(LocalDate.parse(value, YYYYMMDD).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	@Override
	protected String valueToString(Date value) {
		// Refer: https://stackoverflow.com/questions/40211892/unsupported-field-year-when-formatting-an-instant-to-date-iso
		return YYYYMMDD.format(value.toInstant());
	}
}