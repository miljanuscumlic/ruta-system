package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "BuyerSendApplicationResponseState")
public class BuyerSendApplicationResponseState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerSendApplicationResponseState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
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
						throw new StateActivityException("Correspondence has been interrupted!"); 
				}
			}
			try
			{
				process.getClient().cdrSendDocument(applicationResponse, documentReference, correspondence);
				changeState(process, ClosingState.getInstance());
			}
			catch(Exception e)
			{
				process.getClient().getClientFrame().
				processExceptionAndAppendToConsole(e, new StringBuilder("Sending Application Response has failed!")); 
//				changeState(process, BuyerSendApplicationResponseState.getInstance());
			}
		}
		else
			throw new StateActivityException("Application Response has not been sent to the CDR service! Application Response could not be found!"); 
	}

}