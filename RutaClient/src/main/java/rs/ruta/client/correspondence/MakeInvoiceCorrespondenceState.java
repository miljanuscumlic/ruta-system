package rs.ruta.client.correspondence;

public class MakeInvoiceCorrespondenceState extends BuyingCorrespondenceState
{
	private static final MakeInvoiceCorrespondenceState INSTANCE = new MakeInvoiceCorrespondenceState();

	public static final MakeInvoiceCorrespondenceState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void makeInvoice(BuyingCorrespondence correspondence)
	{
		//TODO
		changeState(correspondence, MakeRemittanceAdviceCorrespondenceState.getInstance());
	}

}
