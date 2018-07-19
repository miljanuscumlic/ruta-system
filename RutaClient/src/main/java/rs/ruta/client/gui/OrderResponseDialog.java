package rs.ruta.client.gui;

import java.awt.Color;
import javax.swing.JButton;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.client.correspondence.Correspondence;

public class OrderResponseDialog extends AbstractOrderResponseDialog
{
	private static final long serialVersionUID = 4323876859820554276L;
	private boolean sendPressed;

	/**
	 * Creates dialog for managing {@link OrderResponseType}. {@code corr} argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending attempt of it could be tried.
	 * @param owner parent frame
	 * @param orderResponse {@link OrderResponseType} to display
	 * @param editable whether the {@link OrderResponseType} should be editable
	 * @param corr {@link Correspondence} of the {@link OrderResponseType}
	 */
	public OrderResponseDialog(RutaClientFrame owner, OrderResponseType orderResponse, boolean editable, Correspondence corr)
	{
		super(owner, orderResponse, editable);

		JButton sendButton = new JButton("Send"); 
		JButton resendButton = new JButton("Resend"); 
		JButton discardButton = new JButton("Discard"); 
		JButton closeButton = new JButton("Close"); 

		sendButton.addActionListener(event ->
		{
			stopEditing();
			sendPressed = true;
			if(editable)
			{
				trimOrderLines(orderResponse.getOrderLine());
				numberOrderLines(orderResponse.getOrderLine());
			}
			setVisible(false);
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
	}

	@Override
	public boolean isSendPressed()
	{
		return sendPressed;
	}

	@Override
	public void setSendPressed(boolean sendPressed)
	{
		this.sendPressed = sendPressed;
	}

}