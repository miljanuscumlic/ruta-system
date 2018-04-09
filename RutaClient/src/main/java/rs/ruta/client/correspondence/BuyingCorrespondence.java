package rs.ruta.client.correspondence;

import java.util.UUID;

import rs.ruta.client.RutaClient;

/**
 * Class encapsulating {@link Correspondence} between parties during which {@link OrderType},
 * {@link InvoiceType} and alike {@code UBL} business documents are exchanged among parties
 * of the {@code Ruta system}.
 */
public class BuyingCorrespondence extends Correspondence
{
/*	public BuyingCorrespondence()
	{
		state = new OrderingProcess();
	}*/

	/**
	 * Constructs new instance of a {@link BuyingCorrespondence} and sets its state to
	 * default value and id to a random value.
	 * @return {@code BuyingCorrespondence}
	 */
	public static BuyingCorrespondence newInstance(RutaClient client)
	{
		BuyingCorrespondence corr = new BuyingCorrespondence();
		corr.setId(UUID.randomUUID().toString());
		corr.setState(OrderingProcess.newInstance(client));
		return corr;
	}

	public void makeOrder()
	{
		((BuyingCorrespondenceState) state).makeOrder(this);
	}

	public void makeInvoice()
	{
		((BuyingCorrespondenceState) state).makeInvoice(this);
	}

	public void makeRemittanceAdvice()
	{
		((BuyingCorrespondenceState) state).makeRemittanceAdvice(this);
	}

}
