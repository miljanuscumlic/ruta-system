package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "CustomerSendApplicationResponseState")
public class CustomerSendApplicationResponseState extends CustomerBillingProcessState
{
	private static CustomerBillingProcessState INSTANCE = new CustomerSendApplicationResponseState();

	public static CustomerBillingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final CustomerBillingProcess process = (CustomerBillingProcess) correspondence.getState();
		final ApplicationResponseType applicationResponse = process.getApplicationResponse(correspondence);
		if(applicationResponse != null)
		{
			final DocumentReference documentReference = correspondence.getDocumentReference(applicationResponse.getUUIDValue());
			if(!documentReference.getStatus().equals(DocumentReference.Status.UBL_VALID)) // sending failed in a previous attempt
			{
				try
				{
					correspondence.block();
				}
				catch(InterruptedException e)
				{
					if(!correspondence.isStopped()) //non-intentional interruption
						throw new StateActivityException(Messages.getString("CustomerSendApplicationResponseState.0")); //$NON-NLS-1$
				}
			}
			try
			{
				process.getClient().cdrSendDocument(applicationResponse, documentReference, correspondence);
				if(process.isInvoiceAccepted() && !process.isInvoiceRejected())
					changeState(process, ClosingState.getInstance());
				else if(process.isInvoiceRejected() && !process.isInvoiceAccepted())
					changeState(process, CustomerReceiveInvoiceState.getInstance());
				else
					throw new StateActivityException(Messages.getString("CustomerSendApplicationResponseState.1")); //$NON-NLS-1$
				process.setInvoiceAccepted(false);
				process.setInvoiceRejected(false);
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder(Messages.getString("CustomerSendApplicationResponseState.2"))); //$NON-NLS-1$
//				changeState(process, CustomerSendApplicationResponseState.getInstance());
			}
		}
		else
		{
			//			correspondence.updateDocumentStatus(correspondence.getLastDocumentReference(OrderType.class),
			//					DocumentReference.Status.CLIENT_FAILED);
			throw new StateActivityException(Messages.getString("CustomerSendApplicationResponseState.3")); //$NON-NLS-1$
		}
	}
}