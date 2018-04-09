package rs.ruta.client.correspondence;

public class NotifyOfCatalogueDeletionState extends DeleteCatalogueProcessState
{
	private static NotifyOfCatalogueDeletionState INSTANCE = new NotifyOfCatalogueDeletionState();

	public static NotifyOfCatalogueDeletionState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void notifyOfCatalogueDeletion(final RutaProcess process) throws StateTransitionException
	{
		changeState(process, ReceiveCatalogueDeletionAppRespState.getInstance());
	}

}
