package rs.ruta.client.correspondence;

public class ReceiveCatalogueDeletionAppRespState extends DeleteCatalogueProcessState
{
	private static ReceiveCatalogueDeletionAppRespState INSTANCE = new ReceiveCatalogueDeletionAppRespState();

	public static ReceiveCatalogueDeletionAppRespState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveCatalogueDeletionAppResp(final RutaProcess process) throws StateTransitionException
	{
		changeState(process, ReviewDeletionOfCatalogueState.getInstance());
	}

}
