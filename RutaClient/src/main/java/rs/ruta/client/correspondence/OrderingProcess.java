package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.DocumentReference;

abstract public class OrderingProcess extends BuyingProcess
{
	protected OrderType order;
	protected OrderResponseType orderResponse;
	protected OrderResponseSimpleType orderResponseSimple;
	protected OrderChangeType orderChange;
	protected OrderCancellationType orderCancellation;
	protected ApplicationResponseType applicationResponse;
	protected Future<?> future;
	/**
	 * True when the order has been canceled by correspondent party.
	 */
	protected boolean correspondenceCanceled; //MMM check this is not used at all

	public OrderingProcess()
	{
		correspondenceCanceled = false;
	}

	/**
	 * Gets the most recently received {@link OrderResponseType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link OrderResponseType} document or {@code null} if document could not be
	 * found in the correspondence
	 * @throws StateActivityException if order could not be retrieved from the database
	 */
	public OrderType getOrder(Correspondence correspondence)
	{
		if(order == null)
			order = correspondence.getLastDocument(OrderType.class);
		return order;
	}

	public void setOrder(OrderType order)
	{
		this.order = order;
	}

	/**
	 * Gets the most recently received {@link OrderResponseType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link OrderResponseType} document or {@code null} if document could not be
	 * found in correspondence
	 * @throws StateActivityException if orderLines could not be retrieved from the database
	 */
	public OrderResponseType getOrderResponse(Correspondence correspondence)
	{
		if(orderResponse == null)
			orderResponse = correspondence.getLastDocument(OrderResponseType.class);
		return orderResponse;
	}

	public void setOrderResponse(OrderResponseType orderResponse)
	{
		this.orderResponse = orderResponse;
	}

	/**
	 * Gets the most recently received {@link OrderResponseSimpleType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link OrderResponseSimpleType} document or {@code null} if document could not
	 * be found in correspondence
	 * @throws StateActivityException if orderLines could not be retrieved from the database
	 */
	public OrderResponseSimpleType getOrderResponseSimple(Correspondence correspondence)
	{
		if(orderResponseSimple == null)
			orderResponseSimple = correspondence.getLastDocument(OrderResponseSimpleType.class);
		return orderResponseSimple;
	}

	public void setOrderResponseSimple(OrderResponseSimpleType orderResponseSimple)
	{
		this.orderResponseSimple = orderResponseSimple;
	}

	/**
	 * Gets the most recently received {@link OrderChangeType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link OrderChangeType} document or {@code null} if document could not
	 * be found in correspondence
	 * @throws StateActivityException if Order Change could not be retrieved from the database
	 */
	public OrderChangeType getOrderChange(Correspondence correspondence)
	{
		if(orderChange == null)
			orderChange = correspondence.getLastDocument(OrderChangeType.class);
		return orderChange;
	}

	/**
	 * Gets {@link OrderChangeType} document which {@link DocumentReference document reference} is passed.
	 * @param correspondence correspondence which document is part of
	 * @param {@link DocumentReference document reference}
	 * @return {@link OrderChangeType} document or {@code null} if document could not be
	 * found in correspondence
	 * @throws StateActivityException if orderLines could not be retrieved from the database
	 */
	public OrderChangeType getOrderChange(Correspondence correspondence, DocumentReference documentReference)
	{
		if(orderChange == null)
			orderChange = (OrderChangeType) correspondence.getDocument(documentReference);
		return orderChange;
	}

	public void setOrderChange(OrderChangeType orderChange)
	{
		this.orderChange = orderChange;
	}

	/**
	 * Gets the most recently received {@link OrderCancellationType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link OrderCancellationType} document or {@code null} if document could not
	 * be found in correspondence
	 * @throws StateActivityException if Order Cancellation could not be retrieved from the database
	 */
	public OrderCancellationType getOrderCancellation(Correspondence correspondence)
	{
		if(orderCancellation == null)
			orderCancellation = correspondence.getLastDocument(OrderCancellationType.class);
		return orderCancellation;
	}

	public void setOrderCancellation(OrderCancellationType orderCancellation)
	{
		this.orderCancellation = orderCancellation;
	}

	/**
	 * Gets the {@link ApplicationResponseType} document.
	 * @param correspondence correspondence which document is part of
	 * @return {@link ApplicationResponseType} document or {@code null} if document could not
	 * be found in correspondence
	 * @throws StateActivityException if Application Response could not be retrieved from the database
	 */
	public ApplicationResponseType getApplicationResponse(Correspondence correspondence)
	{
		if(applicationResponse == null)
			applicationResponse = correspondence.getLastDocument(ApplicationResponseType.class);
		return applicationResponse;
	}

	public void setApplicationResponse(ApplicationResponseType applicationResponse)
	{
		this.applicationResponse = applicationResponse;
	}

	public Future<?> getFuture()
	{
		return future;
	}

	public void setFuture(Future<?> future)
	{
		this.future = future;
	}

	/**
	 * Test whether the correspondence is canceld by the correspondent party.
	 * @return true if correspondence is canceled; false otherwise
	 */
	public boolean isCorrespondenceCanceled()
	{
		return correspondenceCanceled;
	}

	public void setCorrespondenceCanceled(boolean canceled)
	{
		this.correspondenceCanceled = canceled;
	}

}
