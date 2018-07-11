package rs.ruta.client.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.common.InstanceFactory;

public abstract class AbstractOrderResponseDialog extends AbstractOrderDialog
{
	private static final long serialVersionUID = 4323876859820554276L;
	private static OrderResponseType orderResponse; // have to be static because of the reference from the inner static class
	private boolean sendPressed;
	private JTable headerTable;

	/**
	 * Creates dialog for creating {@link OrderResponseType}.
	 * @param owner parent frame
	 * @param orderResponse {@link OrderResponseType} to display
	 * @param editable whether the {@link OrderResponseType} should be editable
	 */
	public AbstractOrderResponseDialog(RutaClientFrame owner, OrderResponseType orderResponse, boolean editable)
	{
		super(owner, orderResponse.getOrderLine(), editable);
		AbstractOrderResponseDialog.orderResponse = orderResponse;
		final HeaderTableModel headerTableModel = new HeaderTableModel(editable);
		headerTable = createHeaderTable(headerTableModel);
		headerPanel.add(new JScrollPane(headerTable));
		final Dimension buttonPanelSize = new Dimension(
				((int) headerTable.getPreferredSize().getWidth()),
				((int) headerTable.getPreferredSize().getHeight()) + 5);
		headerPanel.setPreferredSize(buttonPanelSize);
		headerTable.addMouseListener(stopEditingListener);
	}

	@Override
	protected void stopEditing()
	{
		super.stopEditing();
		if(headerTable.isEditing())
			headerTable.getCellEditor().stopCellEditing();
	}

	public OrderResponseType getOrderResponse()
	{
		return orderResponse;
	}

	public void setOrderResponse(OrderResponseType orderResponse)
	{
		AbstractOrderResponseDialog.orderResponse = orderResponse;
	}

	public boolean isSendPressed()
	{
		return sendPressed;
	}

	public void setSendPressed(boolean sendPressed)
	{
		this.sendPressed = sendPressed;
	}

	private JTable createHeaderTable(HeaderTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setTableHeader(null);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(150);
		columnModel.getColumn(1).setPreferredWidth(350);

		return table;
	}

	private static class HeaderTableModel extends DefaultTableModel
	{
		private static final long serialVersionUID = -3976099918438412530L;
		private static final String [] rowNames = { Messages.getString("AbstractOrderResponseDialog.0"), Messages.getString("AbstractOrderResponseDialog.1"), Messages.getString("AbstractOrderResponseDialog.2"), Messages.getString("AbstractOrderResponseDialog.3") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		private boolean editable;

		public HeaderTableModel(boolean editable)
		{
			super();
			this.editable = editable;
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
		public boolean isCellEditable(int row, int column)
		{
			return editable ? column == 1 && row == 3 : false;
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			if(column == 0)
				return rowNames[row];
			else if(column == 1)
			{
				switch(row)
				{
				case 0 :
					return Messages.getString("AbstractOrderResponseDialog.4"); //$NON-NLS-1$
				case 1:
					return orderResponse.getIDValue();
				case 2:
					return InstanceFactory.getLocalDateAsString(orderResponse.getIssueDateValue());
				case 3:
					if(orderResponse.getNoteCount() != 0)
						return orderResponse.getNoteAtIndex(0).getValue();
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
		public void setValueAt(Object aValue, int row, int column)
		{
			if(column == 1 && row == 3)
			{
				orderResponse.getNote().clear();
				orderResponse.addNote(new NoteType((String) aValue));
			}
		}
	}
}
