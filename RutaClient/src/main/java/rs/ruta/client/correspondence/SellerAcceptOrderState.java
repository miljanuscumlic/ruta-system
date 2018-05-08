package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.OrderReferenceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "BuyerAcceptOrderState")
public class SellerAcceptOrderState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerAcceptOrderState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}
	@Override
	public void doActivity(Correspondence correspondence)
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		final OrderType order = ((SellerOrderingProcess) process).getOrder();
		final OrderResponseSimpleType orderResponseSimple = new OrderResponseSimpleType();
		orderResponseSimple.setID(UUID.randomUUID().toString());
		final XMLGregorianCalendar now = InstanceFactory.getDate();
		orderResponseSimple.setIssueDate(now);
		orderResponseSimple.setIssueTime(now);
		orderResponseSimple.setAcceptedIndicator(true);
		final OrderReferenceType orderReference = new OrderReferenceType();
		orderReference.setID(UUID.randomUUID().toString());
		final DocumentReferenceType docReference = new DocumentReferenceType();
		docReference.setID(order.getID());
		docReference.setUUID(order.getUUIDValue());
		docReference.setIssueDate(order.getIssueDate());
		docReference.setIssueTime(order.getIssueTime());
		docReference.setDocumentType(order.getClass().getName());
		orderReference.setDocumentReference(docReference);
		orderResponseSimple.setOrderReference(orderReference);
		orderResponseSimple.setSellerSupplierParty(order.getSellerSupplierParty());
		orderResponseSimple.setBuyerCustomerParty(order.getBuyerCustomerParty());

		process.getClient().cdrSendOrderResponseSimple(orderResponseSimple);
		correspondence.addDocumentReference(docReference);

		try
		{
			correspondence.block(5000); //MMM timeout should be defined globally
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		changeState(process, SellerReceiveOrderChangeCancellationState.getInstance());
	}
}
