package rs.ruta.client.correspondence;

public class MakeRemittanceAdviceCorrespondenceState extends BuyingCorrespondenceState
{
	private static final MakeRemittanceAdviceCorrespondenceState INSTANCE = new MakeRemittanceAdviceCorrespondenceState();

	public static final MakeRemittanceAdviceCorrespondenceState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void makeRemittanceAdvice(BuyingCorrespondence correspondence)
	{
		//TODO
		changeState(correspondence, MakeOrderCorrespondenceState.getInstance());

	}

}
