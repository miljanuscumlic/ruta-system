package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

@XmlRootElement(name = "CustomerReceiveInvoiceState")
public class CustomerReceiveInvoiceState extends CustomerBillingProcessState
{
	private static CustomerBillingProcessState INSTANCE = new CustomerReceiveInvoiceState();

	public static CustomerBillingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			correspondence.block();
			final CustomerBillingProcess process = (CustomerBillingProcess) correspondence.getState();
			final InvoiceType invoice = process.getInvoice(correspondence);
			correspondence.validateDocument(invoice);
			changeState(process, CustomerReconcileChargesState.getInstance());
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateActivityException(Messages.getString("CustomerReceiveInvoiceState.0")); //$NON-NLS-1$
		}
	}
}