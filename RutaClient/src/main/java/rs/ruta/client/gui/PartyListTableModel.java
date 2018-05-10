package rs.ruta.client.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.StreetNameType;
import rs.ruta.client.BusinessParty;
import rs.ruta.common.InstanceFactory;

public class PartyListTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -1366607627023019652L;
	private static String[] columnNames =
		{
				"No.", "Party name", "Company ID", "Street", "Building number", "City", "Country",
				"Classification code", "Telephone", "Email", "Website", "Industry Classification Code"
		};
	private List<BusinessParty> parties = null;

	public PartyListTableModel()
	{
		super();
	}

	public List<BusinessParty> getParties()
	{
		return parties;
	}

	public void setParties(List<BusinessParty> parties)
	{
		this.parties = parties;
	}

	/**
	 * Gets the {@link BusinessParty party} from the list of parties.
	 * @param index party's index
	 * @return party or {@code null} if parties field is {@code null}
	 */
	public BusinessParty getPartyAtIndex(int index)
	{
		return parties != null ? parties.get(index) : null;
	}

	@Override
	public int getRowCount()
	{
		return parties != null ? parties.size() : 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		PartyType party = parties.get(rowIndex).getCoreParty();
		try
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				return party.getPartyNameCount() == 0 ? null : party.getPartyNameAtIndex(0).getNameValue();
			case 2:
				return party.getPartyLegalEntityCount() == 0 ?
						null : party.getPartyLegalEntityAtIndex(0).getCompanyIDValue();
			case 3:
				return party.getPartyLegalEntityCount() == 0 ? null :
					InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getStreetName(),
							StreetNameType::getValue);
			case 4:
				return party.getPartyLegalEntityCount() == 0 ? null :
					InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getBuildingNumber(),
							BuildingNumberType::getValue);
			case 5:
				return party.getPartyLegalEntityCount() == 0 ? null :
					InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getCityName(),
							CityNameType::getValue);
			case 6:
				return party.getPartyLegalEntityCount() == 0 ? null :
					InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getCountry().getName(),
							NameType::getValue);
			case 7:
				return party.getIndustryClassificationCodeValue();
			case 8:
				return party.getContact() == null ? null : party.getContact().getTelephoneValue();
				//InstanceFactory.getPropertyOrNull(party.getContact().getTelephone(), TelephoneType::getValue);
				//MMM: could put some speed measuring test to see which one is faster
			case 9:
				return party.getContact() == null ? null : party.getContact().getElectronicMailValue();
				//InstanceFactory.getPropertyOrNull(party.getContact().getElectronicMail(), ElectronicMailType::getValue);
			case 10:
				return party.getWebsiteURIValue();
			case 11:
				return party.getIndustryClassificationCodeValue();
			default:
				return null;
			}
		}
		catch(Exception e) // i.e when party.getPartyLegalEntityAtIndex(0).getRegistrationAddress() = null
		{
			//logger.error("Exception is ", e);
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}
