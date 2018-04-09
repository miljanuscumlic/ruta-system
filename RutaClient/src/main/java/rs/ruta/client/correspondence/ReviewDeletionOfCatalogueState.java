package rs.ruta.client.correspondence;

public class ReviewDeletionOfCatalogueState extends DeleteCatalogueProcessState
{

	private static ReviewDeletionOfCatalogueState INSTANCE = new ReviewDeletionOfCatalogueState();

	public static ReviewDeletionOfCatalogueState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void receiveCatalogueDeletionAppResp(final RutaProcess process) throws StateTransitionException
	{
		//TODO resolution
		changeState(process, NotifyOfCatalogueDeletionState.getInstance());
		changeState(process, CancelCatalogueState.getInstance());
	}

}