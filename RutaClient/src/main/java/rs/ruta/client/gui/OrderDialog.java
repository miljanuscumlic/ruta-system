package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.client.correspondence.Correspondence;

public class OrderDialog extends AbstractOrderDialog
{
	private static final long serialVersionUID = -814272597710083510L;
	private boolean sendPressed;
	private OrderType order;

	/**
	 * Creates new Order Dialogue displaying its orderLines line items. {@code corr} argument should be set to {@code null}
	 * when new {@code Order} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order} failed to be delievered and new sending atempt of it could be tried.
	 * @param owner parent frame of this dialogue
	 * @param order {@link OrderType order} to display
	 * @param editable whether the Order is editable i.e. its quantity column
	 * @param corr {@link Correspondence} of the {@link OrderType}
	 */
	public OrderDialog(RutaClientFrame owner, OrderType order, boolean editable, Correspondence corr)
	{
		super(owner, order.getOrderLine(), editable);
		this.order = order;
		final JButton sendButton = new JButton("Send");
		final JButton resendButton = new JButton("Resend");
		final JButton previewButton = new JButton("Preview");
		final JButton discardButton = new JButton("Discard");
		final JButton closeButton = new JButton("Close");

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

		resendButton.addActionListener(event ->
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
			setVisible(false);
		});

		previewButton.addActionListener(event ->
		{
			stopEditing();
			final OrderType previewOrder = order.clone();
			if(trimOrderLines(previewOrder.getOrderLine()))
			{
//				final PreviewOrderDialog previewDialog = new PreviewOrderDialog(owner, previewOrder);
//				previewDialog.setTitle("Order Preview");
//				previewDialog.setVisible(true);
				owner.showOrderDialog("Preview Order", previewOrder, false, null);
			}
			else
			{
				JOptionPane.showMessageDialog(OrderDialog.this, "Removal of items with quantity of zero has failed!",
						 "Error message", JOptionPane.ERROR_MESSAGE);
//				final PreviewOrderDialog previewDialog = new PreviewOrderDialog(owner, previewOrder);
//				previewDialog.setTitle("Order Preview");
//				previewDialog.setVisible(true);
				owner.showOrderDialog("Preview Order", previewOrder, false, null);
			}
		});
//		buttonPanel.add(previewButton);

		discardButton.addActionListener(event ->
		{
			sendPressed = false;
			setVisible(false);
		});
		discardButton.setVerifyInputWhenFocusTarget(false);

		getRootPane().setDefaultButton(closeButton);
		closeButton.requestFocusInWindow();
		closeButton.addActionListener(event ->
		{
			setVisible(false);
		});

		if(editable)
		{
			buttonPanel.add(sendButton);
			buttonPanel.add(previewButton);
			buttonPanel.add(discardButton);
			getRootPane().setDefaultButton(sendButton);
			sendButton.requestFocusInWindow();
		}
		else
		{
			getRootPane().setDefaultButton(closeButton);
			closeButton.requestFocusInWindow();
			if(corr != null)
			{
				buttonPanel.add(resendButton);
				getRootPane().setDefaultButton(resendButton);
				resendButton.requestFocusInWindow();
			}
			buttonPanel.add(closeButton);

		}

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