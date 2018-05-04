package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "BuyerAcceptOrderState", namespace = "urn:rs:ruta:client:correspondence:buying:ordering:seller")
public class SellerAcceptOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerAcceptOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		final OrderType order = ((SellerOrderingProcess) process).getOrder();
		final OrderResponseSimpleType orderResponseSimple = new OrderResponseSimpleType();
		orderResponseSimple.setID(UUID.randomUUID().toString());
		orderResponseSimple.setIssueDate(InstanceFactory.getDate());
		orderResponseSimple.setAcceptedIndicator(true);
		final OrderReferenceType orderReference = new OrderReferenceType();
		orderReference.setID(UUID.randomUUID().toString());
		final DocumentReferenceType docReference = new DocumentReferenceType();
		docReference.setID(order.getID());
		docReference.setUUID(order.getUUIDValue());
		orderReference.setDocumentReference(docReference);
		orderResponseSimple.setOrderReference(orderReference);
		orderResponseSimple.setSellerSupplierParty(order.getSellerSupplierParty());
		orderResponseSimple.setBuyerCustomerParty(order.getBuyerCustomerParty());

		process.getClient().cdrSendOrderResponseSimple(orderResponseSimple);

		try
		{
			correspondence.block(5000); //MMM time should be defined globally
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		changeState(process, SellerReceiveOrderChangeCancellationState.getInstance());
	}
}
