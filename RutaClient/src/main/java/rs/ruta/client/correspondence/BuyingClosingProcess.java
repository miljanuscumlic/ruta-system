package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

@XmlRootElement(name = "BuyingClosingProcess")
@XmlType(name = "BuyingClosingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class BuyingClosingProcess extends DocumentProcess
{
	/**
	 * Constructs new instance of a {@link BuyingClosingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code BuyingClosingProcess}
	 */
	public static BuyingClosingProcess newInstance(RutaClient client)
	{
		BuyingClosingProcess process = new BuyingClosingProcess();
		process.setState(BuyerPlaceOrderState.getInstance()); //MMM: default state should be changed
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		return process;
	}

}