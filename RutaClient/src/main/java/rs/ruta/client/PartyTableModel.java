package rs.ruta.client;

import java.util.function.*;

import javax.swing.table.*;

public class PartyTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -6097474546942059715L;

	private String[] rowNames =
		{
				"Party name", "Registration name", "Company ID", "Registration date", "Registration address",
				" - Street name", " - Building number", " - Floor", " - Room", " - Building name", " - City subdivision",
				" - City name", " - Postal zone", " - Country subentity", " - Country", "Classification code",
				"Postal address", " - Street name", " - Building number", " - Floor", " - Room", " - Building name",
				" - City subdivision", " - City name", " - Postal zone", " - Country subentity", " - Country",
				"Contact", " - Name", " - Telephone", " - Telefax", " - Email", " - Note", "Website",
				"Industry Classification Code"
		};

	private Party party;
	private boolean tableEditable;

	public PartyTableModel()
	{
		party = null;
		tableEditable = true;
	}

	public boolean isTableEditable()
	{
		return tableEditable;
	}

	public void setTableEditable(boolean tableEditable)
	{
		this.tableEditable = tableEditable;
	}

	public void setParty(Party party)
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
				return party.getPartySimpleName();
			case 1:
				return party.getRegistrationName();
			case 2:
				return party.getCompanyID();
			case 3:
				return party.getRegistrationDate();
			case 4:
				return null;
			case 5:
				return party.getRegistrationStreetName();
			case 6:
				return party.getRegistrationBuildingNumber();
			case 7:
				return party.getRegistrationFloor();
			case 8:
				return party.getRegistrationRoom();
			case 9:
				return party.getRegistrationBuildingName();
			case 10:
				return party.getRegistrationCitySubdivision();
			case 11:
				return party.getRegistrationCityName();
			case 12:
				return party.getRegistrationPostalZone();
			case 13:
				return party.getRegistrationCountrySubentity();
			case 14:
				return party.getRegistrationCountry();
			case 15:
				return party.getClassificationCode();
			case 16:
				return null;
			case 17:
				return party.getPostalStreetName();
			case 18:
				return party.getPostalBuildingNumber();
			case 19:
				return party.getPostalFloor();
			case 20:
				return party.getPostalRoom();
			case 21:
				return party.getPostalBuildingName();
			case 22:
				return party.getPostalCitySubdivision();
			case 23:
				return party.getPostalCityName();
			case 24:
				return party.getPostalPostalZone();
			case 25:
				return party.getPostalCountrySubentity();
			case 26:
				return party.getPostalCountry();
			case 27:
				return null;
			case 28:
				return party.getContactName();
			case 29:
				return party.getContactTelephone();
			case 30:
				return party.getContactTelefax();
			case 31:
				return party.getContactEmail();
			case 32:
				return party.getContactNote();
			case 33:
				return party.getWebsite();
			case 34:
				return party.getIndustryClassificationCodeValue();
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

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyName(), PartyNameType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyName().get(0), party.getPartyName().get(0).getName(), NameType.class, obj, String.class);*/

			party.setPartySimpleName(obj.toString());
			break;
		case 1:
			//			party.getPartyLegalEntity().get(0).getRegistrationName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationName(),
					RegistrationNameType.class, obj, String.class);*/

			party.setRegistrationName(obj.toString());
			break;
		case 2:
			//			party.getPartyLegalEntity().get(0).getCompanyID().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getCompanyID(),
					CompanyIDType.class, obj, String.class);*/
			party.setCompanyID(obj.toString());
			break;
		case 3:
			/*try
			{
				//		party.getPartyLegalEntity().get(0).getRegistrationDate().setValue(InstanceFactory.getXMLGregorianCalendar(obj.toString()));

				InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
				InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationDate(),
						RegistrationDateType.class, InstanceFactory.getXMLGregorianCalendar(obj.toString()), XMLGregorianCalendar.class);
			} catch (Exception e)
			{
				break; // should not do anything because the entry is invalid
			}*/
			party.setRegistrationDate(obj.toString());
			break;
		case 4:
			break;
		case 5:
			//party.getPartyLegalEntity().get(0).getRegistrationAddress().getStreetName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getStreetName(), StreetNameType.class, obj, String.class);*/

			party.setRegistrationStreetName(obj.toString());
			break;
		case 6:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingNumber().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingNumber(), BuildingNumberType.class, obj, String.class);*/
			party.setRegistrationBuildingNumber(obj.toString());
			break;
		case 7:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getFloor().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getFloor(), FloorType.class, obj, String.class);*/
			party.setRegistrationFloor(obj.toString());
			break;
		case 8:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getRoom().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getRoom(), RoomType.class, obj, String.class);*/
			party.setRegistrationRoom(obj.toString());
			break;
		case 9:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getBuildingName(), BuildingNameType.class, obj, String.class);*/
			party.setRegistrationBuildingName(obj.toString());
			break;
		case 10:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCitySubdivisionName().setValue(obj.toString());


/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCitySubdivisionName(), CitySubdivisionNameType.class, obj, String.class);*/
			party.setRegistrationCitySubdivision(obj.toString());
			break;
		case 11:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCityName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCityName(), CityNameType.class, obj, String.class);*/
			party.setRegistrationCityName(obj.toString());
			break;
		case 12:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getPostalZone().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getPostalZone(), PostalZoneType.class, obj, String.class);*/
			party.setRegistrationPostalZone(obj.toString());
			break;
		case 13:
			//			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountrySubentity().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountrySubentity(), CountrySubentityType.class, obj, String.class);*/
			party.setRegistrationCountrySubentity(obj.toString());
			break;
		case 14:
			/*			//1 - just set already constructed field
			party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName().setValue(obj.toString());*/

			//2 - recursion
