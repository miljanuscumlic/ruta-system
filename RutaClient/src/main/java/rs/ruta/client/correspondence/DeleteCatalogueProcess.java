package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

/**
 * Encapsulating {@code UBL document process} for deletion
 * of the {@link CatalogueType} document, called {@code Delete Catalogue Process}.
 */
@XmlRootElement(name = "DeleteCatalogueProcess")
@XmlType(name = "DeleteCatalogueProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteCatalogueProcess extends CatalogueProcess
{

	private Future<?> future;

	/**
	 * Constructs new instance of a {@link DeleteCatalogueProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@link DeleteCatalogueProcess}
	 */
	public static DeleteCatalogueProcess newInstance(RutaClient client)
	{
		DeleteCatalogueProcess process = new DeleteCatalogueProcess();
		process.setState(NotifyOfCatalogueDeletionState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		return process;
	}

	public Future<?> getFuture()
	{
		return future;
	}

	public void setFuture(Future<?> future)
	{
		this.future = future;
	}

	//MMM change in the way CreateCatalogueProcess, doActivity is made
/*	@Override
	public void doActivity(Correspondence correspondence) throws StateTransitionException
	{
		try
		{
//			boolean loop = true;
			while(active)
			{
				Future<?> future = notifyOfCatalogueDeletion();
				receiveCatalogueDeletionAppResponse(future);
				if(state instanceof ReviewDeletionOfCatalogueState)
				{
					Semaphore decision = new Semaphore(0);
					reviewDeletionOfCatalogue(decision);
					decision.acquire();
					if(state instanceof EndOfProcessState)
					{
						endOfProcess();
//						loop = false;
					}
//					else if(state instanceof NotifyOfCatalogueDeletionState) active = true;
				}
				else if(state instanceof CancelCatalogueState)
				{
					cancelCatalogue();
					endOfProcess();
//					loop = false;
				}
			}
			correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
		}
		catch(Exception e)
		{
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
			throw new StateTransitionException("Interrupted execution of Delete Catalogue Process!", e);
		}
	}*/

	@Override
	public void doActivity(final Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active)
			{
				state.doActivity(correspondence);
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Create Catalogue Process!", e);
		}
		finally
		{
			if(correspondence.isActive() && !correspondence.isStopped())
			{
				correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
			}
		}
	}

}