package rs.ruta.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import rs.ruta.common.InstanceFactory;

public class ProcessInvoiceDialog extends AbstractInvoiceDialog
{
	private static final long serialVersionUID = -805322842356794669L;
	private String decision;

	public ProcessInvoiceDialog(RutaClientFrame owner, InvoiceType invoce)
	{
		super(owner, invoce.getInvoiceLine(), false);
		JButton acceptButton = new JButton(InstanceFactory.ACCEPT_INVOICE);
		JButton rejectButton = new JButton(InstanceFactory.REJECT_INVOICE);
		JButton postponeButton = new JButton(InstanceFactory.DECIDE_LATER);
		decision = null;

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				decision = ((JButton) event.getSource()).getText();
				setVisible(false);
			}
		};

		acceptButton.addActionListener(listener);
		rejectButton.addActionListener(listener);
		postponeButton.addActionListener(listener);

		getRootPane().setDefaultButton(postponeButton);
		postponeButton.requestFocusInWindow();
		buttonPanel.add(acceptButton);
		buttonPanel.add(rejectButton);
		buttonPanel.add(postponeButton);
	}

	public String getDecision()
	{
		return decision;
	}
}