/*			InstanceFactory.checkAndCreateListProperty(party.getPartyLegalEntity(), PartyLegalEntityType.class);
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0), party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					AddressType.class, "setRegistrationAddress");
			InstanceFactory.checkAndCreateNodeProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry(), CountryType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry(),
					party.getPartyLegalEntity().get(0).getRegistrationAddress().getCountry().getName(), NameType.class, obj, String.class);*/

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
			//not implemented


			party.setRegistrationCountry(obj.toString());
			break;
		case 15:
			/*			newPropertyObject(party, PartyType::setIndustryClassificationCode, IndustryClassificationCodeType::new);
			setProperty(party.getIndustryClassificationCode(), IndustryClassificationCodeType::setValue, obj.toString());
			party.setIndustryClassificationCode(new IndustryClassificationCodeType());
			party.getIndustryClassificationCode().setValue(obj.toString());*/

//			InstanceFactory.createAndSetLeafProperty(party, party.getIndustryClassificationCode(), IndustryClassificationCodeType.class, obj, String.class);

			party.setClassificationCode(obj.toString());
			break;
		case 16:
			break;
		case 17:
			//			party.getPostalAddress().getStreetName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getStreetName(), StreetNameType.class, obj, String.class);*/

			party.setPostalStreetName(obj.toString());
			break;
		case 18:
			//			party.getPostalAddress().getBuildingNumber().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getBuildingNumber(), BuildingNumberType.class, obj, String.class);*/

			party.setPostalBuildingNumber(obj.toString());
			break;
		case 19:
			//			party.getPostalAddress().getFloor().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getFloor(), FloorType.class, obj, String.class);*/

			party.setPostalFloor(obj.toString());
			break;
		case 20:
			//			party.getPostalAddress().getRoom().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getRoom(), RoomType.class, obj, String.class);*/
			party.setPostalRoom(obj.toString());
			break;
		case 21:
			//			party.getPostalAddress().getBuildingName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getBuildingName(), BuildingNameType.class, obj, String.class);*/

			party.setPostalBuildingName(obj.toString());
			break;
		case 22:
			//			party.getPostalAddress().getCitySubdivisionName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getCitySubdivisionName(), CitySubdivisionNameType.class, obj, String.class);*/
			party.setPostalCitySubdivision(obj.toString());
			break;
		case 23:
			//			party.getPostalAddress().getCityName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getCityName(), CityNameType.class, obj, String.class);*/
			party.setPostalCityName(obj.toString());
			break;
		case 24:
			//			party.getPostalAddress().getPostalZone().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getPostalZone(), PostalZoneType.class, obj, String.class);*/
			party.setPostalPostalZone(obj.toString());
			break;
		case 25:
			//			party.getPostalAddress().getCountrySubentity().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress(), party.getPostalAddress().getCountrySubentity(), CountrySubentityType.class, obj, String.class);*/
			party.setPostalCountrySubentity(obj.toString());
			break;
		case 26:
			//			party.getPostalAddress().getCountry().getName().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getPostalAddress(), AddressType.class, "setPostalAddress");
			InstanceFactory.checkAndCreateNodeProperty(party.getPostalAddress(), party.getPostalAddress().getCountry(), CountryType.class);
			InstanceFactory.createAndSetLeafProperty(party.getPostalAddress().getCountry(), party.getPostalAddress().getCountry().getName(), NameType.class, obj, String.class);*/
			party.setPostalCountry(obj.toString());
			break;
		case 27:
			break;
		case 28:
			//			party.getContact().getName().setValue(obj.toString());

			/*			newPropertyObject(party, PartyType::setIndustryClassificationCode, IndustryClassificationCodeType::new);
			setProperty(party.getIndustryClassificationCode(), IndustryClassificationCodeType::setValue, obj.toString());*/

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getName(), NameType.class, obj, String.class);*/

			party.setContactName(obj.toString());
			break;
		case 29:
			//			party.getContact().getTelephone().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getTelephone(), TelephoneType.class, obj, String.class);*/

			party.setContactTelephone(obj.toString());
			break;
		case 30:
			//			party.getContact().getTelefax().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getTelefax(), TelefaxType.class, obj, String.class);
*/
			party.setContactTelefax(obj.toString());
			break;
		case 31:
			//			party.getContact().getElectronicMail().setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact(), party.getContact().getElectronicMail(), ElectronicMailType.class, obj, String.class);*/
			party.setContactEmail(obj.toString());
			break;
		case 32:
//			party.getContact().getNote().get(0).setValue(obj.toString());

/*			InstanceFactory.checkAndCreateNodeProperty(party, party.getContact(), ContactType.class);
			InstanceFactory.checkAndCreateListProperty(party.getContact().getNote(), NoteType.class);
			InstanceFactory.createAndSetLeafProperty(party.getContact().getNote(), party.getContact().getNote().get(0), NoteType.class, obj, String.class);*/
			party.setContactNote(obj.toString());
			break;
		case 33:
			//			party.getWebsiteURI().setValue(obj.toString());

//			InstanceFactory.createAndSetLeafProperty(party, party.getWebsiteURI(), WebsiteURIType.class, obj, String.class);
			party.setWebsite(obj.toString());
			break;
		case 34:
			party.setIndustryClassificationCode(obj.toString());
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
		return (!tableEditable) || (column == 0 || row == 4 || row == 16 || row == 27) ? false : true;
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
/*	public static void test(PartyType party)
	{
		bindToMethodWithSupplier(PartyType::setIndustryClassificationCode, IndustryClassificationCodeType::new).accept(party);
		bindToMethodWithArgument(IndustryClassificationCodeType::setValue, "123456").accept(party.getIndustryClassificationCode());
	}*/

}
