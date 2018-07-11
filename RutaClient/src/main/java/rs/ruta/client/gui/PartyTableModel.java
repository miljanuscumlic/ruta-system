package rs.ruta.client.gui;

import javax.swing.table.*;

import rs.ruta.client.Party;

/**
 * Data model for a table displaying {@link PartyType} properties of a party.
 */
public class PartyTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -6097474546942059715L;

	private static String[] rowNames =
		{
				Messages.getString("PartyTableModel.0"), Messages.getString("PartyTableModel.1"), Messages.getString("PartyTableModel.2"), Messages.getString("PartyTableModel.3"), Messages.getString("PartyTableModel.4"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				Messages.getString("PartyTableModel.5"), Messages.getString("PartyTableModel.6"), Messages.getString("PartyTableModel.7"), Messages.getString("PartyTableModel.8"), Messages.getString("PartyTableModel.9"), Messages.getString("PartyTableModel.10"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				Messages.getString("PartyTableModel.11"), Messages.getString("PartyTableModel.12"), Messages.getString("PartyTableModel.13"), Messages.getString("PartyTableModel.14"), Messages.getString("PartyTableModel.15"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				Messages.getString("PartyTableModel.16"), Messages.getString("PartyTableModel.17"), Messages.getString("PartyTableModel.18"), Messages.getString("PartyTableModel.19"), Messages.getString("PartyTableModel.20"), Messages.getString("PartyTableModel.21"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				Messages.getString("PartyTableModel.22"), Messages.getString("PartyTableModel.23"), Messages.getString("PartyTableModel.24"), Messages.getString("PartyTableModel.25"), Messages.getString("PartyTableModel.26"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				Messages.getString("PartyTableModel.27"), Messages.getString("PartyTableModel.28"), Messages.getString("PartyTableModel.29"), Messages.getString("PartyTableModel.30"), Messages.getString("PartyTableModel.31"), Messages.getString("PartyTableModel.32"), Messages.getString("PartyTableModel.33") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		};

	private Party party;
	private boolean editable;

	public PartyTableModel(boolean editable)
	{
		super();
		party = null;
		this.editable = editable;
	}

	public boolean isTableEditable()
	{
		return editable;
	}

	public void setTableEditable(boolean tableEditable)
	{
		this.editable = tableEditable;
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
			return Messages.getString("PartyTableModel.34"); //$NON-NLS-1$
		case 1:
			return Messages.getString("PartyTableModel.35"); //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return (!editable) || (column == 0 || row == 4 || row == 16 || row == 27) ? false : true;
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