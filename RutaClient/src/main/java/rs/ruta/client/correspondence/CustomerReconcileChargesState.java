package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "CustomerReconcileChargesState")
public class CustomerReconcileChargesState extends CustomerBillingProcessState
{
	private static CustomerBillingProcessState INSTANCE = new CustomerReconcileChargesState();

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

			decideOnInvoice(process, invoice);
			if(process.isInvoiceAccepted() || process.isInvoiceRejected())
				changeState(process, CustomerCreateApplicationResponseState.getInstance());
			else // user decided to postpone the decision
				changeState(process, CustomerReconcileChargesState.getInstance());
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateActivityException(Messages.getString("CustomerReconcileChargesState.0")); 
		}
	}

	/**
	 * Displays dialog for making the decision on what kind of response should be on the Invoice.
	 * @param process current process
	 * @param invoice Invoicer to make decision on
	 */
	private void decideOnInvoice(CustomerBillingProcess process, InvoiceType invoice)
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final String decision = clientFrame.showProcessInvoiceDialog(invoice);
		if(InstanceFactory.ACCEPT_INVOICE.equals(decision))
			process.setInvoiceAccepted(true);
		else if(InstanceFactory.REJECT_INVOICE.equals(decision))
			process.setInvoiceRejected(true);
	}

}