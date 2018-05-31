package rs.ruta.client.gui;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public class OrderDialog extends AbstractOrderDialog
{
	private static final long serialVersionUID = -814272597710083510L;
	private boolean sendPressed;
	private OrderType order;

	/**
	 * Creates new Order Dialogue displaying its orderLines line items
	 * @param owner parent frame of this dialogue
	 * @param orderLines {@link OrderType orderLines} to display
	 * @param editable whether the Order is editable i.e. its quantity column
	 */
	public OrderDialog(RutaClientFrame owner, OrderType order, boolean editable)
	{
		super(owner, order.getOrderLine(), editable);
		this.order = order;
		final JButton sendButton = new JButton("Send");
		final JButton previewButton = new JButton("Preview");
		final JButton cancelButton = new JButton("Discard");

		sendButton.addActionListener(event ->
		{
			stopEditing();
			final OrderType trimOrder = order.clone();
			trimOrderLines(trimOrder.getOrderLine());
			if(trimOrder.getOrderLineCount() == 0)
			{
				JOptionPane.showMessageDialog(OrderDialog.this, "There must be at least one line item with the quantity larger than zero!",
						"Error message", JOptionPane.ERROR_MESSAGE);
				sendPressed = false;
			}
			else
			{
				order.setOrderLine(trimOrder.getOrderLine());
				numberOrderLines(order.getOrderLine());
				sendPressed = true;
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(sendButton);
		buttonPanel.add(sendButton);

		previewButton.addActionListener(event ->
		{
			stopEditing();
			final OrderType previewOrder = order.clone();
			if(trimOrderLines(previewOrder.getOrderLine()))
			{
				final PreviewOrderDialog previewDialog = new PreviewOrderDialog(owner, previewOrder);
				previewDialog.setTitle("Order Preview");
				previewDialog.setVisible(true);
			}
			else
			{
				JOptionPane.showMessageDialog(OrderDialog.this, "Removal of items with quantity of zero has failed!",
						 "Error message", JOptionPane.ERROR_MESSAGE);
				final PreviewOrderDialog previewDialog = new PreviewOrderDialog(owner, previewOrder);
				previewDialog.setTitle("Order Preview");
				previewDialog.setVisible(true);
			}
		});
		buttonPanel.add(previewButton);

		cancelButton.addActionListener(event ->
		{
			sendPressed = false;
			setVisible(false);
		});
		cancelButton.setVerifyInputWhenFocusTarget(false);
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
}