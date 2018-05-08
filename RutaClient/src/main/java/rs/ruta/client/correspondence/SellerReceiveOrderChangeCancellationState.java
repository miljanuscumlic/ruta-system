package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SellerReceiveOrderChangeCancellationState")
public class SellerReceiveOrderChangeCancellationState extends SellerOrderingProcessState
{
	private static SellerOrderingProcessState INSTANCE = new SellerReceiveOrderChangeCancellationState();

	public static SellerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		try
		{
			//MMM: implementing timeout : temporary commented
//			long timeout = 5000;
//			correspondence.block(timeout);
			correspondence.block();
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateTransitionException("Correspondence has been interrupted!");
		}

		if(!correspondence.isStopped())
			changeState((RutaProcess) correspondence.getState(), EndOfProcessState.getInstance());
	}
}
