package rs.ruta.client.correspondence;

import java.awt.Color;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.client.RutaClient;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.datamapper.DetailException;

@XmlRootElement(name = "SupplierRaiseInvoiceState")
public class SupplierRaiseInvoiceState extends SupplierBillingProcessState
{
	private static SupplierBillingProcessState INSTANCE = new SupplierRaiseInvoiceState();

	public static SupplierBillingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		try
		{
			final SupplierBillingProcess process = (SupplierBillingProcess) correspondence.getState();
			if(process.getInvoice(correspondence) == null || process.isInvoiceDiscarded())
				correspondence.block();
			final InvoiceType invoice = prepareInvoice(correspondence);
			if(invoice != null)
			{
				process.setInvoiceDiscarded(false);
				saveInvoice(correspondence, invoice);
				changeState(process, SupplierSendInvoiceState.getInstance());
			}
			else
			{
				process.getClient().getClientFrame().
				appendToConsole(new StringBuilder(Messages.getString("SupplierRaiseInvoiceState.0")), Color.BLACK); 
				process.setInvoiceDiscarded(true);
				changeState(process, SupplierRaiseInvoiceState.getInstance());
			}
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateActivityException(Messages.getString("SupplierRaiseInvoiceState.1")); 
		}
	}

	/**
	 * Creates {@link InvoiceType invoice} populating it with the data.
	 * @param correspondence which Invoice is part of
	 * @return prepared Invoice or {@code null} if Invoice creation has been failed or has been discarded
	 * by the user, or Invoice does not conform to the {@code UBL} standard
	 */
	private InvoiceType prepareInvoice(Correspondence correspondence)
	{
		InvoiceType invoice = null;
		final SupplierBillingProcess process = (SupplierBillingProcess) correspondence.getState();
		final RutaClient client = process.getClient();
		client.getClientFrame().appendToConsole(new StringBuilder(Messages.getString("SupplierRaiseInvoiceState.2")), Color.BLACK); 
		invoice = process.getInvoice(correspondence);
		if(invoice != null)
			invoice = client.getMyParty().produceInvoice(invoice);
		else
		{
			final OrderChangeType orderChange = correspondence.getLastDocument(OrderChangeType.class);
			if(orderChange != null)
				invoice = client.getMyParty().produceInvoice(orderChange);
			else
			{
				final OrderResponseType orderResponse = correspondence.getLastDocument(OrderResponseType.class);
				if(orderResponse != null)
					invoice = client.getMyParty().produceInvoice(orderResponse);
				else
				{
					final OrderType order = correspondence.getLastDocument(OrderType.class);
					if(order != null)
						invoice = client.getMyParty().produceInvoice(order);
				}
			}
		}
		return invoice;
	}

	/**
	 * Sets Invoice in the process, adds it's {@link DocumentReference} to the correspondence and stores it
	 * in the database.
	 * @param correspondence which Invoice is part of
	 * @param invoice Invoice to save
	 */
	private void saveInvoice(Correspondence correspondence, InvoiceType invoice)
	{
		final SupplierBillingProcess process = (SupplierBillingProcess) correspondence.getState();
		((SupplierBillingProcess) process).setInvoice(invoice);
		//		correspondence.addDocumentReference(invoice.getAccountingSupplierParty().getParty(),
		//				invoice.getUUIDValue(), invoice.getIDValue(), invoice.getIssueDateValue(),
		//				invoice.getIssueTimeValue(), invoice.getClass().getName(), DocumentReference.Status.UBL_VALID);
		try
		{
			correspondence.addDocumentReference(invoice, DocumentReference.Status.UBL_VALID);
			correspondence.storeDocument(invoice);
		}
		catch (DetailException e)
		{
			throw new StateActivityException(e.getMessage());
		}

	}

}