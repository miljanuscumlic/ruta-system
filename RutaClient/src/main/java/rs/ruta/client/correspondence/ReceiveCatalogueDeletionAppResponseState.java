package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.common.DocumentReference;

@XmlRootElement(name = "ReceiveCatalogueDeletionAppResponseState")
public class ReceiveCatalogueDeletionAppResponseState extends DeleteCatalogueProcessState
{
	private static ReceiveCatalogueDeletionAppResponseState INSTANCE = new ReceiveCatalogueDeletionAppResponseState();

	public static ReceiveCatalogueDeletionAppResponseState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		final DeleteCatalogueProcess process = (DeleteCatalogueProcess) correspondence.getState();
		final Future<?> future = process.getFuture();
		final DocumentReference documentReference = correspondence.getLastDocumentReference();
		try
		{
			final Boolean accepted = process.getClient().cdrReceiveMyCatalogueDeletionAppResponse(future, documentReference, correspondence);
			RutaProcessState newState = null;
			if(accepted.equals(Boolean.TRUE))
				newState = CancelCatalogueState.getInstance();
			else
				newState = ReviewDeletionOfCatalogueState.getInstance();
			process.changeState(newState);
		}
		catch(Exception e)
		{
			throw new StateActivityException("My Catalogue has not been updated in the CDR service!", e); 
		}
	}

}