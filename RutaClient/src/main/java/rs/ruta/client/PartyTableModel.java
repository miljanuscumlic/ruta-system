package rs.ruta.client;

import java.time.*;
import java.time.format.*;
import java.util.function.*;

import javax.swing.table.*;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;

@SuppressWarnings("serial")
public class PartyTableModel extends AbstractTableModel
{
	private String[] rowNames =
		{
				"Party name", "Registration name", "Company ID", "Registration date", "Registration address", " - Street name",
				" - Building number", " - Floor", " - Room", " - Building name", " - City subdivision", " - City name", " - Postal zone",
				" - Country subentity", " - Country", "Classification code", "Postal address", " - Street name",
				" - Building number", " - Floor", " - Room", " - Building name", " - City subdivision", " - City name", " - Postal zone",
				" - Country subentity", " - Country", "Contact", " - Name", " - Telephone", " - Telefax", " - Email", " - Note", "Website"
		};

	private PartyType party;

	public PartyTableModel()
	{
		this.party = null;
	}

	public void setParty(PartyType party)
	{
		this.party = party;
	}

	@Override
	public int getRowCount()
	{
		return rowNames.length;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(columnIndex == 0)
			if(rowIndex < rowNames.length)
				return rowNames[rowIndex];
			else
				return null;
		else
		{
			switch(rowIndex)
			{
			case 0:
				try
				{
					return party.getPartyName().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyName().get(0).getName(), NameType::getValue);
				}
				catch(Exception e) { return null; }
			case 1:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationName(), RegistrationNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 2:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getCompanyID(), CompanyIDType::getValue);
				}
				catch(Exception e) { return null; }
			case 3:
				XMLGregorianCalendar cal = party.getPartyLegalEntity().size() == 0 ? null :
					(XMLGregorianCalendar) InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationDate(), RegistrationDateType::getValue);
				if(cal != null)
				{
					LocalDate date = InstanceFactory.getLocalDate((XMLGregorianCalendar) party.getPartyLegalEntity().get(0).getRegistrationDate().getValue());
					return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date);
				}
				else
					return null;
			case 4:
				return null;
			case 5:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getStreetName(), StreetNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 6:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingNumber(), BuildingNumberType::getValue);
				}
				catch(Exception e) { return null; }
			case 7:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getFloor(), FloorType::getValue);
				}
				catch(Exception e) { return null; }
			case 8:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getRoom(), RoomType::getValue);
				}
				catch(Exception e) { return null; }
			case 9:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingName(), BuildingNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 10:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCitySubdivisionName(), CitySubdivisionNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 11:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCityName(), CityNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 12:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getPostalZone(), PostalZoneType::getValue);
				}
				catch(Exception e) { return null; }
			case 13:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountrySubentity(), CountrySubentityType::getValue);
				}
				catch(Exception e) { return null; }
			case 14:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName(), NameType::getValue);
				}
				catch(Exception e) { return null; }
			case 15:
				try
				{
					return party.getPartyLegalEntity().size() == 0 ? null :
						InstanceFactory.getPropertyOrNull(party.getIndustryClassificationCode(), IndustryClassificationCodeType::getValue);
				}
				catch(Exception e) { return null; }
			case 16:
				return null;
			case 17:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getStreetName(), StreetNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 18:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getBuildingNumber(), BuildingNumberType::getValue);
				}
				catch(Exception e) { return null; }
			case 19:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getFloor(), FloorType::getValue);
				}
				catch(Exception e) { return null; }
			case 20:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getRoom(), RoomType::getValue);
				}
				catch(Exception e) { return null; }
			case 21:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getBuildingName(), BuildingNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 22:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getCitySubdivisionName(), CitySubdivisionNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 23:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getCityName(), CityNameType::getValue);
				}
				catch(Exception e) { return null; }
			case 24:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getPostalZone(), PostalZoneType::getValue);
				}
				catch(Exception e) { return null; }
			case 25:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getCountrySubentity(), CountrySubentityType::getValue);
				}
				catch(Exception e) { return null; }
			case 26:
				try
				{
					return party.getPostalAddress() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getPostalAddress().getCountry().getName(), NameType::getValue);
				}
				catch(Exception e) { return null; }
			case 27:
				return null;
			case 28:
				try
				{
					return party.getContact() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getContact().getName(), NameType::getValue);
				}
				catch(Exception e) { return null; }
			case 29:
				try
				{
					return party.getContact() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getContact().getTelephone(), TelephoneType::getValue);
				}
				catch(Exception e) { return null; }
			case 30:
				try
				{
					return party.getContact() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getContact().getTelefax(), TelefaxType::getValue);
				}
				catch(Exception e) { return null; }
			case 31:
				try
				{
					return party.getContact() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getContact().getElectronicMail(), ElectronicMailType::getValue);
				}
				catch(Exception e) { return null; }
			case 32:
				try
				{
					return party.getContact() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getContact().getNote().get(0), NoteType::getValue);
				}
				catch(Exception e) { return null; }
			case 33:
				try
				{
					return party.getContact() == null ? null :
						InstanceFactory.getPropertyOrNull(party.getWebsiteURI(), WebsiteURIType::getValue);
				}
				catch(Exception e) { return null; }
			default:
				return null;
			}
		}
	}

	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex)
	{
		switch(rowIndex)
		{
		case 0:
			//			party.getPartyName().get(0).getName().setValue(obj.toString());


			//			This functional code is not completed. There are so much lines, that there is the question if it is gained something with it
			//			against the plain use of the constructors and setter methods.
//			newPropertyObject(party.getPartyName(), List<PartyNameType>::add, PartyNameType::new);
//			newPropertyObject(party.getPartyName().get(0), PartyNameType::setName, NameType::new);
//			setProperty(party.getPartyName().get(0).getName(), NameType::setValue, obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyName(), PartyNameType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyName().get(0), party.getPartyName().get(0).getName(), NameType.class, obj, String.class);
			break;
		case 1:
			//			party.getPartyLegalEntity().get(0).getRegistrationName().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationName(),
					RegistrationNameType.class, obj, String.class);
			break;
		case 2:
			//			party.getPartyLegalEntity().get(0).getCompanyID().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getCompanyID(),
					CompanyIDType.class, obj, String.class);
			break;
		case 3:
			try
			{	// MMM: doesn't work without code below. In the newInstance metod RegistrationDate is not constructed, because of the paramater depth = 1
				// MMM: so it must be constructed here, before it can be set - OR constructed in the first place, OR here with some general method approach
				/*				if(party.getPartyLegalEntity().get(0).getRegistrationDate() == null)
					party.getPartyLegalEntity().get(0).setRegistrationDate(new RegistrationDateType());
				 */
				//				party.getPartyLegalEntity().get(0).getRegistrationDate().setValue(InstanceFactory.getXMLGregorianCalendar(obj.toString()));

				InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
				InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationDate(),
						RegistrationDateType.class, InstanceFactory.getXMLGregorianCalendar(obj.toString()), XMLGregorianCalendar.class);
			} catch (Exception e)
			{
				break; // should not do anything because the entry is invalid
			}
			break;
		case 4:
			break;
		case 5:
			//party.getPartyLegalEntity().get(0).getRegistrationAddress().getStreetName().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getStreetName(), StreetNameType.class, obj, String.class);
			break;
		case 6:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingNumber().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingNumber(), BuildingNumberType.class, obj, String.class);
			break;
		case 7:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getFloor().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getFloor(), FloorType.class, obj, String.class);

			break;
		case 8:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getRoom().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getRoom(), RoomType.class, obj, String.class);
			break;
		case 9:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingName().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingName(), BuildingNameType.class, obj, String.class);
			break;
		case 10:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCitySubdivisionName().setValue(obj.toString());


			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCitySubdivisionName(), CitySubdivisionNameType.class, obj, String.class);
			break;
		case 11:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCityName().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCityName(), CityNameType.class, obj, String.class);
			break;
		case 12:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getPostalZone().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getPostalZone(), PostalZoneType.class, obj, String.class);
			break;
		case 13:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountrySubentity().setValue(obj.toString());

			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountrySubentity(), CountrySubentityType.class, obj, String.class);
			break;
		case 14:
			/*			//1 - just set already constructed field
			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName().setValue(obj.toString());*/

			//2 - recursion
			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry(), CountryType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName(), NameType.class, obj, String.class);

			/*			//3 - constructors and setters - MMM: This version should be used, but this code should be placed in some other
			 * 			//	class from the Domain Model
			 *
			if(party.getPartyLegalEntity().size() == 0)
			{
				party.getPartyLegalEntity().add(new PartyLegalEntityType());
			}
			if (party.getPartyLegalEntity().get(0).getRegistrationAddress() == null)
			{
				party.getPartyLegalEntity().get(0).setRegistrationAddress(new AddressType());
			}
			if(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry() == null)
			{
				party.getPartyLegalEntity().get(0).getRegistrationAddress().setCountry(new CountryType());
			}
			if(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName() == null)
			{
				party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().setName(new NameType());
			}
			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName().setValue(obj.toString());
			*/


			//4 - functions

			break;
		case 15:
			/*			newPropertyObject(party, PartyType::setIndustryClassificationCode, IndustryClassificationCodeType::new);
			setProperty(party.getIndustryClassificationCode(), IndustryClassificationCodeType::setValue, obj.toString());

			party.setIndustryClassificationCode(new IndustryClassificationCodeType());
			party.getIndustryClassificationCode().setValue(obj.toString());*/

			InstanceFactory.createAndSetLeafProperty(party, party.getIndustryClassificationCode(), IndustryClassificationCodeType.class, obj, String.class);
			break;
		case 16:
			break;
		case 17:
			//			party.getPostalAddress().getStreetName().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getStreetName(), StreetNameType.class, obj, String.class);
			break;
		case 18:
			//			party.getPostalAddress().getBuildingNumber().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getBuildingNumber(), BuildingNumberType.class, obj, String.class);
			break;
		case 19:
			//			party.getPostalAddress().getFloor().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getFloor(), FloorType.class, obj, String.class);
			break;
		case 20:
			//			party.getPostalAddress().getRoom().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getRoom(), RoomType.class, obj, String.class);
			break;
		case 21:
			//			party.getPostalAddress().getBuildingName().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getBuildingName(), BuildingNameType.class, obj, String.class);
			break;
		case 22:
			//			party.getPostalAddress().getCitySubdivisionName().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getCitySubdivisionName(), CitySubdivisionNameType.class, obj, String.class);
			break;
		case 23:
			//			party.getPostalAddress().getCityName().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getCityName(), CityNameType.class, obj, String.class);
			break;
		case 24:
			//			party.getPostalAddress().getPostalZone().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getPostalZone(), PostalZoneType.class, obj, String.class);
			break;
		case 25:
			//			party.getPostalAddress().getCountrySubentity().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getCountrySubentity(), CountrySubentityType.class, obj, String.class);
			break;
		case 26:
			//			party.getPostalAddress().getCountry().getName().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.checkAndCreateNodeProperty(party.getPostalAddress(), party.getPostalAddress().getCountry(), CountryType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress().getCountry(), party.getPostalAddress().getCountry().getName(), NameType.class, obj, String.class);

			break;
		case 27:
			break;
		case 28:
			//			party.getContact().getName().setValue(obj.toString());

			/*			newPropertyObject(party, PartyType::setIndustryClassificationCode, IndustryClassificationCodeType::new);
			setProperty(party.getIndustryClassificationCode(), IndustryClassificationCodeType::setValue, obj.toString());*/

			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getName(), NameType.class, obj, String.class);
			break;
		case 29:
			//			party.getContact().getTelephone().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getTelephone(), TelephoneType.class, obj, String.class);
			break;
		case 30:
			//			party.getContact().getTelefax().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getTelefax(), TelefaxType.class, obj, String.class);
			break;
		case 31:
			//			party.getContact().getElectronicMail().setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getElectronicMail(), ElectronicMailType.class, obj, String.class);
			break;
		case 32:
