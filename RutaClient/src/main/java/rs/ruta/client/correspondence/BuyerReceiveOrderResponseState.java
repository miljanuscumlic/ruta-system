package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "BuyerReceiveOrderResponseState")
//@XmlType(namespace = "urn:rs:ruta:client:correspondence:buying:ordering:buyer")
public class BuyerReceiveOrderResponseState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerReceiveOrderResponseState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveOrderResponse(final RutaProcess process)
	{
		//MMM to implement
		changeState(process, BuyerProcessResponseState.getInstance());
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		try
		{
			correspondence.block();
		}
		catch(InterruptedException e)
		{
			if(! correspondence.isStopped()) //non-intentional interruption
				throw new StateTransitionException("Correspondence has been interrupted!");
		}

		//after the correspondence is notified the Order Response should be processed
		if(! correspondence.isStopped())
			changeState(process, BuyerProcessResponseState.getInstance());

	}

/*	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		try
		{
			synchronized(correspondence)
			{
				correspondence.wait();
			}
		}
		catch (InterruptedException e)
		{
			throw new StateTransitionException("Correspondence has been interrupted!");
		}
		//after the correspondence is notified the Order Response should be processed
		changeState(process, BuyerProcessResponseState.getInstance());

	}*/

}