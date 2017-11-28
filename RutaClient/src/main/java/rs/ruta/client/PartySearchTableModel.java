package rs.ruta.client;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ElectronicMailType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.StreetNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.TelephoneType;
import rs.ruta.common.InstanceFactory;

public class PartySearchTableModel extends SearchTableModel<PartyType>
{
	private static final long serialVersionUID = -2704312134331885642L;
	private static String[] columnNames =
		{
			"No.", "Party name", "Company ID", "Street", "Building number", "City", "Country",
			"Classification code", "Telephone", "Email", "Website", "Industry Classification Code"
		};

	public PartySearchTableModel(boolean editable)
	{
		super(editable);
	}

	public PartySearchTableModel(Search<PartyType> search, boolean editable)
	{
		super(search, editable);
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
	public boolean isCellEditable(int row, int column)
	{
		return editable;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		PartyType party = search.getResults().get(rowIndex);
		switch(columnIndex)
		{
		case 0:
			return rowIndex + 1;
		case 1:
			return party.getPartyNameCount() == 0 ? null : party.getPartyNameAtIndex(0).getNameValue();
		case 2:
			return party.getPartyLegalEntityCount() == 0
					 ? null : party.getPartyLegalEntityAtIndex(0).getCompanyIDValue();
		case 3:
			return party.getPartyLegalEntityCount() == 0 ? null :
				InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getStreetName(), StreetNameType::getValue);
		case 4:
			return party.getPartyLegalEntityCount() == 0 ? null :
				InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getBuildingNumber(), BuildingNumberType::getValue);
		case 5:
			return party.getPartyLegalEntityCount() == 0 ? null :
				InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getCityName(), CityNameType::getValue);
		case 6:
			return party.getPartyLegalEntityCount() == 0 ? null :
				InstanceFactory.getPropertyOrNull(party.getPartyLegalEntityAtIndex(0).getRegistrationAddress().getCountry().getName(), NameType::getValue);
		case 7:
			return party.getIndustryClassificationCodeValue();
		case 8:
			return party.getContact() == null ? null : party.getContact().getTelephoneValue();
				//InstanceFactory.getPropertyOrNull(party.getContact().getTelephone(), TelephoneType::getValue);
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
}
