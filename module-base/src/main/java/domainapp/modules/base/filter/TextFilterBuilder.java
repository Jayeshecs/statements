/**
 * 
 */
package domainapp.modules.base.filter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domainapp.modules.base.datatype.DataType;
import domainapp.modules.base.datatype.DataTypeUtil;
import domainapp.modules.base.view.Value;

/**
 * @author Prajapati
 *
 */
public class TextFilterBuilder extends FilterBuilder {
	
	public static final Pattern CSV_REGEX_PATTERN = Pattern.compile("(\"(?:[^\"]|\"\")*\"|[^,\"\\n\\r]*)(,|\\r?\\n|\\r)");

	public TextFilterBuilder(String fieldName) {
		super(fieldName);
	}

	@Override
	public String buildFilterString(Map<String, Object> criteria, Map<String, Value> parameters, boolean addAnd) {
		Object criteriaValue = criteria.get(getFieldName());
		if (criteriaValue == null) {
			// no value available and hence there is nothing to be done
			return null;
		}
		String value = null;
		if (criteriaValue instanceof String) {
			value = (String)criteriaValue;
		} else {
			value = String.valueOf(criteriaValue);
		}
		if (value.trim().isEmpty()) {
			// empty value and hence there is nothing to be done
			return null;
		}
		StringBuilder filter = new StringBuilder();
		Matcher matcher = CSV_REGEX_PATTERN.matcher(value + ",");
		boolean prefixOr = false;
		filter.append("(");
		while (matcher.find()) {
			if (prefixOr) {
				filter.append(" || ");
			}
			String textValue = sanitizeCsvValue(matcher.group(0));
			filter.append(getFieldName() + ".indexOf('" + textValue + "') >= 0");
			prefixOr = true;
		}
		filter.append(")");
		parameters.put(getFieldName(), DataTypeUtil.createValue(DataType.TEXT, value));
		addAnd = true;
		return filter.toString();
	}
	
	/**
	 * Remove enclosing quotes and ending delimiter.<br>
	 * "Paid For Order", => Paid For Order<br>
	 * 
	 * @param text
	 * @return
	 */
	private static final String sanitizeCsvValue(String text) {
		if (text == null) {
			return null;
		}
		text = text.trim();
		if (text.charAt(0) == '"' && text.endsWith("\",")) {
			return text.substring(1, text.length() - 2).trim();
		} else if (text.charAt(text.length() - 1) == ',') {
			return text.substring(0, text.length() - 1).trim();
		}
		return text.trim();
	}

}
