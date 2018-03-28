package rs.ruta.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxSchemeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

/**
 * Class factory that instantiate different objects and have some convinient check methods.
 */
public final class InstanceFactory
{
	public static String TAX_CATEGORY_0 = "0";
	public static String TAX_CATEGORY_10 = "10";
	public static String TAX_CATEGORY_20 = "20";
	public static TaxSchemeType TAX_SCHEME = new TaxSchemeType();
	private static Map<String, TaxCategoryType> taxCategories;

	static
	{
		TAX_SCHEME.setName("Serbian tax scheme");
		TAX_SCHEME.setCurrencyCode("RSD");

		Map<String, TaxCategoryType> tempMap = new HashMap<String, TaxCategoryType>();
		TaxCategoryType tc0 = new TaxCategoryType();
		IDType id0 = new IDType(TAX_CATEGORY_0);
		tc0.setID(id0);
		tc0.setPercent(new BigDecimal(TAX_CATEGORY_0));
		tc0.setTaxScheme(TAX_SCHEME);
		tempMap.put(TAX_CATEGORY_0, tc0);

		TaxCategoryType tc1 = new TaxCategoryType();
		IDType id1 = new IDType(TAX_CATEGORY_10);
		tc1.setID(id1);
		tc1.setPercent(new BigDecimal(TAX_CATEGORY_10));
		tc1.setTaxScheme(TAX_SCHEME);
		tempMap.put(TAX_CATEGORY_10, tc1);

		TaxCategoryType tc2 = new TaxCategoryType();
		IDType id2 = new IDType(TAX_CATEGORY_20);
		tc2.setID(id2);
		tc2.setPercent(new BigDecimal(TAX_CATEGORY_20));
		tc2.setTaxScheme(TAX_SCHEME);
		tempMap.put(TAX_CATEGORY_20, tc2);

		taxCategories = Collections.unmodifiableMap(tempMap);
	}

	public static String[] getTaxCategories()
	{
		return new String[] {TAX_CATEGORY_0, TAX_CATEGORY_10, TAX_CATEGORY_20};
	}

	/**
	 * Returns {@code null} or the value of the property of the object. Value of the property
	 * can be of the type {@code String}, {@code BigDecimal}, or some other type.
	 * @param <T> type of object which extracting metod is being called
	 * @param <U> type of the return value of extracting method
	 * @param property property of the object whose value is get
	 * @param extractor function which has two type parameters: of the type T or some supertype of the T,
	 * and U or some subtype of U
	 * @return value of the type U which is the result of the called passed method for getting the value
	 */
	public static <T, U> U getPropertyOrNull(T property, Function<? super T, ? extends U> extractor)
	{
		return property != null ? extractor.apply(property) : null;
	}

	public static LocalDate getLocalDate(XMLGregorianCalendar xgc)
	{
		LocalDate date = null;
		if(xgc != null)
		{
			date = LocalDate.of(xgc.getYear(), xgc.getMonth(), xgc.getDay());
		}
		//System.out.println(date);
		return date;
	}

	public static LocalDateTime getLocalDateTime(XMLGregorianCalendar xgc)
	{
		LocalDateTime date = null;
		if(xgc != null)
		{
			date = LocalDateTime.of(xgc.getYear(), xgc.getMonth(), xgc.getDay(), xgc.getHour(), xgc.getMinute(), xgc.getSecond());
		}
		//System.out.println(date);
		return date;
	}

	/**
	 * Transforms {@link XMLGregorianCalendar} to a {@code String} that is in the form of {@link LocalDate} object.
	 * @param xgc date and time
	 * @return string representation of a date
	 */
	public static String getLocalDateAsString(XMLGregorianCalendar xgc)
	{
		LocalDate date = InstanceFactory.getLocalDate(xgc);
		return date != null ? DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date) : null;
	}

	/**
	 * Transforms {@link XMLGregorianCalendar} to a {@code String} that is in the form of {@link LocalDateTime} object.
	 * @param xgc date and time
	 * @return string representation of a date
	 */
	public static String getLocalDateTimeAsString(XMLGregorianCalendar xgc)
	{
		LocalDateTime date = InstanceFactory.getLocalDateTime(xgc);
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
		return date != null ? formatter.format(date) : null;
	}

	/**
	 * Constructs a {@link XMLGregorianCalendar} using the current time.
	 * @return current time as {@code XMLGregorianCalendar}
	 */
	public static XMLGregorianCalendar getDate()
	{
		try
		{
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
		}
		catch (DatatypeConfigurationException e) {
			throw new Error(e);
		}
	}

	/**
	 * Constructs a {@link XMLGregorianCalendar} from passed {@link GregorianCalendar}.
	 * @return time as {@code XMLGregorianCalendar}
	 */
	public static XMLGregorianCalendar getDate(GregorianCalendar gc)
	{
		try
		{
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		}
		catch (DatatypeConfigurationException e) {
			throw new Error(e);
		}
	}

	/**
	 * Dissect passed string in a day, month and year and constructs a new {@link XMLGregorianCalendar} object.
	 * @param str string representation of the datum
	 * @return XMLGregorianCalendar object representing datum
	 * @throws Exception if something goes wrong within the call of static factory method that constructs the XMLGregorianCalendar object
	 */
	public static XMLGregorianCalendar getXMLGregorianCalendar(String str) throws Exception
	{
//		System.out.println(str);
		String[] datum = str.split("\\.");
		return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
				Integer.parseInt(datum[2]), Integer.parseInt(datum[1]), Integer.parseInt(datum[0]), DatatypeConstants.FIELD_UNDEFINED);
	}

	/**
	 * Gets the object representing specific {@link TaxCategoryType tax category}.
	 * @param taxType parameter designating tax category. May be:
	 * <br>TAX_CATEGORY_0 for 0% tax rate
	 * <br>TAX_CATEGORY_10 for 10% tax rate
	 * <br>TAX_CATEGORY_20 for 20% tax rate
	 * @return {@code TaxCategoryType} object or {@code null} if {@code taxType} code is invalid
	 */
	public static TaxCategoryType getTaxCategory(String taxType)
	{
		return taxCategories.get(taxType);
	}

}