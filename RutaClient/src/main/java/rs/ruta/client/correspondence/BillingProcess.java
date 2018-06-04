package rs.ruta.client.correspondence;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

abstract public class BillingProcess extends BuyingProcess
{
	protected ApplicationResponseType applicationResponse;
	protected InvoiceType invoice;
	private boolean invoiceAccepted;
	private boolean invoiceRejected;

	/**
	 * Gets the {@link ApplicationResponseType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link ApplicationResponseType} document or {@code null} if document could not
	 * be found in correspondence
	 * @throws StateActivityException if Application Response could not be retrieved from the database
	 */
	public ApplicationResponseType getApplicationResponse(Correspondence correspondence)
	{
		if(applicationResponse == null)
			applicationResponse = correspondence.getLastDocument(ApplicationResponseType.class);
		return applicationResponse;
	}

	public void setApplicationResponse(ApplicationResponseType applicationResponse)
	{
		this.applicationResponse = applicationResponse;
	}

	/**
	 * Gets the {@link InvoiceType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link InvoiceType} document or {@code null} if document could not
	 * be found in correspondence
	 * @throws StateActivityException if Invoice could not be retrieved from the database
	 */
	public InvoiceType getInvoice(Correspondence correspondence)
	{
		if(invoice == null)
			invoice = correspondence.getLastDocument(InvoiceType.class);
		return invoice;
	}

	public void setInvoice(InvoiceType invoice)
	{
		this.invoice = invoice;
	}

	public boolean isInvoiceAccepted()
	{
		return invoiceAccepted;
	}

	public void setInvoiceAccepted(boolean invoiceAccepted)
	{
		this.invoiceAccepted = invoiceAccepted;
	}

	public boolean isInvoiceRejected()
	{
		return invoiceRejected;
	}

	public void setInvoiceRejected(boolean invoiceRejected)
	{
		this.invoiceRejected = invoiceRejected;
	}
}