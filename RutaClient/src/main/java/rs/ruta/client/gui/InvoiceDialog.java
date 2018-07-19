package rs.ruta.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import rs.ruta.client.correspondence.Correspondence;
import rs.ruta.common.InstanceFactory;

public class InvoiceDialog extends AbstractInvoiceDialog
{
	private static final long serialVersionUID = -814272597710083510L;
	private boolean sendPressed;
	private InvoiceType invoice;

	/**
	 * Creates new Invoice Dialogue displaying its orderLines line items. {@code corr} argument should be set to {@code null}
	 * when new {@code Invoice} is to be created or old one viewed and to appropriate non-{@code null} value only when
	 * some old {@code Invoice} failed to be delievered and new sending attempt of it could be tried.
	 * @param owner parent frame of this dialogue
	 * @param invoice {@link InvoiceType invoice} to display
	 * @param editable whether the Invoice is editable i.e. its quantity column
	 * @param corr {@link Correspondence} of the {@link InvoiceType}
	 */
	public InvoiceDialog(RutaClientFrame owner, InvoiceType invoice, boolean editable, Correspondence corr)
	{
		super(owner, invoice.getInvoiceLine(), editable);
		this.invoice = invoice;
		final JButton sendButton = new JButton("Send"); 
		final JButton resendButton = new JButton("Resend"); 
		final JButton discardButton = new JButton("Discard"); 
		final JButton closeButton = new JButton("Close"); 
		sendPressed = false;

		sendButton.addActionListener(event ->
		{
			stopEditing();
			numberInvoiceLines(invoice.getInvoiceLine());
			calculateLegalMonetaryTotal();
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

	private void calculateLegalMonetaryTotal()
	{
		final BigDecimal divisor = new BigDecimal(100);
		final BigDecimal total =
				invoice.getInvoiceLine().stream().
				map(invoiceLine -> invoiceLine.getLineExtensionAmountValue().
						multiply(invoiceLine.getItem().getClassifiedTaxCategoryAtIndex(0).getPercentValue()).
						divide(divisor)).
				reduce(new BigDecimal(0), (a, b) -> a.add(b));

		final MonetaryTotalType monetaryTotal = new MonetaryTotalType();
		final PayableAmountType payableAmount = new PayableAmountType();
		payableAmount.setValue(total);
		payableAmount.setCurrencyID(InstanceFactory.CURRENCY_CODE);
		monetaryTotal.setPayableAmount(payableAmount);
		invoice.setLegalMonetaryTotal(monetaryTotal);
	}

	public boolean isSendPressed()
	{
		return sendPressed;
	}

	public void setSendPressed(boolean sendPressed)
	{
		this.sendPressed = sendPressed;
	}

	public InvoiceType getInvoice()
	{
		return invoice;
	}
}