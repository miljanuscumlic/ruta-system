package rs.ruta.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.InstanceFactory;

public class OrderChangeDialog extends AbstractOrderDialog
{
	private static final long serialVersionUID = 4323876859820554276L;
	private static OrderChangeType orderChange; // have to be static because of the reference from the inner static class
	private boolean sendPressed;
	private JTable headerTable;

	/**
	 * Creates dialog for creating {@link OrderChangeType}.
	 * @param owner parent frame
	 * @param orderChange {@link OrderChangeType} to display
	 * @param editable whether the {@link OrderChangeType} should be editable
	 * @param corr {@link Correspondence} if {@link OrderChangeType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 */
	public OrderChangeDialog(RutaClientFrame owner, OrderChangeType orderChange, boolean editable, Correspondence corr)
	{
		super(owner, orderChange.getOrderLine(), editable);
		OrderChangeDialog.orderChange = orderChange;
		final HeaderTableModel headerTableModel = new HeaderTableModel(editable);
		headerTable = createHeaderTable(headerTableModel);
		headerPanel.add(new JScrollPane(headerTable));
		final Dimension buttonPanelSize = new Dimension(
				((int) headerTable.getPreferredSize().getWidth()),
				((int) headerTable.getPreferredSize().getHeight()) + 5);
		headerPanel.setPreferredSize(buttonPanelSize);
		headerTable.addMouseListener(stopEditingListener);

		JButton sendButton = new JButton("Send"); 
		JButton discardButton = new JButton("Discard"); 
		JButton closeButton = new JButton("Close"); 

		sendButton.addActionListener(event ->
		{
			stopEditing();
			sendPressed = true;
			if(editable)
			{
				trimOrderLines(orderChange.getOrderLine());
				numberOrderLines(orderChange.getOrderLine());
			}
			else if(corr != null)
			{
				new Thread(() ->
				{
					try
					{
						if(!corr.isAlive())
							corr.start();
						corr.waitThreadBlocked();
						corr.proceed();
					}
					catch(Exception e)
					{
						owner.appendToConsole(new StringBuilder("Correspondence has been interrupted!"), Color.RED); 
					}
				}).start();
			}
			setVisible(false);
		});

		discardButton.addActionListener(event ->
		{
			sendPressed = false;
			setVisible(false);
		});

		closeButton.addActionListener(event ->
		{
			sendPressed = false;
			setVisible(false);
		});

		if(editable)
		{
			buttonPanel.add(sendButton);
			buttonPanel.add(discardButton);
			getRootPane().setDefaultButton(sendButton);
			sendButton.requestFocusInWindow();
		}
		else
		{
			buttonPanel.add(closeButton);
			getRootPane().setDefaultButton(closeButton);
			closeButton.requestFocusInWindow();
		}
	}

	public boolean isSendPressed()
	{
		return sendPressed;
	}

	public void setSendPressed(boolean sendPressed)
	{
		this.sendPressed = sendPressed;
	}

	public OrderChangeType getOrderChange()
	{
		return orderChange;
	}


	@Override
	protected void stopEditing()
	{
		super.stopEditing();
		if(headerTable.isEditing())
			headerTable.getCellEditor().stopCellEditing();
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
		private static final String [] rowNames = { "Document Type", "ID", "Issue Date", "Note" };    
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
					return "Order Change"; 
				case 1:
					return orderChange.getIDValue();
				case 2:
					return InstanceFactory.getLocalDateAsString(orderChange.getIssueDateValue());
				case 3:
					if(orderChange.getNoteCount() != 0)
						return orderChange.getNoteAtIndex(0).getValue();
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
				orderChange.getNote().clear();
				orderChange.addNote(new NoteType((String) aValue));
			}
		}
	}
}