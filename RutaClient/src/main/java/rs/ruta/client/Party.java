package rs.ruta.client;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

import rs.ruta.common.InstanceFactory;

/**
 *Wrapper class for {@link PartyType} with some additional convinient methods.
 */
@XmlType(name = "Party")
@XmlAccessorType(XmlAccessType.NONE)
public class Party extends PartyType
{
	private static final long serialVersionUID = 7205209787239807571L;

	public Party() { super(); }

	/**
	 * Copy constructor that copies passed {@link PartyType} superclass object and
	 * retrieves new {@code Party} object.
	 * @param party {@code PartyType} object to initialise newly created {@code Party}
	 */
	public Party(PartyType party)
	{
		party.cloneTo(this);
	}

	@Override
	public Party clone()
	{
		Party ret = new Party();
	    super.cloneTo(ret);
	    return ret;
	}

	/**
	 * Returns Party's name.
	 * @return {@code String} representing Party name or {@code null} if Party name is not set.
	 */
	public String getPartySimpleName()
	{
		try
		{
			return super.getPartyName().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyName().get(0).getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPartySimpleName(String value)
	{
		if(value == null || "".equals(value)) //$NON-NLS-1$
			setPartyName(null);
		else
		{
			List<PartyNameType> names = getPartyName();
			if(names.isEmpty())
				names.add(new PartyNameType());
			if(names.get(0).getName() == null)
				names.get(0).setName(new NameType());
			names.get(0).getName().setValue(value);
		}
	}

	/*	public void setProductDescription(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.isEmpty())
			descriptions.add(new DescriptionType());
		if(hasCellValueChanged(descriptions.get(0).getValue(), value))
			descriptions.get(0).setValue(value);
	}*/

	/**
	 * Returns Party's registration name.
	 * @return ID or {@code null} if registration name has not been set
	 */
	public String getRegistrationName()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationName(), RegistrationNameType::getValue);
		}
		catch(Exception e) { return null; }
	}


	public void setRegistrationName(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).setRegistrationName((RegistrationNameType) null);
		else
		{
			if(legalEntities.get(0).getRegistrationName() == null)
				legalEntities.get(0).setRegistrationName(new RegistrationNameType());
			legalEntities.get(0).getRegistrationName().setValue(value);
		}
	}

	/**
	 * Returns Party's Company ID from {@link PartyLegalEntity} element.
	 * @return Party's Company ID or {@code null} if Company ID has not been set
	 */
	public String getCompanyID()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getCompanyID(), CompanyIDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setCompanyID(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).setCompanyID((CompanyIDType) null);
		else
		{
		if(legalEntities.get(0).getCompanyID() == null)
			legalEntities.get(0).setCompanyID(new CompanyIDType());
		legalEntities.get(0).getCompanyID().setValue(value);
	}
	}

	public String getRegistrationDate()
	{
		XMLGregorianCalendar cal = super.getPartyLegalEntity().isEmpty() ? null :
			(XMLGregorianCalendar) InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationDate(), RegistrationDateType::getValue);
		if(cal != null)
		{
			LocalDate date = InstanceFactory.getLocalDate((XMLGregorianCalendar) super.getPartyLegalEntity().get(0).getRegistrationDate().getValue());
			return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date);
		}
		else
			return null;
	}

	public void setRegistrationDate(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).setRegistrationDate((RegistrationDateType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationDate() == null)
			legalEntities.get(0).setRegistrationDate(new RegistrationDateType());
		try
		{
			legalEntities.get(0).getRegistrationDate().setValue(InstanceFactory.getXMLGregorianCalendar(value));
		}
		catch (Exception e)
		{
			// should not do anything because the entry is invalid	MMM should be checked
		}
		}
	}

	/**
	 * Returns Party's registration street name.
	 * @return ID or {@code null} if registration street name has not been set
	 */
	public String getRegistrationStreetName()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getStreetName(), StreetNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationStreetName(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setStreetName((StreetNameType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getStreetName() == null)
			legalEntities.get(0).getRegistrationAddress().setStreetName(new StreetNameType());
		legalEntities.get(0).getRegistrationAddress().getStreetName().setValue(value);
	}
	}

	/**
	 * Returns Party's registration Building Number.
	 * @return ID or {@code null} if registration Building Number has not been set
	 */
	public String getRegistrationBuildingNumber()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingNumber(), BuildingNumberType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationBuildingNumber(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setBuildingNumber((BuildingNumberType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getBuildingNumber() == null)
			legalEntities.get(0).getRegistrationAddress().setBuildingNumber(new BuildingNumberType());
		legalEntities.get(0).getRegistrationAddress().getBuildingNumber().setValue(value);
	}
	}

	public String getRegistrationFloor()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getFloor(), FloorType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationFloor(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setFloor((FloorType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getFloor() == null)
			legalEntities.get(0).getRegistrationAddress().setFloor(new FloorType());
		legalEntities.get(0).getRegistrationAddress().getFloor().setValue(value);
	}
	}

	public String getRegistrationRoom()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getRoom(), RoomType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationRoom(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setRoom((RoomType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getRoom() == null)
			legalEntities.get(0).getRegistrationAddress().setRoom(new RoomType());
		legalEntities.get(0).getRegistrationAddress().getRoom().setValue(value);
	}
	}

	public String getRegistrationBuildingName()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingName(), BuildingNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationBuildingName(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setBuildingName((BuildingNameType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getBuildingName() == null)
			legalEntities.get(0).getRegistrationAddress().setBuildingName(new BuildingNameType());
		legalEntities.get(0).getRegistrationAddress().getBuildingName().setValue(value);
	}
	}

	public String getRegistrationCitySubdivision()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getCitySubdivisionName(), CitySubdivisionNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationCitySubdivision(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setCitySubdivisionName((CitySubdivisionNameType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getCitySubdivisionName() == null)
			legalEntities.get(0).getRegistrationAddress().setCitySubdivisionName(new CitySubdivisionNameType());
		legalEntities.get(0).getRegistrationAddress().getCitySubdivisionName().setValue(value);
	}
	}

	/**
	 * Returns Party's registration city name.
	 * @return ID or {@code null} if registration city name has not been set
	 */
	public String getRegistrationCityName()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getCityName(), CityNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationCityName(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setCityName((CityNameType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getCityName() == null)
			legalEntities.get(0).getRegistrationAddress().setCityName(new CityNameType());
		legalEntities.get(0).getRegistrationAddress().getCityName().setValue(value);
	}
	}

	public String getRegistrationPostalZone()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getPostalZone(), PostalZoneType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationPostalZone(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setPostalZone((PostalZoneType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getPostalZone() == null)
			legalEntities.get(0).getRegistrationAddress().setPostalZone(new PostalZoneType());
		legalEntities.get(0).getRegistrationAddress().getPostalZone().setValue(value);
	}
	}

	public String getRegistrationCountrySubentity()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getCountrySubentity(), CountrySubentityType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationCountrySubentity(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setCountrySubentity((CountrySubentityType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getCountrySubentity() == null)
			legalEntities.get(0).getRegistrationAddress().setCountrySubentity(new CountrySubentityType());
		legalEntities.get(0).getRegistrationAddress().getCountrySubentity().setValue(value);
	}
	}

	/**
	 * Returns Party's registration country.
	 * @return ID or {@code null} if registration country has not been set
	 */
	public String getRegistrationCountry()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setRegistrationCountry(String value)
	{
		List<PartyLegalEntityType> legalEntities = getPartyLegalEntity();
		if(legalEntities.isEmpty())
			legalEntities.add(new PartyLegalEntityType());
		if(legalEntities.get(0).getRegistrationAddress() == null)
			legalEntities.get(0).setRegistrationAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			legalEntities.get(0).getRegistrationAddress().setCountry((CountryType) null);
		else
		{
		if(legalEntities.get(0).getRegistrationAddress().getCountry() == null)
			legalEntities.get(0).getRegistrationAddress().setCountry(new CountryType());
		if(legalEntities.get(0).getRegistrationAddress().getCountry().getName() == null)
			legalEntities.get(0).getRegistrationAddress().getCountry().setName(new NameType());
		legalEntities.get(0).getRegistrationAddress().getCountry().getName().setValue(value);
	}
	}

	public String getClassificationCode()
	{
		try
		{
			return super.getPartyLegalEntity().isEmpty() ? null :
				InstanceFactory.getPropertyOrNull(super.getIndustryClassificationCode(), IndustryClassificationCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setClassificationCode(String value)
	{
		if(value == null || "".equals(value)) //$NON-NLS-1$
			super.setIndustryClassificationCode((IndustryClassificationCodeType) null);
		else
		{
			if(getIndustryClassificationCode() == null)
				setIndustryClassificationCode(new IndustryClassificationCodeType());
			getIndustryClassificationCode().setValue(value);
		}
	}

	public String getPostalStreetName()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getStreetName(), StreetNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalStreetName(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setStreetName((StreetNameType) null);
		else
		{
		if(getPostalAddress().getStreetName() == null)
			getPostalAddress().setStreetName(new StreetNameType());
		getPostalAddress().getStreetName().setValue(value);
	}
	}

	public String getPostalBuildingNumber()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getBuildingNumber(), BuildingNumberType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalBuildingNumber(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setBuildingNumber((BuildingNumberType) null);
		else
		{
		if(getPostalAddress().getBuildingNumber() == null)
			getPostalAddress().setBuildingNumber(new BuildingNumberType());
		getPostalAddress().getBuildingNumber().setValue(value);
	}
	}

	public String getPostalFloor()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getFloor(), FloorType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalFloor(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setFloor((FloorType) null);
		else
		{
		if(getPostalAddress().getFloor() == null)
			getPostalAddress().setFloor(new FloorType());
		getPostalAddress().getFloor().setValue(value);
	}
	}

	public String getPostalRoom()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getRoom(), RoomType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalRoom(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setRoom((RoomType) null);
		else
		{
		if(getPostalAddress().getRoom() == null)
			getPostalAddress().setRoom(new RoomType());
		getPostalAddress().getRoom().setValue(value);
		}
	}

	public String getPostalBuildingName()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getBuildingName(), BuildingNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalBuildingName(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setBuildingName((BuildingNameType) null);
		else
		{
		if(getPostalAddress().getBuildingName() == null)
			getPostalAddress().setBuildingName(new BuildingNameType());
		getPostalAddress().getBuildingName().setValue(value);
	}
	}

	public String getPostalCitySubdivision()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getCitySubdivisionName(), CitySubdivisionNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalCitySubdivision(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setCitySubdivisionName((CitySubdivisionNameType) null);
		else
		{
		if(getPostalAddress().getCitySubdivisionName() == null)
			getPostalAddress().setCitySubdivisionName(new CitySubdivisionNameType());
		getPostalAddress().getCitySubdivisionName().setValue(value);
	}
	}

	public String getPostalCityName()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getCityName(), CityNameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalCityName(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setCityName((CityNameType) null);
		else
		{
		if(getPostalAddress().getCityName() == null)
			getPostalAddress().setCityName(new CityNameType());
		getPostalAddress().getCityName().setValue(value);
	}
	}

	public String getPostalPostalZone()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getPostalZone(), PostalZoneType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalPostalZone(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setPostalZone((PostalZoneType) null);
		else
		{
		if(getPostalAddress().getPostalZone() == null)
			getPostalAddress().setPostalZone(new PostalZoneType());
		getPostalAddress().getPostalZone().setValue(value);
	}
	}

	public String getPostalCountrySubentity()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getCountrySubentity(), CountrySubentityType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalCountrySubentity(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setCountrySubentity((CountrySubentityType) null);
		else
		{
		if(getPostalAddress().getCountrySubentity() == null)
			getPostalAddress().setCountrySubentity(new CountrySubentityType());
		getPostalAddress().getCountrySubentity().setValue(value);
	}
	}

	public String getPostalCountry()
	{
		try
		{
			return super.getPostalAddress() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getPostalAddress().getCountry().getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setPostalCountry(String value)
	{
		if(getPostalAddress() == null)
			setPostalAddress(new AddressType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getPostalAddress().setCountry((CountryType) null);
		else
		{
		if(getPostalAddress().getCountry() == null)
			getPostalAddress().setCountry(new CountryType());
		if(getPostalAddress().getCountry().getName() == null)
			getPostalAddress().getCountry().setName(new NameType());
		getPostalAddress().getCountry().getName().setValue(value);
	}
	}

	public String getContactName()
	{
		try
		{
			return super.getContact() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getContact().getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setContactName(String value)
	{
		if(getContact() == null)
			setContact(new ContactType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getContact().setName((NameType) null);
		else
		{
		if(getContact().getName() == null)
			getContact().setName(new NameType());
		getContact().getName().setValue(value);
	}
	}

	public String getContactTelephone()
	{
		try
		{
			return super.getContact() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getContact().getTelephone(), TelephoneType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setContactTelephone(String value)
	{
		if(getContact() == null)
			setContact(new ContactType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getContact().setTelephone((TelephoneType) null);
		else
		{
		if(getContact().getTelephone() == null)
			getContact().setTelephone(new TelephoneType());
		getContact().getTelephone().setValue(value);
	}
	}

	public String getContactTelefax()
	{
		try
		{
			return super.getContact() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getContact().getTelefax(), TelefaxType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setContactTelefax(String value)
	{
		if(getContact() == null)
			setContact(new ContactType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getContact().setTelefax((TelefaxType) null);
		else
		{
		if(getContact().getTelefax() == null)
			getContact().setTelefax(new TelefaxType());
		getContact().getTelefax().setValue(value);
	}
	}

	public String getContactEmail()
	{
		try
		{
			return super.getContact() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getContact().getElectronicMail(), ElectronicMailType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setContactEmail(String value)
	{
		if(getContact() == null)
			setContact(new ContactType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getContact().setElectronicMail((ElectronicMailType) null);
		else
		{
		if(getContact().getElectronicMail() == null)
			getContact().setElectronicMail(new ElectronicMailType());
		getContact().getElectronicMail().setValue(value);
	}
	}

	public String getContactNote()
	{
		try
		{
			return super.getContact() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getContact().getNote().get(0), NoteType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setContactNote(String value)
	{
		if(getContact() == null)
			setContact(new ContactType());
		if(value == null || "".equals(value)) //$NON-NLS-1$
			getContact().setNote((List<NoteType>) null);
		else
		{
		if(getContact().getNote().isEmpty())
			getContact().getNote().add(new NoteType());
		getContact().getNote().get(0).setValue(value);
	}
	}

	public String getWebsite()
	{
		try
		{
			return super.getContact() == null ? null :
				InstanceFactory.getPropertyOrNull(super.getWebsiteURI(), WebsiteURIType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setWebsite(String value)
	{
		if(value == null || "".equals(value)) //$NON-NLS-1$
			setWebsiteURI((WebsiteURIType) null);
		else
		{
		if(getWebsiteURI() == null)
			setWebsiteURI(new WebsiteURIType());
		getWebsiteURI().setValue(value);
		}
	}

	public void setPartyID(String id)
	{
		if(id == null)
			setPartyIdentification(null);
		 List<PartyIdentificationType> identifications = getPartyIdentification();
		 if(identifications.isEmpty())
			 identifications.add(new PartyIdentificationType());
		 if(identifications.get(0).getID() == null)
			 identifications.get(0).setID(new IDType());
		 identifications.get(0).getID().setValue(id);
	}

	/**
	 * Returns Party's ID as a {@code String}.
	 * @return ID or {@code null} if ID has not been set
	 */
	public String getPartyID()
	{
		String ID = InstanceFactory.getPropertyOrNull(super.getPartyIdentification().get(0).getID(), IDType::getValue);
		return ("").equals(ID) ? null : ID; //$NON-NLS-1$
	}

	/**
	 * Returns Party's ID as a {@code String}.
	 * @param party Party to be checked
	 * @return ID or {@code null} if ID has not been set
	 */
	public static String getPartyID(PartyType party)
	{
		String ID = InstanceFactory.getPropertyOrNull(party.getPartyIdentification().get(0).getID(), IDType::getValue);
		return ("").equals(ID) ? null : ID; //$NON-NLS-1$
	}

	/**
	 * Verifies whether Party has all of its mandatory fields.
	 * @return {@code null} if it has all mandatory fields, or {@code String} designating the first missing field
	 */
	public String verifyParty()
	{
		String missingField = null;
		if(getPartySimpleName() == null)
			missingField = "Party Name"; 
		else if(getRegistrationName() == null)
			missingField = "Registration Name"; 
		else if(getCompanyID() == null)
			missingField = "Company ID"; 
		else if(getRegistrationStreetName() == null)
			missingField = "Registration Street Name"; 
		else if(getRegistrationBuildingNumber() == null)
			missingField = "Registration Building Number"; 
		else if(getRegistrationCityName() == null)
			missingField = "Registration City Name"; 
		else if(getRegistrationCountry() == null)
			missingField = "Registration Country"; 
		return missingField;
	}

	/**
	 * Checks whether two parties are the same. Check is based on equality of party IDs.
	 * @param partyOne
	 * @param partyTwo
	 * @return true if parties are the same, false if they are not the same or some party has not set
	 * its party ID.
	 */
	public static boolean sameParties(PartyType partyOne, PartyType partyTwo)
	{
		String partyOneID = getPartyID(partyOne);
		String otherPartyID = getPartyID(partyTwo);
		return partyOneID != null ? partyOneID.equals(otherPartyID) : false;
	}

	/**
	 * Returns String representing Party class. Used as the node name in the tree model for a CD Party.
	 * @return the name of the core Party or null if core Party is not set
	 */
	@Override
	public String toString()
	{
		return InstanceFactory.getPropertyOrNull(getPartyName().get(0).getName(), NameType::getValue);
	}

}
