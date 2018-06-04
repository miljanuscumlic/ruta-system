package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import rs.ruta.client.MyParty;
import rs.ruta.client.gui.OrderResponseDialog;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "SellerAddDetailState")
public class SellerAddDetailState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerAddDetailState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final OrderResponseType orderResponse = prepareOrderResponse(correspondence);
		if(orderResponse != null)
		{
			saveOrderResponse(correspondence, orderResponse);
			changeState(process, SellerSendOrderResponseState.getInstance());
		}
		else
		{
			process.setOrderModified(false);
			changeState(process, SellerProcessOrderState.getInstance());
		}
	}

	/**
	 * Prepares {@link OrderResponseType Order Response} populated with some data from the {@link OrderType Order}.
	 * @param correspondence correspondence that process of this state belongs to
	 * @return Order Response or {@code null} if its creation has failed, or has been aborted by the user
	 * or the Order Response document doesn't conform to the UBL standard
	 */
	private OrderResponseType prepareOrderResponse(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final MyParty myParty = process.getClient().getMyParty();
		final DocumentReference documentReference = correspondence.getLastDocumentReference();
		OrderType order = null;
		if(OrderType.class.getName().equals(documentReference.getDocumentTypeValue()))
			order = process.getOrder(correspondence);
		else if(OrderChangeType.class.getName().equals(documentReference.getDocumentTypeValue()))
		{
			order = process.getOrder(correspondence).clone();
			order.setOrderLine(process.getOrderChange(correspondence).getOrderLine());
		}
		return myParty.produceOrderResponse(order);
	}

	/**
	 * Sets Order Response field of the process, adds it's {@link DocumentReference} to the correspondence
	 * and stores it in the database.
	 * @param correspondence which order is part of
	 * @param orderResponse Order Response to save
	 */
	private void saveOrderResponse(Correspondence correspondence, OrderResponseType orderResponse)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		process.setOrderResponse(orderResponse);
		correspondence.addDocumentReference(orderResponse.getSellerSupplierParty().getParty(),
				orderResponse.getUUIDValue(), orderResponse.getIDValue(),
				orderResponse.getIssueDateValue(), orderResponse.getIssueTimeValue(),
				orderResponse.getClass().getName(), DocumentReference.Status.UBL_VALID);
		correspondence.storeDocument(orderResponse);
	}
}