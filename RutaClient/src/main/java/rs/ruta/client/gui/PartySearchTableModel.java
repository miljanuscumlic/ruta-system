package rs.ruta.client.gui;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ElectronicMailType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.StreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TelephoneType;
import rs.ruta.client.Search;
import rs.ruta.common.InstanceFactory;

public class PartySearchTableModel extends SearchTableModel<PartyType>
{
	private static final long serialVersionUID = -2704312134331885642L;
	private static String[] columnNames =
		{
				"No.", "Party name", "Company ID", "Street", "Building number", "City", "Country",       
				"Classification code", "Telephone", "Email", "Website"    
		};

	public PartySearchTableModel()
	{
		super();
	}

	public PartySearchTableModel(Search<PartyType> search)
	{
		super(search);
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
		PartyType party = search.getResults().get(rowIndex);
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
			case 9:
				return party.getContact() == null ? null : party.getContact().getElectronicMailValue();
			case 10:
				return party.getWebsiteURIValue();

			default:
				return null;
			}
		}
		catch(Exception e) // i.e when party.getPartyLegalEntityAtIndex(0).getRegistrationAddress() = null
		{
			return null;
		}
	}

	public String getPartyID(int index)
	{
		return InstanceFactory.getPropertyOrNull(search.getResults().get(index).getPartyIdentificationAtIndex(0),
				PartyIdentificationType::getIDValue);
	}

	/**
	 * Gets the {@link PartyType party} with an {@code index} from the list of the search results.
	 * @param index index of the party
	 * @return {@code PartyType} object
	 */
	public PartyType getParty(int index)
	{
		return search.getResults().get(index);
	}
}