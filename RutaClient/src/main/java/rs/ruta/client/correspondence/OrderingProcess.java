package rs.ruta.client.correspondence;

import java.util.UUID;

import rs.ruta.client.RutaClient;

public class OrderingProcess extends DocumentProcess
{
	/**
	 * Constructs new instance of a {@link OrderingProcess} and sets its state to
	 * default value and id to a random value.
	 * @return {@code OrderingProcess}
	 */
	public static OrderingProcess newInstance(RutaClient client)
	{
		OrderingProcess process = new OrderingProcess();
		process.setState(PrepareCatalogueState.getInstance());//MMM: state should be changed
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		return process;
	}


}
