package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import rs.ruta.client.RutaClient;

public class DeleteCatalogueProcess extends CatalogueProcess
{
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
			boolean loop = true;
			while(loop)
			{
				Future<?> future = notifyOfCatalogueDeletion();
				receiveCatalogueDeletionAppResponse(future);
				if(state instanceof ReviewDeletionOfCatalogueState)
				{
					Semaphore decision = new Semaphore(0);
					reviewDeletionOfCatalogue(decision);
					decision.acquire();
					if(state instanceof NotifyOfCatalogueDeletionState)
						loop = true;
					else if(state instanceof EndOfProcessState)
					{
						endOfProcess();
						loop = false;
					}
				}
				else if(state instanceof CancelCatalogueState)
				{
					cancelCatalogue();
					endOfProcess();
					loop = false;
				}
			}
			correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
		}
		catch(Exception e)
		{
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
			throw new StateTransitionException("Interrupted execution of Delete Catalogue Process!", e);
		}
	}

	/**
	 * Sends {@link CatalogueDeletionType} {@code UBL document} to the CDR.
	 * @return {@link Future} object that holds the CDR response
	 */
	public Future<?> notifyOfCatalogueDeletion()
	{
		return ((DeleteCatalogueProcessState) state).notifyOfCatalogueDeletion(this);
	}

	/**
	 * Waits to receive the response in the form of {@link ApplicationResponseType} document from the CDR service.
	 * @param future {@link Future} object that holds the CDR response
	 */
	public void receiveCatalogueDeletionAppResponse(Future<?> future)
	{
		((DeleteCatalogueProcessState) state).receiveCatalogueDeletionAppResponse(this, future);
	}

	/**
	 * Decides what to do upon receipt of the {@link ApplicationResponseType} document from the CDR service.
	 * @param decision {@link Semaphore} that enables calling method to wait for callee to finish its execution in its {@code Thread}
	 */
	public void reviewDeletionOfCatalogue(Semaphore decision)
	{
		((DeleteCatalogueProcessState) state).reviewDeletionOfCatalogue(this, decision);
	}

	/**
	 * Cancels the {@code Catalogue} noting that it is deleted on the serveice side.
	 */
	public void cancelCatalogue()
	{
		((DeleteCatalogueProcessState) state).cancelCatalogue(this);
	}

	/**
	 * Ends the process.
	 */
	public void endOfProcess()
	{
		state.endOfProcess(this);
	}
}