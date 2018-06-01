package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.correspondence.Correspondence;

public class OrderResponseSimpleDialog extends AbstractOrderResponseSimpleDialog
{
	private static final long serialVersionUID = 3259789645341776081L;
	boolean sendPressed;

	/**
	 * Creates {@link OrderResponseSimpleDialog} for making new {@link OrderResponseSimpleType} document.
	 * {@code corr} argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to some non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending atempt of it could be tried.
	 * @param owner parent frame
	 * @param orderResponseSimple Order Response Simple to show or amend
	 * @param editable whether the {@link OrderResponseSimpleType} should be editable
	 * @param obsoleteCatalogue true if Order has been sent with the reference to some previous version
	 * of the Catalogue
	 * @param corr {@link Correspondence} of the {@link OrderResponseSimpleType}
	 */
	public OrderResponseSimpleDialog(RutaClientFrame owner, OrderResponseSimpleType orderResponseSimple,
			boolean editable, boolean obsoleteCatalogue, Correspondence corr)
	{
		super(owner, orderResponseSimple, editable);

		JButton sendButton = new JButton("Send");
		JButton resendButton = new JButton("Resend");
		JButton discardButton = new JButton("Discard");
		JButton closeButton = new JButton("Close");

		sendButton.addActionListener(event ->
		{
			stopEditing();
			sendPressed = true;
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
			getRootPane().setDefaultButton(sendButton);
			if(!obsoleteCatalogue)
				buttonPanel.add(discardButton);
		}
		else
		{
			getRootPane().setDefaultButton(closeButton);
			if(corr != null)
			{
				buttonPanel.add(resendButton);
				getRootPane().setDefaultButton(resendButton);
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
}