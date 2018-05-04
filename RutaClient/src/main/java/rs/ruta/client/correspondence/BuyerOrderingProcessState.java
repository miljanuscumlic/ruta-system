package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "BuyerOrderingProcessState")
public abstract class BuyerOrderingProcessState implements RutaProcessState
{
	//MMM maybe all of these method won't be necessary because doActivity is used instead
	public void prepareOrder(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void placeOrder(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void receiveOrderResponse(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void orderRejected(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void processResponse(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void changeOrder(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void cancelOrder(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void acceptOrder(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void orderAccepted(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

}