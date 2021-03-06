/**
 * 
 */
package domainapp.modules.base.datatype.definition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Prajapati
 *
 */
public class DateDataTypeDefinitionTest {

	private DateDataTypeDefinition definition;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		definition = new DateDataTypeDefinition();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		definition = null;
	}

	/**
	 * Test method for {@link domainapp.modules.base.datatype.definition.DateDataTypeDefinition#stringToValue(java.lang.String)}.
	 * @throws ParseException 
	 */
	@Test
	public void testStringToValueString() throws ParseException {
		Date date = definition.stringToValue("2019-01-01");
		Assert.assertNotNull(date);
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"), date);
	}

	/**
	 * Test method for {@link domainapp.modules.base.datatype.definition.DateDataTypeDefinition#valueToString(java.util.Date)}.
	 * @throws ParseException 
	 */
	@Test
	public void testValueToStringDate() throws ParseException {
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01");
		String value = definition.valueToString(date);
		Assert.assertNotNull(value);
		Assert.assertEquals("2019-01-01", value);
	}

	/**
	 * Test method for {@link domainapp.modules.base.datatype.definition.BaseDataTypeDefinition#parse(java.lang.String)}.
	 * @throws ParseException 
	 */
	@Test
	public void testParse() throws ParseException {
		Date parse = definition.parse("2019-01-01");
		Assert.assertNotNull(parse);
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"), parse);
	}

	/**
	 * Test method for {@link domainapp.modules.base.datatype.definition.BaseDataTypeDefinition#format(java.util.List)}.
	 */
	@Test
	public void testFormat() {
		Date date = definition.parse("2019-01-01");
		String format = definition.format(Arrays.asList(date));
		Assert.assertNotNull(format);
		Assert.assertEquals("2019-01-01", format);
	}

}
