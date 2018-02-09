package rs.ruta.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.GregorianCalendar;
import java.util.function.Function;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class InstanceFactory
{
	/**Returns null or the value property of the object. Value of the property called value can be of the type String, BigDecimal,
	 * or some other type.
	 * @param <T> type of the object which metod getValue is being called
	 * @param <U> type of the return value of the getValue method
	 * @param property object whose value property is get
	 * @param extractor function which has two type parameters: of the type T or some supertype of the T, and U or some subtype of U
	 * @return value of the type U wich is the result of the called passed method getValue
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

	/**Transforms {@link XMLGregorianCalendar} to a {@code String} that is in the form of {@link LocalDate} object.
	 * @param xgc date and time
	 * @return string representation of a date
	 */
	public static String getLocalDateAsString(XMLGregorianCalendar xgc)
	{
		LocalDate date = InstanceFactory.getLocalDate(xgc);
		return date != null ? DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date) : null;
	}

	/**Transforms {@link XMLGregorianCalendar} to a {@code String} that is in the form of {@link LocalDate} object.
	 * @param xgc date and time
	 * @return string representation of a date
	 */
	public static String getLocalDateTimeAsString(XMLGregorianCalendar xgc)
	{
		LocalDateTime date = InstanceFactory.getLocalDateTime(xgc);
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
		return date != null ? formatter.format(date) : null;
	}

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

	/**Dissect the passed string in day, month and year and constructs the new {@link XMLGregorianCalendar} object.
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

}
