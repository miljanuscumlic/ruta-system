package rs.ruta.client.correspondence;

public class CancelCatalogueState extends DeleteCatalogueProcessState
{

	private static CancelCatalogueState INSTANCE = new CancelCatalogueState();

	public static CancelCatalogueState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveCatalogueDeletionAppResp(final RutaProcess process) throws StateTransitionException
	{
		changeState(process, NotifyOfCatalogueDeletionState.getInstance());
	}

}