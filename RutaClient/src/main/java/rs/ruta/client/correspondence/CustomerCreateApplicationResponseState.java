package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import rs.ruta.client.MyParty;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "CustomerCreateApplicationResponseState")
public class CustomerCreateApplicationResponseState extends CustomerBillingProcessState
{
	private static CustomerBillingProcessState INSTANCE = new CustomerCreateApplicationResponseState();

	public static CustomerBillingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final CustomerBillingProcess process = (CustomerBillingProcess) correspondence.getState();
		final ApplicationResponseType applicationResponse = prepareApplicationResponse(correspondence);
		if(applicationResponse != null)
		{
			saveApplicationResponse(correspondence, applicationResponse);
			changeState(process, CustomerSendApplicationResponseState.getInstance());
		}
		else
		{
			process.setInvoiceAccepted(false);
			process.setInvoiceRejected(false);
			changeState(process, CustomerReconcileChargesState.getInstance());
		}
	}

	/**
	 * Prepares {@link ApplicationResponseType Application Response} populated with some data.
	 * @param correspondence correspondence that process of this state belongs to
	 * @return Application Response or {@code null} if its creation has failed, or has been aborted by the user
	 * or the document doesn't conform to the UBL standard
	 */
	private ApplicationResponseType prepareApplicationResponse(Correspondence correspondence)
	{
		final CustomerBillingProcess process = (CustomerBillingProcess) correspondence.getState();
		final MyParty myParty = process.getClient().getMyParty();
		String appResponse = null;
		if(process.isInvoiceAccepted())
			appResponse = InstanceFactory.APP_RESPONSE_POSITIVE;
		else if(process.isInvoiceRejected())
			appResponse = InstanceFactory.APP_RESPONSE_NEGATIVE;
		return myParty.produceApplicationResponse(process.getInvoice(correspondence), appResponse);
	}

	/**
	 * Sets Application Response field of the process, adds it's {@link DocumentReference} to the
	 * correspondence and stores it in the database.
	 * @param correspondence which order is part of
	 * @param applicationResponse Application Response to save
	 */
	private void saveApplicationResponse(Correspondence correspondence, ApplicationResponseType applicationResponse)
	{
		final CustomerBillingProcess process = (CustomerBillingProcess) correspondence.getState();
		process.setApplicationResponse(applicationResponse);
		correspondence.addDocumentReference(applicationResponse.getSenderParty(),
				applicationResponse.getUUIDValue(), applicationResponse.getIDValue(),
				applicationResponse.getIssueDateValue(), applicationResponse.getIssueTimeValue(),
				applicationResponse.getClass().getName(), DocumentReference.Status.UBL_VALID);
		correspondence.storeDocument(applicationResponse);
	}
}