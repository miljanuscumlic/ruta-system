package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReceiveCatalogueDeletionAppResponseState")
public class ReceiveCatalogueDeletionAppResponseState extends DeleteCatalogueProcessState
{
	private static ReceiveCatalogueDeletionAppResponseState INSTANCE = new ReceiveCatalogueDeletionAppResponseState();

	public static ReceiveCatalogueDeletionAppResponseState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final DeleteCatalogueProcess process = (DeleteCatalogueProcess) correspondence.getState();
		final Future<?> future = process.getFuture();
		final Boolean accepted = process.getClient().cdrReceiveMyCatalogueDeletionAppResponse(future);
		if(accepted != null)
		{
			RutaProcessState newState = null;
			if(accepted.equals(Boolean.TRUE))
				newState = CancelCatalogueState.getInstance();
			else
				newState = ReviewDeletionOfCatalogueState.getInstance();
			process.changeState(newState);
		}
		else
			throw new StateTransitionException("Invalid Application Response code!");
	}

}