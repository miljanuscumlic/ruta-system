package rs.ruta.client.correspondence;

import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DeleteCatalogueProcessState")
public abstract class DeleteCatalogueProcessState implements RutaProcessState
{
	public Future<?> notifyOfCatalogueDeletion(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void receiveCatalogueDeletionAppResponse(final RutaProcess process, Future<?> future) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void reviewDeletionOfCatalogue(final RutaProcess process, Semaphore decision) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void cancelCatalogue(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

/*	public void endOfProcess(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}*/

}