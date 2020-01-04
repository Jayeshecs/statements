/**
 * 
 */
package domainapp.appdefn.view.daashboard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.isisaddons.wicket.wickedcharts.cpt.applib.WickedChart;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.HorizontalAlignment;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.LegendLayout;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.VerticalAlignment;
import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;

import domainapp.modules.base.view.GenericFilter;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.TransactionType;
import domainapp.modules.txn.dom.StatementSource;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.view.dashboard.ManageTransactionDashboard;

/**
 * Mixin for analytics of transaction by category by month
 * 
 * @author jayeshecs
 */
@Mixin
public class ManageTransactionDashboard_ByCategory {

	private ManageTransactionDashboard dashboard;
	
	public ManageTransactionDashboard_ByCategory(ManageTransactionDashboard dashboard) {
		this.dashboard = dashboard;
	}
	
	@ActionLayout(
			named = "By Category",
			describedAs = "Analysis by category"
	)
	@Action(semantics = SemanticsOf.IDEMPOTENT)
	public WickedChart $$(
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Statement Source")
			StatementSource source, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Start date")
			Date dateStart, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "End date")
			Date dateEnd, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Minimum")
			BigDecimal amountFloor, 
			@Parameter(optionality = Optionality.OPTIONAL)
			@ParameterLayout(named = "Maximum")
			BigDecimal amountCap
			) {
		// 1. Get requried transactions
		GenericFilter originalFilter = dashboard.getFilter();
		List<Transaction> transactions = dashboard
			.internalFilter(null, Arrays.asList(TransactionType.DEBIT), null, dateStart, dateEnd, amountFloor, amountCap, null, null, null)
			.getTransactions();
		dashboard.setFilter(originalFilter);
		
		// 2. Apply necessary grouping
		final Map<String, Map<Integer, BigDecimal>> groupedData = new HashMap<>();
		transactions.stream().forEach(t -> {
			String categoryName = "Uncategorized";
			Category category = t.getCategory();
			if (category != null) {
				categoryName = category.getName();
			}
			int month = DateUtils.toCalendar(t.getTransactionDate()).get(Calendar.MONTH);
			Map<Integer, BigDecimal> seriesData = groupedData.get(categoryName);
			if (seriesData == null) {
				synchronized (groupedData) {
					seriesData = groupedData.get(categoryName);
					if (seriesData == null) {
						seriesData = new TreeMap<Integer, BigDecimal>();
						for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; i++) {
							seriesData.put(i, new BigDecimal(0));
						}
						groupedData.put(categoryName, seriesData);
					}
				}
			}
			seriesData.put(month, seriesData.get(month).add(t.getAmount()));
		});
		// 3. Prepare chart options
		Options options = new Options();
		options.setTitle(new Title(""));
		options.setChartOptions(new ChartOptions(SeriesType.LINE));
		options.setxAxis(new Axis().setCategories(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")));
		options.setyAxis(new Axis().setTitle(new Title("Expense (INR)")));
		options.setLegend(new Legend().setLayout(LegendLayout.VERTICAL).setAlign(HorizontalAlignment.RIGHT).setVerticalAlign(VerticalAlignment.TOP).setX(-10).setY(100).setBorderWidth(0));
		groupedData.entrySet().stream().sorted((entryLeft, entryRight) -> {
			return entryLeft.getKey().compareTo(entryRight.getKey());
		}).forEach(entry -> {
			Map<Integer, BigDecimal> data = entry.getValue();
			List<BigDecimal> dataList = new ArrayList<>();
			for (int i = Calendar.JANUARY; i <= Calendar.DECEMBER; ++i) {
				dataList.add(data.get(i));
			}
			options.addSeries(new SimpleSeries().setName(entry.getKey()).setData(dataList.toArray(new BigDecimal[] {})));
		});
		WickedChart result = new WickedChart(options);
		return result;
	}
	
	public Date default1$$() {
		Date date = new Date();
		date = DateUtils.setMonths(date, Calendar.JANUARY);
		date = DateUtils.setDays(date, 1);
		date = DateUtils.setHours(date, 0);
		date = DateUtils.setMinutes(date, 0);
		date = DateUtils.setSeconds(date, 0);
		date = DateUtils.setMilliseconds(date, 0);
		return date;
	}
	
	public Date default2$$() {
		Date date = new Date();
		date = DateUtils.setMonths(date, Calendar.DECEMBER);
		date = DateUtils.setDays(date, 31);
		date = DateUtils.setHours(date, 23);
		date = DateUtils.setMinutes(date, 59);
		date = DateUtils.setSeconds(date, 59);
		date = DateUtils.setMilliseconds(date, 999);
		return date;
	}
}
