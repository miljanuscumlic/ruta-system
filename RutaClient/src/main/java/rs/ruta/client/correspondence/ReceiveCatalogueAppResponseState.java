package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "ReceiveCatalogueAppResponseState")
public class ReceiveCatalogueAppResponseState extends CreateCatalogueProcessState
{
	private static final ReceiveCatalogueAppResponseState INSTANCE = new ReceiveCatalogueAppResponseState();

	public static ReceiveCatalogueAppResponseState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final CreateCatalogueProcess process = (CreateCatalogueProcess) correspondence.getState();
		final Future<?> future = process.getFuture();
		final DocumentReference documentReference = correspondence.getLastDocumentReference();
		final Boolean accepted = process.getClient().cdrReceiveMyCatalogueUpdateAppResponse(future, documentReference, correspondence);
		if(accepted != null)
		{
			RutaProcessState newState;
			if(accepted.equals(Boolean.TRUE))
				newState = ClosingState.getInstance();
			else
				newState = DecideOnActionState.getInstance();
			changeState(process, newState);
		}
		else
			throw new StateActivityException("Invalid Application Response code!");
	}
}