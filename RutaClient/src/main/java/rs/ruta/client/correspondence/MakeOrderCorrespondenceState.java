package rs.ruta.client.correspondence;

public class MakeOrderCorrespondenceState extends BuyingCorrespondenceState
{
	private static final MakeOrderCorrespondenceState INSTANCE = new MakeOrderCorrespondenceState();

	public static MakeOrderCorrespondenceState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void makeOrder(BuyingCorrespondence correspondence)
	{
		changeState(correspondence, MakeInvoiceCorrespondenceState.getInstance());
	}

}
