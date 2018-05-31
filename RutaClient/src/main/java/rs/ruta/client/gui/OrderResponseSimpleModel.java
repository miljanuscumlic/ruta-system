package rs.ruta.client.gui;

import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AcceptedIndicatorType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.RejectionNoteType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.InstanceFactory;

public class OrderResponseSimpleModel extends DefaultTableModel
{
	private static final long serialVersionUID = 3487280406309398968L;

	//must be static because it's used in getRowCount method which is called from the
	//constructor of the super class
	private static String[] rowNames = { "Response Document Type", "ID", "Issue Date", "Acceptance Indicator", "Note", "Rejection Note"};
	private static int NOTE_INDEX = 4;
	private static int REJECTION_NOTE_INDEX = 5;
	private OrderResponseSimpleType orderResponse;
	private boolean editable;
	private boolean accepted;

	public OrderResponseSimpleModel(OrderResponseSimpleType orderResponse, boolean editable, boolean accepted)
	{
		super();
		this.orderResponse = orderResponse;
		this.editable = editable;
		this.accepted = accepted;
	}

	public OrderResponseSimpleType getOrderResponse()
	{
		return orderResponse;
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column == 1 && (row == NOTE_INDEX || row == REJECTION_NOTE_INDEX)) : false;
	}

	@Override
	public int getRowCount()
	{
		if(accepted)
			return rowNames.length - 1;
		else
			return rowNames.length;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public String getColumnName(int column)
	{
		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(columnIndex == 0)
		{
			return rowNames[rowIndex];
		}
		else if(columnIndex == 1)
		{
			switch(rowIndex)
			{
			case 0:
				return "Order Response Simple";
			case 1:
				return orderResponse.getIDValue();
			case 2:
				return InstanceFactory.getLocalDateAsString(orderResponse.getIssueDateValue());
			case 3:
				final AcceptedIndicatorType acceptedIndicator = orderResponse.getAcceptedIndicator();
				if(acceptedIndicator != null)
					return acceptedIndicator.isValue();
				else
					return null;
			case 4:
				if(orderResponse.getNoteCount() != 0)
					return orderResponse.getNoteAtIndex(0).getValue();
				else
					return null;
			case 5:
				if(orderResponse.getRejectionNoteCount() != 0)
					return orderResponse.getRejectionNoteAtIndex(0).getValue();
				else
					return null;
			default:
				return null;
			}
		}
		else
			return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(columnIndex == 1)
		{
			if(rowIndex == NOTE_INDEX)
				orderResponse.addNote(new NoteType((String) aValue));
			if(!accepted && rowIndex == REJECTION_NOTE_INDEX)
					orderResponse.addRejectionNote(new RejectionNoteType((String) aValue));
		}
	}
}