package rs.ruta.client.correspondence;

import java.util.UUID;

import rs.ruta.client.RutaClient;

public class BillingProcess extends DocumentProcess
{
	/**
	 * Constructs new instance of a {@link BillingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code BillingProcess}
	 */
	public static BillingProcess newInstance(RutaClient client)
	{
		BillingProcess process = new BillingProcess();
		process.setState(BuyerPlaceOrderState.getInstance()); //MMM: default state should be changed
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		return process;
	}

}
