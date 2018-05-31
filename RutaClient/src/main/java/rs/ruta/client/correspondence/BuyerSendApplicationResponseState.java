package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
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
			DocumentReference documentReference = correspondence.getDocumentReference(applicationResponse.getUUIDValue());
			process.getClient().cdrSendDocument(applicationResponse, documentReference, correspondence);
			changeState(process, ClosingState.getInstance());
		}
		else
		{
//			correspondence.updateDocumentStatus(correspondence.getLastDocumentReference(OrderType.class),
//					DocumentReference.Status.CLIENT_FAILED);
			throw new StateActivityException("Application Response has not been sent to the CDR service! Application Response could not be found!");
		}
	}

}