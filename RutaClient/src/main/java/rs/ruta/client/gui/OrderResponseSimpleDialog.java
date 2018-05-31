package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.RejectionNoteType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.InstanceFactory;

public class OrderResponseSimpleDialog extends AbstractOrderResponseSimpleDialog
{
	private static final long serialVersionUID = 3259789645341776081L;
	boolean sendPressed;

	/**
	 * Creates {@link OrderResponseSimpleDialog} for making new {@link OrderResponseSimpleType} document.
	 * @param owner parent frame
	 * @param applicationResponse Order Response Simple to show or amend
	 * @param accepted true if Order is to be accepted; false otherwise
	 * @param editable TODO
	 * @param obsoleteCatalogue true if Order has been sent with the reference to some previous version
	 * of the Catalogue
	 */
	public OrderResponseSimpleDialog(RutaClientFrame owner, OrderResponseSimpleType orderResponseSimple,
			boolean accepted, boolean editable, boolean obsoleteCatalogue)
	{
		super(owner, orderResponseSimple, accepted, editable);

		MouseAdapter tableFocus = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				if(responseTable.isEditing())
					responseTable.getCellEditor().stopCellEditing();
			}
		};
		addMouseListener(tableFocus);

		JButton sendButton = new JButton("Send");
		JButton discardButton = new JButton("Discard");
		JButton closeButton = new JButton("Close");

		sendButton.addActionListener(event ->
		{
			if(responseTable.isEditing())
				responseTable.getCellEditor().stopCellEditing();
			sendPressed = true;
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
			buttonPanel.add(closeButton);
			getRootPane().setDefaultButton(closeButton);
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