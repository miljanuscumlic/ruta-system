package rs.ruta.common;

import java.util.function.Function;

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

}
