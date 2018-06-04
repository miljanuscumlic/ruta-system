package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.MyParty;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "SellerRejectOrderState")
public class SellerRejectOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerRejectOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final OrderResponseSimpleType orderResponseSimple = prepareOrderResponseSimple(correspondence);
		if(orderResponseSimple != null)
		{
			saveOrderResponseSimple(correspondence, orderResponseSimple);
			changeState(process, SellerSendOrderResponseSimpleState.getInstance());
		}
		else
		{
			process.setOrderRejected(false);
			changeState(process, SellerProcessOrderState.getInstance());
		}
	}

	/**
	 * Prepares {@link OrderResponseSimpleType Order Response Simple} populated with some data from the
	 *  {@link OrderType Order}.
	 * @param correspondence correspondence that process of this state belongs to
	 * @return Order Response Simple or {@code null} if its creation has failed, or has been aborted by the user
	 * or the document doesn't conform to the UBL standard
	 */
	private OrderResponseSimpleType prepareOrderResponseSimple(Correspondence correspondence)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		final MyParty myParty = process.getClient().getMyParty();
		return myParty.produceOrderResponseSimple(process.getOrder(correspondence), false, process.isObsoleteCatalogue());
	}

	/**
	 * Sets Order Response Simple field of the process, adds it's {@link DocumentReference} to the
	 * correspondence and stores it in the database.
	 * @param correspondence which order is part of
	 * @param applicationResponse Order Response Simple to save
	 */
	private void saveOrderResponseSimple(Correspondence correspondence, OrderResponseSimpleType orderResponseSimple)
	{
		final SellerOrderingProcess process = (SellerOrderingProcess) correspondence.getState();
		process.setOrderResponseSimple(orderResponseSimple);
		correspondence.addDocumentReference(orderResponseSimple.getSellerSupplierParty().getParty(),
				orderResponseSimple.getUUIDValue(), orderResponseSimple.getIDValue(),
				orderResponseSimple.getIssueDateValue(), orderResponseSimple.getIssueTimeValue(),
				orderResponseSimple.getClass().getName(), DocumentReference.Status.UBL_VALID);
		correspondence.storeDocument(orderResponseSimple);
	}
}