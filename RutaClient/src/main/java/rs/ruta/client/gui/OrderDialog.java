package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.LineItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderLineType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public class OrderDialog extends JDialog
{
	private static final long serialVersionUID = -814272597710083510L;
	private boolean sendPressed;
	private OrderType order;

	public OrderDialog(RutaClientFrame owner, OrderType order, boolean editable, boolean preview)
	{
		super(owner, true);
		setSize(700, 500);
		setLocationRelativeTo(owner);
		this.order = order;
		final JPanel orderPanel = new JPanel(new BorderLayout());
		final OrderTableModel orderTableModel = new OrderTableModel(order, editable);
		final JTable table = createOrderTable(orderTableModel);

		orderPanel.add(new JScrollPane(table));
		add(orderPanel, BorderLayout.CENTER);

		MouseAdapter tableLostFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(table.isEditing())
					table.getCellEditor().stopCellEditing();
			}
		};
		addMouseListener(tableLostFocus);
		table.getTableHeader().addMouseListener(tableLostFocus);

		final JPanel buttonPanel = new JPanel();
		final JButton sendButton = new JButton("Send");
		final JButton previewButton = new JButton("Preview");
		final JButton cancelButton = new JButton("Cancel");

		sendButton.addActionListener(event ->
		{
			if(table.isEditing())
				table.getCellEditor().stopCellEditing();
			trimOrder(order);
			numberOrderLines();
			sendPressed = true;
			setVisible(false);
		});
		getRootPane().setDefaultButton(sendButton);
		if(!preview)
			buttonPanel.add(sendButton);

		previewButton.addActionListener(event ->
		{
			if(table.isEditing())
				table.getCellEditor().stopCellEditing();

			final OrderType previewOrder = order.clone();
			if(trimOrder(previewOrder))
			{
				final OrderDialog previewDialog = new OrderDialog(owner, previewOrder, false, true);
				previewDialog.setTitle("Order Preview");
				previewDialog.setVisible(true);
			}
			else
			{
				JOptionPane.showMessageDialog(OrderDialog.this, "Removal of items with quantity of zero failed!",
						 "Error message", JOptionPane.ERROR_MESSAGE);
				final OrderDialog previewDialog = new OrderDialog(owner, previewOrder, false, true);
				previewDialog.setTitle("Order Preview");
				previewDialog.setVisible(true);
			}
		});
		if(!preview)
			buttonPanel.add(previewButton);

		cancelButton.addActionListener(event ->
		{
			sendPressed = false;
			setVisible(false);
		});
		cancelButton.setVerifyInputWhenFocusTarget(false);
		if(preview)
			cancelButton.setText("Close");
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	public boolean isSendPressed()
	{
		return sendPressed;
	}

	public void setSendPressed(boolean sendPressed)
	{
		this.sendPressed = sendPressed;
	}

	public OrderType getOrder()
	{
		return order;
	}

	private JTable createOrderTable(DefaultTableModel tableModel)
	{
		JTable table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(20);
		columnModel.getColumn(1).setPreferredWidth(100);
		columnModel.getColumn(2).setPreferredWidth(100);
		columnModel.getColumn(3).setPreferredWidth(100);
		columnModel.getColumn(4).setPreferredWidth(100);

		return table;
	}

	/**
	 * Removes all items from the {@link OrderType order} that have quantity that is a {@code null} value or 0.
	 * @param order order to process
	 * @return true if trimming is done without throwing any exception; false otherwise
	 */
	private boolean trimOrder(OrderType order)
	{
		boolean success = true;
		try
		{
			order.getOrderLine().removeIf(orderLine ->
			orderLine.getLineItem().getQuantityValue() == null ||
			new BigDecimal(0).compareTo(orderLine.getLineItem().getQuantityValue()) >= 0);
		}
		catch(Exception e)
		{
			success = false;
		}
		return success;
	}

	private void numberOrderLines()
	{
		int lineNumber = 0;

		final List<OrderLineType> orderLines = order.getOrderLine();
		for(OrderLineType orderLine: orderLines)
		{
			orderLine.getLineItem().setID(String.valueOf(lineNumber++));
		}
	}
}