package domainapp.modules.base.datatype.definition;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * {@link IDataTypeDefinition} implementation for {@link Date}
 * 
 * @author jayeshecs
 */
public class DateDataTypeDefinition extends BaseDataTypeDefinition<Date> {
	
	private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 

	@Override
	protected Date stringToValue(String value) {
		return Date.from(Instant.from(YYYYMMDD.parse(value)));
	}
	
	@Override
	protected String valueToString(Date value) {
		return YYYYMMDD.format(value.toInstant());
	}
}