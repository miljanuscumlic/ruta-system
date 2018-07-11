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
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending attempt of it could be tried.
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

		JButton sendButton = new JButton(Messages.getString("OrderResponseSimpleDialog.0")); //$NON-NLS-1$
		JButton resendButton = new JButton(Messages.getString("OrderResponseSimpleDialog.1")); //$NON-NLS-1$
		JButton discardButton = new JButton(Messages.getString("OrderResponseSimpleDialog.2")); //$NON-NLS-1$
		JButton closeButton = new JButton(Messages.getString("OrderResponseSimpleDialog.3")); //$NON-NLS-1$

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
					owner.appendToConsole(new StringBuilder(Messages.getString("OrderResponseSimpleDialog.4")), Color.RED); //$NON-NLS-1$
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
			sendButton.requestFocusInWindow();
			if(!obsoleteCatalogue)
				buttonPanel.add(discardButton);
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
}