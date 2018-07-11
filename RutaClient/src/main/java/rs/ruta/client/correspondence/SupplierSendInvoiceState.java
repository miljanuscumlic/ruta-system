package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import rs.ruta.client.MyParty;
import rs.ruta.client.gui.OrderResponseDialog;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "SupplierSendInvoiceState")
public class SupplierSendInvoiceState extends SupplierBillingProcessState
{
	private static SupplierBillingProcessState INSTANCE = new SupplierSendInvoiceState();

	public static SupplierBillingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final SupplierBillingProcess process = (SupplierBillingProcess) correspondence.getState();
		final InvoiceType invoice = process.getInvoice(correspondence);
		if(invoice != null)
		{
			final DocumentReference documentReference = correspondence.getDocumentReference(invoice.getUUIDValue());
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException(Messages.getString("SupplierSendInvoiceState.0")); 
				}
			}
			try
			{
				process.getClient().cdrSendDocument(invoice, documentReference, correspondence);
				changeState(process, SupplierReceiveApplicationResponseState.getInstance());
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("SupplierSendInvoiceState.1"))); 
//				changeState(process, SupplierSendInvoiceState.getInstance());
			}
		}
		else
			throw new StateActivityException(Messages.getString("SupplierSendInvoiceState.2")); 
	}
}