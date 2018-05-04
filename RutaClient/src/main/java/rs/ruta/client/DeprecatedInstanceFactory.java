package rs.ruta.client;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.function.Function;

import javax.xml.datatype.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;

public final class DeprecatedInstanceFactory
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");

	/**Instatiate new instance object of the ItemType, setting all properties used in the Ruta application on not null.
	 * Properties not used in the Ruta remain null.
	 * @return new object of type ItemType
	 */
	@Deprecated
	public static ItemType newInstanceItemType()
	{
		ItemType item = new ItemType();
		DescriptionType desc = new DescriptionType();
		//		desc.setValue("");
		item.getDescription().add(desc);
		NameType name = new NameType();
		//		name.setValue("");
		item.setName(name);
		ItemIdentificationType ident = new ItemIdentificationType();
		ident.setID(new IDType());
		item.setSellersItemIdentification(ident);
		return item;
	}

	/**Instatiate new instance object of the PartyType, setting all properties used in the Ruta application on not null.
	 * Properties not used in the Ruta remain null.
	 * @return new object of type PartyType
	 */
	@Deprecated
	public static PartyType newInstancePartyType()
	{
		PartyType party = new PartyType();
		party.setWebsiteURI(new WebsiteURIType());
		party.setIndustryClassificationCode(new IndustryClassificationCodeType());
		PartyIdentificationType partyID = new PartyIdentificationType();
		IDType IDValue = new IDType();
		partyID.setID(IDValue);
		party.getPartyIdentification().add(partyID);
		PartyNameType partyName = new PartyNameType();
		NameType pName = new NameType();
		partyName.setName(pName);
		party.getPartyName().add(partyName);

		AddressType postalAddress = newInstanceAddressType();
		party.setPostalAddress(postalAddress);

		PartyLegalEntityType legalEntity = newInstancePartyLegalEntity();
		party.getPartyLegalEntity().add(legalEntity);

		ContactType contact = newInstanceContactType();
		party.setContact(contact);

		return party;
	}

	@Deprecated
	public static Party newInstanceParty()
	{
		Party party = new Party();
		party.setWebsiteURI(new WebsiteURIType());
		party.setIndustryClassificationCode(new IndustryClassificationCodeType());
		PartyIdentificationType partyID = new PartyIdentificationType();
		IDType IDValue = new IDType();
		partyID.setID(IDValue);
		party.getPartyIdentification().add(partyID);
		PartyNameType partyName = new PartyNameType();
		NameType pName = new NameType();
		partyName.setName(pName);
		party.getPartyName().add(partyName);

		AddressType postalAddress = newInstanceAddressType();
		party.setPostalAddress(postalAddress);

		PartyLegalEntityType legalEntity = newInstancePartyLegalEntity();
		party.getPartyLegalEntity().add(legalEntity);

		ContactType contact = newInstanceContactType();
		party.setContact(contact);

		return party;
	}

	/**Instatiate new instance object of the ContactType, setting all properties used in the Ruta application on not null.
	 * Properties not used in the Ruta remain null.
	 * @return new object of type ContactType
	 */
	@Deprecated
	public static ContactType newInstanceContactType()
	{
		ContactType contact = new ContactType();
		contact.setID(new IDType());
		contact.setName(new NameType());
		contact.setTelephone(new TelephoneType());
		contact.setTelefax(new TelefaxType());
		contact.setElectronicMail(new ElectronicMailType());
		contact.getNote().add( new NoteType());

		return contact;
	}

	/**Instatiate new instance object of the PartyLegalEntityType, setting all properties used in the Ruta application on not null.
	 * Properties not used in the Ruta remain null.
	 * @return new object of type PartyLegalEntityType
	 */
	@Deprecated
	public static PartyLegalEntityType newInstancePartyLegalEntity()
	{
		PartyLegalEntityType entity = new PartyLegalEntityType();
		entity.setRegistrationName(new RegistrationNameType());
		entity.setCompanyID(new CompanyIDType());
		entity.setRegistrationDate(new RegistrationDateType());
//		entity.getRegistrationDate().setValue(getDate()); // MMM: based on xsd it must have some value - for TEST purposes setting to now()
		entity.setRegistrationAddress(newInstanceAddressType());

		return entity;
	}

	/**Instatiate new instance object of the AddressType, setting all properties used in the Ruta application on not null.
	 * Properties not used in the Ruta remain null.
	 * @return new object of type AddressType
	 */
	@Deprecated
	public static AddressType newInstanceAddressType()
	{
		AddressType address = new AddressType();
		address.setFloor(new FloorType());
		address.setRoom(new RoomType());
		address.setStreetName(new StreetNameType());
		address.setBuildingName(new BuildingNameType());
		address.setBuildingNumber(new BuildingNumberType());
		address.setCitySubdivisionName(new CitySubdivisionNameType());
		address.setCityName(new CityNameType());
		address.setPostalZone(new PostalZoneType());
		address.setCountrySubentity(new CountrySubentityType());
		CountryType country = new CountryType();
		country.setName(new NameType());
		address.setCountry(country);

		return address;
	}

	/**
	 * Creates new instance object of type T. Every non-String field, i.e. field with the complex type
	 * is intialized with new object of that type. Leaf fields, i.e. fields with the type of String
	 * remain null.
	 * CAUTION!!! Because of the nature of UBL XML elements, and their recursive definition, this method
	 * may throw StackOverflowException, which is making this method inherently unsafe for use with the UBL XML
	 * JAXB generated classes.
	 * @param cl object of type T that is being copied
	 * @return new instance object of type T
	 */
	@Deprecated
	public static <T> T newInstance(Class<T> cl)
	{
		T object = null;
		try
		{
			@SuppressWarnings("unchecked")
			Constructor<T>[] constructor = (Constructor<T>[]) cl.getDeclaredConstructors();
			Constructor<T> c = constructor[0];	//there is only one default with no args constructor
			object = (T) c.newInstance();

			//Field[] fields = cl.getFields(); // MMM: DOES NOT WORK: does not return any filed from the cuperclasses beacause they are protected not public
			Field[] fields = cl.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true); //enables access to private fields
			for(Field field : fields)
			{
				{
					Class<?> fieldType = field.getType();
					if(fieldType == java.util.List.class || fieldType == java.util.ArrayList.class) //fieldValue is an ArrayList or List then copy array
					{
						//creates empty ArrayList if it is null
						Method method = cl.getDeclaredMethod(synthesizeMethodName1("get", field.getName()));
						@SuppressWarnings("unchecked")
						ArrayList<Object> copyList = (ArrayList<Object>)(method.invoke(object));
						String listSignature = field.getGenericType().getTypeName(); // signature of the List object
						String elementClassName = listSignature.substring(listSignature.lastIndexOf('<') + 1, listSignature.lastIndexOf('>'));
						copyList.add(newInstance(Class.forName(elementClassName)));
					}
					else // set value of the field
					{
						//Method method = cl.getDeclaredMethod(syntethizeMethodName("set", field.getName()), fieldType);
						Method method = cl.getMethod(synthesizeMethodName1("set", field.getName()), fieldType); // MMM: alternative to above statement - checking all methods from superclasses
						method.invoke(object, newInstance(fieldType));
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Something went wrong. Can not do the reflection of the object!");
			logger.error("Exception is ", e);
		}
		return object;
	}

	/**
	 * Creates new instance object of type T. Every field with the complex type
	 * is intialized with new object of that type with null field of its own. Leaf fields, i.e. fields
	 * with the type of BigDecial, boolean, byte[], String or XMLGregorianCalendar remain null.
	 * Recursion is permitted to the depth of the depth parameter. If the depth is 1, than only the
	 * properties of the object will not be null. Properties of the properties will remain null.
	 * @param cl object of type T that is being copied
	 * @param depth pertmitable number of layers of the recursion
	 * @return new instance object of type T
	 */
	@Deprecated
	public static <T> T newInstance(Class<T> cl, int depth)
	{
		T object = null;
		try
		{
			@SuppressWarnings("unchecked")
			Constructor<T>[] constructor = (Constructor<T>[]) cl.getDeclaredConstructors();
			Constructor<T> c = constructor[0];	//there is only one default with no args constructor
			object = (T) c.newInstance();

			if(depth > 0)
			{
				Field[] fields = cl.getDeclaredFields();
				AccessibleObject.setAccessible(fields, true); //enables access to private fields to reflaction
				for(Field field : fields)
				{
					Class<?> fieldType = field.getType();
					if(fieldType == java.util.List.class || fieldType == java.util.ArrayList.class) //fieldValue is an ArrayList or List - then form array
					{
						//creates empty ArrayList if it is null
						Method method = cl.getDeclaredMethod(synthesizeMethodName1("get", field.getName()));
						@SuppressWarnings("unchecked")
						ArrayList<Object> copyList = (ArrayList<Object>)(method.invoke(object));
						String listSignature = field.getGenericType().getTypeName(); // signature of the List object
						String elementClassName = listSignature.substring(listSignature.lastIndexOf('<') + 1, listSignature.lastIndexOf('>'));
						copyList.add(newInstance(Class.forName(elementClassName), depth-1));
					}
					else // set value of the field
					{
						//Method method = cl.getMethod(syntethizeMethodName("set", field.getName()), fieldType); // MMM: alternative to above statement - checking all methods from superclasses
						Method method = cl.getMethod(synthesizeMethodName1("set", field.getName()), fieldType);
						method.invoke(object, newInstance(fieldType, depth-1));
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Something went wrong. Can not do the reflection of the object!");
			logger.error("Exception is ", e);
		}
		return object;
	}

	/**
	 * Creates new instance object of type T as a deep copy of passed object,
	 * copying all properties that are set in the passed object.
	 * Properties that are null or false in the passed object, are not set in the new object.
	 * @param <T> class of the object to be instatiated
	 * @param object object of type T that is being copied
	 * @return new instance object of type T
	 */
	@Deprecated
	@SuppressWarnings("restriction")
	public static <T> T newInstance(T object)
	{
		Class<?> cl = object.getClass();
		T copyObject = null;

		try
		{
			@SuppressWarnings("unchecked")
			Constructor<T>[] constructor = (Constructor<T>[]) cl.getDeclaredConstructors();
			Constructor<T> c = constructor[0];	//there is only one default no arg constructor
			copyObject = (T) c.newInstance();

			while(cl.getSuperclass() != null) // we don't want to process Object.class
			{
				Field[] fields = cl.getDeclaredFields();
				AccessibleObject.setAccessible(fields, true); //enables access to private fields
				for(Field field : fields)
				{
					Object fieldValue = field.get(object);
					if(fieldValue != null)
					{
						Class<?> fieldType = fieldValue.getClass();
						if(fieldType == java.util.ArrayList.class) //fieldValue is an ArrayList then copy array
						{
							ArrayList<?> list = (java.util.ArrayList<?>)fieldValue;
							int length = list.size();
							if(length > 0)
							{
								//creates empty ArrayList if it is null
								Method method = cl.getDeclaredMethod(synthesizeMethodName1("get", field.getName()));
								@SuppressWarnings("unchecked")
								ArrayList<Object> copyList = (ArrayList<Object>)(method.invoke(copyObject));
								for(int i = 0; i < length; i++)
								{
									Object element = list.get(i);
									Object el = newInstance(element);
									copyList.add(el);
								}
							}
						}
						else // set value of the field
						{
							Method method;
							// field is of XMLGregorianCalendarImpl type, but there is no method that accept this implementation instead of interface XMLGregorianCalendar
							if(//fieldType == com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl.class ||
									//fieldType == org.apache.xerces.jaxp.datatype.XMLGregorianCalendarImpl.class ||
									fieldType.getName().contains("XMLGregorianCalendarImpl"))
								method = cl.getDeclaredMethod(synthesizeMethodName1("set", field.getName()), XMLGregorianCalendar.class);
							else
								method = cl.getDeclaredMethod(synthesizeMethodName1("set", field.getName()), fieldType);
							Class<?> fieldSuperclazz = fieldType.getSuperclass();
							if(fieldSuperclazz == Object.class ||
									fieldSuperclazz == Number.class ||
									fieldSuperclazz == XMLGregorianCalendar.class) // field is of String, some primitive type, wrapper class or XMLGregorianCalendarImpl
								method.invoke(copyObject, fieldValue);
							else
								method.invoke(copyObject, newInstance(fieldValue));
						}
					}
				}

				cl = cl.getSuperclass();

			// MMM: THIS COMMENTED CODE BELOW IS DEPRECATED AND SHOULD BE REMOVED
			// If there are no fields in the class, but there are protected fields in some supperclass.
			// They cannot be reached by reflection, because they are protected, and this ugly switch case is nedded therefor.
			// This way field value is copied from one object to the another

			//MMM: Above statement is not true - below is the code - RETHINK this method
//			Class<?> current = yourClass;
//			while(current.getSuperclass()!=null){ // we don't want to process Object.class
//			    // do something with current's fields
//			    current = current.getSuperclass();
//			}


			/*if(fields.length == 0)
			{
				Method methodSet = null;
				Method methodGet = null;
				Class<?> valueClazz = null;

				if(object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.AmountType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.MeasureType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.NumericType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.QuantityType)
					valueClazz = BigDecimal.class;
				else if(object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.BinaryObjectType)
					valueClazz = byte[].class;
				else if(object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.CodeType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.DateTimeType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.IdentifierType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.IndicatorType ||
						object instanceof un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21.TextType )
					valueClazz = String.class;
				else if(object instanceof oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.DateTimeType ||
						object instanceof oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.DateType ||
						object instanceof oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.TimeType)
					valueClazz = XMLGregorianCalendar.class;
				else if(object instanceof oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.IndicatorType)
					valueClazz = boolean.class;

				if(valueClazz != null)
				{
					if(valueClazz == boolean.class)
						methodGet = cl.getMethod("isValue");
					else
						methodGet = cl.getMethod("getValue");
					methodSet = cl.getMethod("setValue", valueClazz);
					Object value = methodGet.invoke(object);
					methodSet.invoke(copyObject, value);
				}
				else
					throw new Exception();
			}*/

			//	MMM: CHECK if this works when newInstance method is invoked directly
			}
		}
		catch (Exception e)
		{
			System.out.println("Something went wrong. Can not do the reflection of the object!");
			logger.error("Exception is ", e);
		}
		return copyObject;
	}

	/**Copies all properties from old object to the copy object, that are not null in the old object.
	 * Properties that are already null, if any, are not changed in the copy object.
	 * @param oldObject object from which the properties are copied
	 * @param copyObject object to which properties are copied
	 */
	@Deprecated
	public static <T> void copyInstance(T oldObject, T copyObject)
	{
		Class<?> copyClazz = copyObject.getClass();
		Class<?> oldClazz = oldObject.getClass();
		try
		{
			if(copyClazz != oldClazz)
				throw new Exception();

			// loop for reaching properties of the superclasses
			// loops until Object.class which is not processed
			while(oldClazz.getSuperclass() != null)
			{
				Field[] oldFields = oldClazz.getDeclaredFields();
				AccessibleObject.setAccessible(oldFields, true);

				for(Field oldField : oldFields)
				{
					Object oldFieldValue = oldField.get(oldObject);

					if(oldFieldValue != null)
					{
						Class<?> fieldType = oldFieldValue.getClass();
						if(fieldType == java.util.ArrayList.class)
						{
							ArrayList<?> oldList = (java.util.ArrayList<?>)oldFieldValue;
							int length = oldList.size();
							if(length > 0)
							{
								Method method = oldClazz.getDeclaredMethod(synthesizeMethodName1("get", oldField.getName()));
								@SuppressWarnings("unchecked")
								ArrayList<Object> copyList = (ArrayList<Object>) (method.invoke(copyObject));
								copyList.clear();
								for(int i = 0; i< length; i++)
									copyList.add(newInstance(oldList.get(i)));
							}
						}
						else
						{
							Method method = oldClazz.getDeclaredMethod(synthesizeMethodName1("set", oldField.getName()), fieldType);
							if(fieldType.getSuperclass() == Object.class || fieldType.getSuperclass() == Number.class) // field is of String or some primitive type
								method.invoke(copyObject, oldFieldValue);
							else
								method.invoke(copyObject, newInstance(oldFieldValue));
						}
					}
				}
				oldClazz = oldClazz.getSuperclass();
			}
		}
		catch (Exception e)
		{
			System.out.println("Something went wrong. Can not do the reflection of the object!");
			logger.error("Exception is ", e);
		}
	}

	/**Synthesizes the method name, adding prefix to the passed string which respresent the field name of the class
	 * @param prefix prefix to be added
	 * @param name field name of the class passed as the string
	 * @return string representing the method name to be called
	 */
	private static String synthesizeMethodName1(String prefix, String name)
	{
		StringBuilder sb = new StringBuilder(prefix);
		if(name.equals("uuid"))
			sb.append("ID");
		else
			sb.append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
		return sb.toString();
	}

	/**Reflection version of setter method of the node property i.e. not leaf property. If the node property object is null,
	 * then it creates the object.
	 * @param <T> type of the clientFrame of the property under check
	 * @param <U> type of the property under check
	 * @param ref clientFrame of the property
	 * @param property property under check
	 * @param propClazz class object of the property
	 * @return created property
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> U checkAndCreateNodeProperty(T ref, U property, Class<U> propClazz)
	{
		if(property == null)
		{
			try
			{
				property = propClazz.newInstance();
				Class<T> refClazz = (Class<T>) ref.getClass();
				String propClazzName = propClazz.getSimpleName();
				Method refM = refClazz.getMethod(DeprecatedInstanceFactory.synthesizeMethodName2("set", propClazzName), propClazz);
				refM.invoke(ref, property);
			} catch (Exception e)
			{
				logger.error("Exception is ", e);
			}
		}
		return property;
	}

	/**Reflection version of setter method of the node property i.e. not leaf property. If the node property object is null,
	 * then it creates the object and set its value calling denoted setter method.
	 * @param <T> type of the clientFrame of the property under check
	 * @param <U> type of the property under check
	 * @param ref clientFrame of the property
	 * @param property property under check
	 * @param propClazz class object of the property
	 * @param methodName string representing a setter method name that should be called
	 * @return created property
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> U checkAndCreateNodeProperty(T ref, U property, Class<U> propClazz, String methodName)
	{
		if(property == null)
		{
			try
			{
				property = propClazz.newInstance();
				Class<T> refClazz = (Class<T>) ref.getClass();
				Method refM = refClazz.getMethod(methodName, propClazz);
				refM.invoke(ref, property);
			} catch (Exception e)
			{
				logger.error("Exception is ", e);
			}
		}
		return property;
	}

	/**Reflection version of the leaf property setter method. If the leaf property object is null, then it creates the object and sets its value.
	 * @param <T> type of the clientFrame of the property under check
	 * @param <U> type of the property under check
	 * @param <V> type of the property value
	 * @param ref clientFrame property of the property under check
	 * @param property property under check
	 * @param propClazz class object of the property
	 * @param value String value that should be asigned to the property
	 * @param valueClazz class object of the value
	 * @return created and/or set property
	 */
	public static <T, U, V> U createAndSetLeafProperty(T ref, U property, Class<U> propClazz, Object value, Class<V> valueClazz)
	{
		if(property == null)
			property = checkAndCreateNodeProperty(ref, property, propClazz);
		try
		{
			Method propM = propClazz.getMethod("setValue", valueClazz);
			propM.invoke(property, value);
		} catch (Exception e)
		{
			logger.error("Exception is ", e);
		}
		return property;
	}

	/**Cheks if the list property is of size greater then 0. If size is 0 then it creates first element of the list.
	 * @param <U> type of the list element
	 * @param <T> type of the list property object
	 * @param ref list property under check
	 * @param propClazz class object of the list element
	 * @return first element of the list
	 */
	@SuppressWarnings("unchecked")
	public static <U, T> U checkAndCreateListProperty(T ref, Class<U> propClazz)
	{
		Class<T> refClazz = (Class<T>) ref.getClass();
		U listElement = null;
		if(refClazz == List.class || refClazz == ArrayList.class)
			if (((List<U>) ref).size() == 0)
			{
				try
				{
					U property = propClazz.newInstance();
					Method refM = refClazz.getMethod("add", Object.class);
					refM.invoke(ref, property);
				}
				catch (Exception e)
				{
					logger.error("Exception is ", e);
				}
			}
		Method elM;
		try
		{
			elM = refClazz.getMethod("get", int.class);
			listElement  = (U) elM.invoke(ref, 0);
		}
		catch (Exception e)
		{
			logger.error("Exception is ", e);
		}
		return listElement;
	}

	/**Synthesizes the method name, adding prefix to the passed string which respresent the simple class name, and removing string "Type"
	 * from it.
	 * @param prefix prefix to be added
	 * @param name simple class name of the type passed as the string
	 * @return string representing the method name to be called
	 */
	public static String synthesizeMethodName2(String prefix, String name)
	{
		StringBuilder sb = new StringBuilder(prefix);
		sb.append(name.substring(0, name.lastIndexOf("Type")));
		return sb.toString();
	}

	/**Returns null or the value property of the object. Value of the property called value can be of the type String, BigDecimal,
	 * or some other type.
	 * @param <T> type of the object which metod getValue is being called
	 * @param <U> type of the return value of the getValue method
	 * @param property object whose value property is get
	 * @param extractor function which has two type parameters: of the type T or some supertype of the T, and U or some subtype of U
	 * @return value of the type U wich is the result of the called passed method getValue
	 */
/*	public static <T, U> U getPropertyOrNull(T property, Function<? super T, ? extends U> extractor)
	{
		return property != null ? extractor.apply(property) : null;
	}*/

	/**Recursion version of the getter method .Gets the value of the property if it is defined. If the clientFrame of the property,
	 * or property is null, returns null.
	 * @param T type of the property being returned
	 * @param field clientFrame of the proprety
	 * @param property property whose value is questioned
	 * @return returns value of the property, or null if property or clientFrame are not set
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	private <T> T getPropertyOrNull(Object field, Object property) // MMM: getter returning the boolean is isValue not getValue - should be corrected
	{
		if(field != null && property != null)
		{
			try
			{
				return (T) property.getClass().getMethod("getValue").invoke(property);
			} catch (Exception e)
			{
				logger.error("Exception is ", e);
			}
		}
		return null;
	}

}
