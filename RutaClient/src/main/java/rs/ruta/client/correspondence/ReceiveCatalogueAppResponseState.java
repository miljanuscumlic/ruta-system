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
	public void doActivity(Correspondence correspondence)
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		final Future<?> future = ((CreateCatalogueProcess) process).getFuture();
		final Boolean accepted = process.getClient().cdrReceiveMyCatalogueUpdateAppResponse(future);
		if(accepted != null)
		{
			RutaProcessState newState;
			if(accepted.equals(Boolean.TRUE))
				newState = EndOfProcessState.getInstance();
			else
				newState = DecideOnActionState.getInstance();
			changeState(process, newState);
		}
		else
			throw new StateTransitionException("Invalid Application Response code!");
	}
}