package rs.ruta.client.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BuildingNumberType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CityNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.StreetNameType;
import rs.ruta.client.BusinessParty;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.InstanceFactory;

public class PartnershipRequestListTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -1366607627023019652L;
	private static String[] columnNames =
		{
				Messages.getString("PartnershipRequestListTableModel.0"), Messages.getString("PartnershipRequestListTableModel.1"), Messages.getString("PartnershipRequestListTableModel.2"), Messages.getString("PartnershipRequestListTableModel.3"), Messages.getString("PartnershipRequestListTableModel.4"), Messages.getString("PartnershipRequestListTableModel.5")      
		};

	private  List<PartnershipRequest> requests = null;

	public PartnershipRequestListTableModel()
	{
		super();
	}

	public List<PartnershipRequest> getRequests()
	{
		return requests;
	}

	public void setRequests(List<PartnershipRequest> requests)
	{
		this.requests = requests;
	}

	/**
	 * Gets the {@link BusinessParty party} from the list of parties.
	 * @param index party's index
	 * @return party or {@code null} if parties field is {@code null}
	 */
	public PartnershipRequest getRequestAtIndex(int index)
	{
		return requests != null ? requests.get(index) : null;
	}

	@Override
	public int getRowCount()
	{
		return requests != null ? requests.size() : 0;
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
		PartnershipRequest request = requests.get(rowIndex);
		try
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				return request.getRequesterPartyName();
			case 2:
				return request.getRequestedPartyName();
			case 3:
				return InstanceFactory.getLocalDateTimeAsString(request.getIssueTime());
			case 4:
				return InstanceFactory.getLocalDateTimeAsString(request.getResponsedTime());
			case 5:
				if(!request.isResolved())
					return Messages.getString("PartnershipRequestListTableModel.6"); 
				else
					if(request.isAccepted())
						return Messages.getString("PartnershipRequestListTableModel.7"); 
					else
						return Messages.getString("PartnershipRequestListTableModel.8"); 
			default:
				return null;
			}
		}
		catch(Exception e)
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
