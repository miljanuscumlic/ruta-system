package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReceiveCatalogueAppResponseState")
public class ReceiveCatalogueAppResponseState extends CreateCatalogueProcessState
{
	private static final ReceiveCatalogueAppResponseState INSTANCE = new ReceiveCatalogueAppResponseState();

	public static ReceiveCatalogueAppResponseState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveCatalogueAppResponse(final RutaProcess process, Future<?> future) throws StateTransitionException
	{
		final RutaProcessState newState = process.getClient().cdrReceiveMyCatalogueUpdateAppResponse(future);
		if(newState != null)
			changeState(process, newState);
		else
			throw new StateTransitionException("Invalid Application Response code!");
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		Future<?> future = ((CreateCatalogueProcess) process).getFuture();
		final RutaProcessState newState = process.getClient().cdrReceiveMyCatalogueUpdateAppResponse(future);
		if(newState != null)
			changeState(process, newState);
		else
			throw new StateTransitionException("Invalid Application Response code!");
	}



}