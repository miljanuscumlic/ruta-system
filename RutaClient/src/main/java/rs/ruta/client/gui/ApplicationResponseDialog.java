package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.ResponseCodeType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.InstanceFactory;

public class ApplicationResponseDialog extends AbstractApplicationResponseDialog
{
	private static final long serialVersionUID = 3259789645341776081L;
	private boolean sendPressed;
	/**
	 * Creates {@link OrderResponseSimpleDialog} for making new {@link ApplicationResponseType} document.
	 * {@code corr} argument should be set to {@code null}
	 * when new {@code Order Response} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Order Response} failed to be delievered and new sending attempt of it could be tried.
	 * @param owner parent frame
	 * @param applicationResponse Application Response to show or amend
	 * @param editable true if Application Response document is to be created; false if is to be viewed only
	 * @param corr {@link Correspondence} of the {@link ApplicationResponseType}
	 *
	 */
	public ApplicationResponseDialog(RutaClientFrame owner, ApplicationResponseType applicationResponse,
			boolean editable, Correspondence corr)
	{
		super(owner, applicationResponse, editable);

		JButton sendButton = new JButton(Messages.getString("ApplicationResponseDialog.0")); //$NON-NLS-1$
		JButton resendButton = new JButton(Messages.getString("ApplicationResponseDialog.1")); //$NON-NLS-1$
		JButton discardButton = new JButton(Messages.getString("ApplicationResponseDialog.2")); //$NON-NLS-1$
		JButton closeButton = new JButton(Messages.getString("ApplicationResponseDialog.3")); //$NON-NLS-1$

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
					owner.appendToConsole(new StringBuilder(Messages.getString("ApplicationResponseDialog.4")), Color.RED); //$NON-NLS-1$
				}
			}).start();
			sendPressed = false;
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