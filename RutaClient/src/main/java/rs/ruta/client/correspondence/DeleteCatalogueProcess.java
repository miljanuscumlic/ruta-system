package rs.ruta.client.correspondence;

import java.util.UUID;

import rs.ruta.client.RutaClient;

public class DeleteCatalogueProcess extends CatalogueProcess
{
/*	public DeleteCatalogueProcess()
	{
		state = NotifyOfCatalogueDeletionState.getInstance();
	}*/

	/**
	 * Constructs new instance of a {@link DeleteCatalogueProcess} and sets its state to
	 * default value and id to a random value.
	 * @return {@link DeleteCatalogueProcess}
	 */
	public static DeleteCatalogueProcess newInstance(RutaClient client)
	{
		DeleteCatalogueProcess process = new DeleteCatalogueProcess();
		process.setState(NotifyOfCatalogueDeletionState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		return process;
	}

	@Override
	public void deleteCatalogue(Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			notifyOfCatalogueDeletion();
			receiveCatalogueDeletionAppResponse();
			reviewDeletionOfCatalogue();
			if(state instanceof CancelCatalogueState)
				cancelCatalogue();
			//else it's in a starting state NotifyOfCatalogueDeletionState
		}
		finally
		{
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
		}
	}

	public void notifyOfCatalogueDeletion()
	{
		((DeleteCatalogueProcessState) state).notifyOfCatalogueDeletion(this);
	}

	public void receiveCatalogueDeletionAppResponse()
	{
		((DeleteCatalogueProcessState) state).receiveCatalogueDeletionAppResp(this);
	}

	public void reviewDeletionOfCatalogue()
	{
		((DeleteCatalogueProcessState) state).reviewDeletionOfCatalogue(this);
	}

	public void cancelCatalogue()
	{
		((DeleteCatalogueProcessState) state).cancelCatalogue(this);
	}


}
