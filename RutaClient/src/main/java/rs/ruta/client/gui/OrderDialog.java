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
	 * some old {@code Order} failed to be delievered and new sending attempt of it could be tried.
	 * @param owner parent frame of this dialogue
	 * @param order {@link OrderType order} to display
	 * @param editable whether the Order is editable i.e. its quantity column
	 * @param corr {@link Correspondence} of the {@link OrderType}
	 */
	public OrderDialog(RutaClientFrame owner, OrderType order, boolean editable, Correspondence corr)
	{
		super(owner, order.getOrderLine(), editable);
		this.order = order;
		final JButton sendButton = new JButton(Messages.getString("OrderDialog.0")); //$NON-NLS-1$
		final JButton resendButton = new JButton(Messages.getString("OrderDialog.1")); //$NON-NLS-1$
		final JButton previewButton = new JButton(Messages.getString("OrderDialog.2")); //$NON-NLS-1$
		final JButton discardButton = new JButton(Messages.getString("OrderDialog.3")); //$NON-NLS-1$
		final JButton closeButton = new JButton(Messages.getString("OrderDialog.4")); //$NON-NLS-1$

		sendButton.addActionListener(event ->
		{
			stopEditing();
			final OrderType trimOrder = order.clone();
			trimOrderLines(trimOrder.getOrderLine());
			if(trimOrder.getOrderLineCount() == 0)
			{
				JOptionPane.showMessageDialog(OrderDialog.this, Messages.getString("OrderDialog.5"), //$NON-NLS-1$
						Messages.getString("OrderDialog.6"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
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
					owner.appendToConsole(new StringBuilder(Messages.getString("OrderDialog.7")), Color.RED); //$NON-NLS-1$
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
				owner.showOrderDialog(Messages.getString("OrderDialog.8"), previewOrder, false, null); //$NON-NLS-1$
			}
			else
			{
				JOptionPane.showMessageDialog(OrderDialog.this, Messages.getString("OrderDialog.9"), //$NON-NLS-1$
						 Messages.getString("OrderDialog.10"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
//				final PreviewOrderDialog previewDialog = new PreviewOrderDialog(owner, previewOrder);
//				previewDialog.setTitle("Order Preview");
//				previewDialog.setVisible(true);
				owner.showOrderDialog(Messages.getString("OrderDialog.11"), previewOrder, false, null); //$NON-NLS-1$
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