package rs.ruta.client.gui;

import javax.swing.table.*;

import rs.ruta.client.Party;

/**
 * Data model for a orderLinesTable displaying {@link PartyType} properties of a party.
 */
public class PartyTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -6097474546942059715L;

	private static String[] rowNames =
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
		super();
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
			party.setPartySimpleName(obj.toString());
			break;
		case 1:
			party.setRegistrationName(obj.toString());
			break;
		case 2:
			party.setCompanyID(obj.toString());
			break;
		case 3:
			party.setRegistrationDate(obj.toString());
			break;
		case 4:
			break;
		case 5:
			party.setRegistrationStreetName(obj.toString());
			break;
		case 6:
			party.setRegistrationBuildingNumber(obj.toString());
			break;
		case 7:
			party.setRegistrationFloor(obj.toString());
			break;
		case 8:
			party.setRegistrationRoom(obj.toString());
			break;
		case 9:
			party.setRegistrationBuildingName(obj.toString());
			break;
		case 10:
			party.setRegistrationCitySubdivision(obj.toString());
			break;
		case 11:
			party.setRegistrationCityName(obj.toString());
			break;
		case 12:
			party.setRegistrationPostalZone(obj.toString());
			break;
		case 13:
			party.setRegistrationCountrySubentity(obj.toString());
			break;
		case 14:
			party.setRegistrationCountry(obj.toString());
			break;
		case 15:
			party.setClassificationCode(obj.toString());
			break;
		case 16:
			break;
		case 17:
			party.setPostalStreetName(obj.toString());
			break;
		case 18:
			party.setPostalBuildingNumber(obj.toString());
			break;
		case 19:
			party.setPostalFloor(obj.toString());
			break;
		case 20:
			party.setPostalRoom(obj.toString());
			break;
		case 21:
			party.setPostalBuildingName(obj.toString());
			break;
		case 22:
			party.setPostalCitySubdivision(obj.toString());
			break;
		case 23:
			party.setPostalCityName(obj.toString());
			break;
		case 24:
			party.setPostalPostalZone(obj.toString());
			break;
		case 25:
			party.setPostalCountrySubentity(obj.toString());
			break;
		case 26:
			party.setPostalCountry(obj.toString());
			break;
		case 27:
			break;
		case 28:
			party.setContactName(obj.toString());
			break;
		case 29:
			party.setContactTelephone(obj.toString());
			break;
		case 30:
			party.setContactTelefax(obj.toString());
			break;
		case 31:
			party.setContactEmail(obj.toString());
			break;
		case 32:
			party.setContactNote(obj.toString());
			break;
		case 33:
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

}