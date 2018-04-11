package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReceiveCatalogueDeletionAppRespState", namespace = "urn:rs:ruta:client")
public class ReceiveCatalogueDeletionAppRespState extends DeleteCatalogueProcessState
{
	private static ReceiveCatalogueDeletionAppRespState INSTANCE = new ReceiveCatalogueDeletionAppRespState();

	public static ReceiveCatalogueDeletionAppRespState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveCatalogueDeletionAppResponse(final RutaProcess process, Future<?> future) throws StateTransitionException
	{
		final RutaProcessState newState = process.getClient().cdrReceiveMyCatalogueDeletionAppResponse(future);
		if(newState != null)
			changeState(process, newState);
		else
			throw new StateTransitionException("Invalid Application Response code!");
	}

}