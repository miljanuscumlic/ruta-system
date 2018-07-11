package rs.ruta.client.correspondence;

import java.awt.Color;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.RutaClient;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;

@XmlRootElement(name = "BuyerAcceptOrderState")
public class BuyerAcceptOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerAcceptOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		correspondence.getClient().getClientFrame().appendToConsole(
				new StringBuilder(Messages.getString("BuyerAcceptOrderState.0") + process.getOrder(correspondence).getIDValue() + 
						Messages.getString("BuyerAcceptOrderState.1") + correspondence.getCorrespondentPartyName() + Messages.getString("BuyerAcceptOrderState.2")), Color.BLACK);  
		DocumentReference documentReference = correspondence.getLastDocumentReference();
		ApplicationResponseType appResponse = null;
		if(OrderResponseType.class.getName().equals(documentReference.getDocumentTypeValue()))
		{
			final OrderResponseType orderResponse = process.getOrderResponse(correspondence);
			appResponse = prepareApplicationResponse(process, orderResponse);

		}
		else if(OrderResponseSimpleType.class.getName().equals(documentReference.getDocumentTypeValue()))
		{
			final OrderResponseSimpleType orderResponseSimple = process.getOrderResponseSimple(correspondence);
			appResponse = prepareApplicationResponse(process, orderResponseSimple);
		}
		if(appResponse != null)
		{
			saveApplicationResponse(correspondence, appResponse);
			changeState(process, BuyerSendApplicationResponseState.getInstance());
		}
		else
		{
			correspondence.setDiscarded(true); //MMM check this: should not happen
			changeState(process, ClosingState.getInstance());
		}
	}

	/**
	 * Creates {@link ApplicationResponseType Application Response} populating it with the data.
	 * @param process process that this state belongs to
	 * @param document {@code UBL} document that the {@code Application Response} is to be made for
	 * @return prepared Application Response or {@code null} if order creation has been failed,
	 * or Application Response does not conform to the {@code UBL} standard
	 */
	private ApplicationResponseType prepareApplicationResponse(RutaProcess process, Object document)
	{
		process.getClient().getClientFrame().appendToConsole(
				new StringBuilder(Messages.getString("BuyerAcceptOrderState.3")), Color.BLACK); 
		return InstanceFactory.produceApplicationResponse(document, InstanceFactory.APP_RESPONSE_POSITIVE, null);
	}

	/**
	 * Creates {@link ApplicationResponseType Application Response} populating it with the data.
	 * @param process process that this state belongs to
	 * @param applicationResponse {@code Order Response Simple} document that the {@code Application Response}
	 * is to be made on
	 * @return prepared Application Response or {@code null} if order creation has been failed,
	 * or Application Response does not conform to the {@code UBL} standard
	 */
	//	private ApplicationResponseType prepareApplicationResponse(RutaProcess process, OrderResponseSimpleType applicationResponse)
	//	{
	//		final RutaClient client = process.getClient();
	//		client.getClientFrame().appendToConsole(new StringBuilder("Collecting data and preparing the Application Response..."),
	//				Color.BLACK);
	//		return client.getMyParty().produceApplicationResponse(applicationResponse);
	//	}

	/**
	 * Sets Application Response in the process, adds it's {@link DocumentReference} to the correspondence and stores it
	 * in the database.
	 * @param correspondence which Application Response is part of
	 * @param appResponse Application Response to save
	 * @throws StateActivityException if a document of an unexpected type is passed to the method
	 */
	private void saveApplicationResponse(Correspondence correspondence, ApplicationResponseType appResponse) throws StateActivityException
	{
		final BuyerOrderingProcess process = (BuyerOrderingProcess) correspondence.getState();
		((BuyerOrderingProcess) process).setApplicationResponse(appResponse);
		try
		{
			correspondence.addDocumentReference(appResponse, DocumentReference.Status.UBL_VALID);
			correspondence.storeDocument(appResponse);
		}
		catch(DetailException e)
		{
			throw new StateActivityException(e.getMessage());
		}
	}
}