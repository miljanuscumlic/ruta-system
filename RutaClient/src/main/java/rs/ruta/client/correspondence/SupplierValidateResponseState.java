package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "SupplierValidateResponseState")
public class SupplierValidateResponseState extends SupplierBillingProcessState
{
	private static SupplierBillingProcessState INSTANCE = new SupplierValidateResponseState();

	public static SupplierBillingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			correspondence.block();
			final SupplierBillingProcess process = (SupplierBillingProcess) correspondence.getState();
			final ApplicationResponseType applicationResponse = process.getApplicationResponse(correspondence);

			decideOnApplicationResponse(process, applicationResponse);
			if(process.isInvoiceAccepted())
				changeState(process, ClosingState.getInstance());
			else if(process.isInvoiceRejected())
			{
				process.setInvoiceRejected(false);
				changeState(process, SupplierRaiseInvoiceState.getInstance());
			}
//			else // user decided to postpone the decision
//				changeState(process, SupplierValidateResponseState.getInstance());
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateActivityException("Correspondence has been interrupted!"); 
		}
	}

	/**
	 * Displays dialog for making the decision on what kind of response should be on the Invoice.
	 * @param process current process
	 * @param applicationResponse Invoicer to make decision on
	 */
	private void decideOnApplicationResponse(SupplierBillingProcess process, ApplicationResponseType applicationResponse)
	{
		final RutaClientFrame clientFrame = process.getClient().getClientFrame();
		final String decision = clientFrame.showProcessApplicationResponseDialog(applicationResponse);
		if(InstanceFactory.MODIFY_INVOICE.equals(decision))
			process.setInvoiceRejected(true);
		else if(InstanceFactory.ACCEPT.equals(decision))
			process.setInvoiceAccepted(true);
	}

}