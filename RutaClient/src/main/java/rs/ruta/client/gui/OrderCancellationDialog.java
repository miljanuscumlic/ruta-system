package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CancellationNoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.InstanceFactory;

public class OrderCancellationDialog extends JDialog
{
	private static final long serialVersionUID = -6846406654876729897L;
	private static OrderCancellationType orderCancellation; // have to be static because of the reference from the inner static class
	private boolean sendPressed;
	protected JPanel buttonPanel;
	private JTable cancellationTable;
	/**
	 * Creates dialog for creating {@link OrderCancellationType}.
	 * @param owner parent frame
	 * @param orderCancellation {@link OrderCancellationType} to display
	 * @param editable whether the {@link OrderCancellationType} should be editable
	 * @param corr {@link Correspondence} if {@link OrderCancellationType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 */
	public OrderCancellationDialog(RutaClientFrame owner, OrderCancellationType orderCancellation, boolean editable, Correspondence corr)
	{
		super(owner, true);
		OrderCancellationDialog.orderCancellation = orderCancellation;
		setSize(700, 180);
		setLocationRelativeTo(owner);
		final JPanel cancellationPanel = new JPanel(new BorderLayout());

		final CancellationTableModel cancellationTableModel = new CancellationTableModel(editable);
		cancellationTable = createCancellationTable(cancellationTableModel);
		cancellationPanel.add(new JScrollPane(cancellationTable));
		final Dimension buttonPanelSize = new Dimension(
				((int) cancellationTable.getPreferredSize().getWidth()),
				((int) cancellationTable.getPreferredSize().getHeight()) + 5);
		cancellationPanel.setPreferredSize(buttonPanelSize);

		cancellationPanel.add(new JScrollPane(cancellationTable));
		add(cancellationPanel, BorderLayout.CENTER);

		MouseAdapter tableFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				stopEditing();
			}
		};
		addMouseListener(tableFocus);

		buttonPanel = new JPanel();
		buttonPanel.addMouseListener(tableFocus);
		add(buttonPanel, BorderLayout.SOUTH);

		JButton sendButton = new JButton(Messages.getString("OrderCancellationDialog.0")); 
		JButton discardButton = new JButton(Messages.getString("OrderCancellationDialog.1")); 
		JButton closeButton = new JButton(Messages.getString("OrderCancellationDialog.2")); 

		sendButton.addActionListener(event ->
		{
			stopEditing();
			String cancellationNote = null;
			boolean missing = false;
			try
			{
				cancellationNote = orderCancellation.getCancellationNoteAtIndex(0).getValue();
			}
			catch(Exception e)
			{
				missing = true;
			}

			if(!missing && !"".equals(cancellationNote)) 
			{
				sendPressed = true;
				if(corr != null)
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
							owner.appendToConsole(new StringBuilder(Messages.getString("OrderCancellationDialog.4")), Color.RED); 
						}
					}).start();
				}
				setVisible(false);
			}
			else
			{
				JOptionPane.showMessageDialog(OrderCancellationDialog.this, Messages.getString("OrderCancellationDialog.5"), 
						Messages.getString("OrderCancellationDialog.6"), JOptionPane.ERROR_MESSAGE); 
			}

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

	public OrderCancellationType getOrderCancellation()
	{
		return orderCancellation;
	}

	private JTable createCancellationTable(CancellationTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setTableHeader(null);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(100);
		columnModel.getColumn(1).setPreferredWidth(400);

		return table;
	}

	private void stopEditing()
	{
		if(cancellationTable.isEditing())
			cancellationTable.getCellEditor().stopCellEditing();
	}

	private static class CancellationTableModel extends DefaultTableModel
	{
		private static final long serialVersionUID = -3976099918438412530L;
		private static final String [] rowNames = { Messages.getString("OrderCancellationDialog.7"), Messages.getString("OrderCancellationDialog.8"), Messages.getString("OrderCancellationDialog.9"), Messages.getString("OrderCancellationDialog.10"), Messages.getString("OrderCancellationDialog.11") };     
		private boolean editable;

		public CancellationTableModel(boolean editable)
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
			return editable ? column == 1 && (row == 3 || row == 4) : false;
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
					return Messages.getString("OrderCancellationDialog.12"); 
				case 1:
					return orderCancellation.getIDValue();
				case 2:
					return InstanceFactory.getLocalDateAsString(orderCancellation.getIssueDateValue());
				case 3:
					if(orderCancellation.getCancellationNoteCount() != 0)
						return orderCancellation.getCancellationNoteAtIndex(0).getValue();
					else
						return null;
				case 4:
					if(orderCancellation.getNoteCount() != 0)
						return orderCancellation.getNoteAtIndex(0).getValue();
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
			if(column == 1)
			{
				if(row == 3)
				{
					orderCancellation.getCancellationNote().clear();
					orderCancellation.addCancellationNote(new CancellationNoteType((String) aValue));
				}
				else if(row == 4)
				{
					orderCancellation.getNote().clear();
					orderCancellation.addNote(new NoteType((String) aValue));
				}
			}
		}
	}
}