//			party.getContact().getNote().get(0).setValue(obj.toString());

			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.checkAndCreateListProperty(party.getContact().getNote(), NoteType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact().getNote(), party.getContact().getNote().get(0), NoteType.class, obj, String.class);
			break;
		case 33:
			//			party.getWebsiteURI().setValue(obj.toString());;

			InstanceFactory.createAndSetLeafProperty(party, party.getWebsiteURI(), WebsiteURIType.class, obj, String.class);
			break;
		default:
			;
		}
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return "Property";
		case 1:
			return "Value";
		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return (column == 0 || row == 4 || row == 16 || row == 27) ? false : true;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return String.class;
		default:
			return Object.class;
		}
	}

	// MMM: methods with the functional interfaces are not used, because they are not finished
	// MMM: this should be deleted or refactored to InstanceFactory class
	public static <T, U> Consumer<T> bindToMethodWithSupplier(BiConsumer<? super T, U> c, Supplier<U> arg2)
	{
		return arg1 -> c.accept(arg1, arg2.get()); //definition of the accept method of the returning Consumer
	}

	public static <T, U> Consumer<T> bindToMethodWithArgument(BiConsumer<? super T, U> c, U arg2)
	{
		return arg1 -> c.accept(arg1, arg2); //definition of the accept method of the returning Consumer
	}

	public static <T, U> void newPropertyObject(T ref, BiConsumer<? super T, U> c, Supplier<U> s)
	{
		bindToMethodWithSupplier(c, s).accept(ref);
	}

	public static <T, U> void setProperty(T ref, BiConsumer<? super T, U> c, U val)
	{
		bindToMethodWithArgument(c, val).accept(ref);
	}

	//NOT WORKING
	public static <T, U, S> void bindToMethod(T arg1, BiConsumer<? super T, U> consumerNew, Supplier<U> supplierNew,
			U arg2, BiConsumer<? super U, S> consumerSet, Supplier<U> supplierGet, S value)
	{
		bindToMethodWithSupplier(consumerNew, supplierNew).accept(arg1);
		bindToMethodWithArgument(consumerSet, value).accept(supplierGet.get()/*arg2*/);
	}

	/**Functional version of the setter method
	 * @param party
	 */
	public static void test(PartyType party)
	{
		bindToMethodWithSupplier(PartyType::setIndustryClassificationCode, IndustryClassificationCodeType::new).accept(party);
		bindToMethodWithArgument(IndustryClassificationCodeType::setValue, "123456").accept(party.getIndustryClassificationCode());
	}

}
