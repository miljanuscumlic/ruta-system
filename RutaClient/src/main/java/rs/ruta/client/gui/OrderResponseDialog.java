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
	 * Creates dialog for creating {@link OrderResponseType}.
	 * @param owner parent frame
	 * @param orderResponse {@link OrderResponseType} to display
	 * @param editable whether the {@link OrderResponseType} should be editable
	 * @param corr {@link Correspondence} if {@link OrderResponseType} is already a part of it; {@code null}
	 * otherwise i.e. should be created and appended to it
	 */
	public OrderResponseDialog(RutaClientFrame owner, OrderResponseType orderResponse, boolean editable, Correspondence corr)
	{
		super(owner, orderResponse, editable);

		JButton sendButton = new JButton("Send");
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
		}
		else
		{
			buttonPanel.add(closeButton);
			getRootPane().setDefaultButton(closeButton);
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